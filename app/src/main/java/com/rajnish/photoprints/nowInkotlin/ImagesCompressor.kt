package com.rajnish.photoprints.nowInkotlin

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import com.rajnish.photoprints.R
import com.rajnish.photoprints.databinding.ActivityImagesCompressorBinding
import com.rajnish.photoprints.BuyCoins
import com.rajnish.photoprints.nowInkotlin.utils.CommonFunUtils
import com.rajnish.photoprints.nowInkotlin.utils.InternetUtils
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class ImagesCompressor : AppCompatActivity(), OnUserEarnedRewardListener {

    private lateinit var binding: ActivityImagesCompressorBinding
    var mrewardedInterstitialAd: RewardedInterstitialAd? = null


    lateinit var bitmap: Bitmap
    lateinit var compressBitmapImage: Bitmap
    var a = 0
    private var dialog: Dialog? = null
    private var afterOnePrint = false
    private var requestFor = "save"

    private var isSeekBarMove = false


    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data

                if (intent != null) {
                    startImageCropping(intent.data!!)
                }


            }
        }


    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImagesCompressorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog = Dialog(this)
        binding.seekBars.max = 100
        binding.seekBars.progress = 100

        val shs = getSharedPreferences("Coins", 0)
        a = shs.getInt("Points", 10)
        if (a < 5) {
            loadAd()
        }


        binding.tvUploadImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }

        binding.seekBars.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {


                GlobalScope.launch(Dispatchers.IO) {
                    compressBitmapImage = compressBitmap(bitmap, progress)

                    withContext(Dispatchers.Main) {
                        binding.ivSelectedImage.setImageBitmap(compressBitmapImage)
                        binding.tvImageSize.text = getBitmapFileSize(compressBitmapImage)
                        isSeekBarMove = true

                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Not needed for this example
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Not needed for this example
            }
        })

        binding.btnShare.setOnClickListener {
            if (isSeekBarMove) {
                val compressedImageUri = CommonFunUtils.getImageUri(this, compressBitmapImage)
                if (compressedImageUri != null && isSeekBarMove) {
                    requestFor = "share"
                    payment("share", compressedImageUri)

                }
            } else {
                Toast.makeText(this, "Adjust seekbar to reduce image quality.", Toast.LENGTH_SHORT)
                    .show()
            }

        }
        binding.btnSave.setOnClickListener {
            if (isSeekBarMove) {
                val compressedImageUri = CommonFunUtils.getImageUri(this, compressBitmapImage)

                requestFor = "save"
                if (compressedImageUri != null) {
                    payment("save", compressedImageUri)
                }

            } else {
                Toast.makeText(this, "Adjust seekbar to reduce image quality.", Toast.LENGTH_SHORT)
                    .show()
            }

        }


    }

    @SuppressLint("SetTextI18n")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                assert(result != null)
                val selectedImageUri: Uri? = result.uri
                binding.ivSelectedImage.setImageURI(selectedImageUri)
                bitmap = CommonFunUtils.uriToBitmap(this, selectedImageUri!!)!!
                binding.tvImageSize.text = getBitmapFileSize(bitmap)
                binding.tvUploadImage.text = "Change Image"
                binding.seekBars.visibility = View.VISIBLE
                binding.tvImageSize.visibility = View.VISIBLE
                binding.btnSave.visibility = View.VISIBLE
                binding.btnShare.visibility = View.VISIBLE
                binding.textOnImage.text = getBitmapFileSize(bitmap)


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                var error: java.lang.Exception? = null
                if (result != null) {
                    error = result.error
                }
                Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun compressBitmap(bitmap: Bitmap, quality: Int): Bitmap {
        // Calculate the target dimensions for the compressed bitmap
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height
        val maxDimension = 1024 // Define the maximum dimension (either width or height)
        val scaleFactor =
            (maxDimension.toFloat() / originalWidth).coerceAtMost(maxDimension.toFloat() / originalHeight)
        val targetWidth = (originalWidth * scaleFactor).toInt()
        val targetHeight = (originalHeight * scaleFactor).toInt()

        // Resize the bitmap
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false)

        // Compress the resized bitmap
        val outputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val compressedByteArray = outputStream.toByteArray()

        // Decode the compressed bitmap
        return BitmapFactory.decodeByteArray(compressedByteArray, 0, compressedByteArray.size)
    }


    private fun getBitmapFileSize(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val sizeInBytes = stream.toByteArray().size.toDouble()
        val sizeInKB = sizeInBytes / 1024
        val sizeInMB = sizeInKB / 1024

        return when {
            sizeInMB >= 1 -> "%.2f MB".format(sizeInMB)
            sizeInKB >= 1 -> "%.2f KB".format(sizeInKB)
            else -> "%.2f Bytes".format(sizeInBytes)
        }
    }

    private fun startImageCropping(imageUri: Uri) {
        CropImage.activity(imageUri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAutoZoomEnabled(true)
            .start(this)
    }


    private fun payment(doFor: String, uri: Uri) {
        if (InternetUtils.isInternetConnected(this)) {
            if (a > 0) {
                val sharedPreferences = getSharedPreferences("Coins", 0)
                val myEdit = sharedPreferences.edit()
                myEdit.putInt("Points", a - 5)
                myEdit.apply()
                if (doFor == "save") {
                    CommonFunUtils.saveImageToGallery(this, compressBitmapImage)
                } else {
                    CommonFunUtils.shareImage(this, uri)
                }

            } else {
                if (afterOnePrint) {
                    CommonFunUtils.saveImageToGallery(this, compressBitmapImage)
                } else {
                    lowCoinsDialog()
                }
            }
        } else {
            Toast.makeText(this, "No Internet Connected", Toast.LENGTH_SHORT).show()
        }
    }


    private fun loadAd() {
        RewardedInterstitialAd.load(this, "ca-app-pub-1002841467878412/7180384425",
            AdRequest.Builder().build(), object : RewardedInterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedInterstitialAd) {
                    super.onAdLoaded(ad)
                    mrewardedInterstitialAd = ad
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    loadAd()
                }
            })
    }

    private fun lowCoinsDialog() {
        dialog?.setContentView(R.layout.low_blance_dialog)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val close: ImageView = dialog!!.findViewById(R.id.close_image_btn)
        val progressBarD: ProgressBar = dialog!!.findViewById(R.id.progressBar)
        val watchAds: TextView = dialog!!.findViewById(R.id.watch_ads)
        val buycoin: Button = dialog!!.findViewById(R.id.buy_coin)
        dialog!!.show()
        close.setOnClickListener { dialog!!.dismiss() }
        watchAds.setOnClickListener {
            progressBarD.visibility = View.VISIBLE
            watchAds.visibility = View.INVISIBLE
            if (mrewardedInterstitialAd != null) {
                mrewardedInterstitialAd!!.show(this@ImagesCompressor, this@ImagesCompressor)
                mrewardedInterstitialAd!!.fullScreenContentCallback = object :
                    FullScreenContentCallback() {}
            }
        }
        buycoin.setOnClickListener {
            val intent = Intent(this@ImagesCompressor, BuyCoins::class.java)
            startActivity(intent)
        }
    }

    override fun onUserEarnedReward(p0: RewardItem) {
        dialog!!.dismiss()

        if (requestFor == "save") {
            CommonFunUtils.saveImageToGallery(this, compressBitmapImage)
        } else {
            CommonFunUtils.getImageUri(this, compressBitmapImage)
                ?.let { CommonFunUtils.shareImage(this, it) }
        }

        afterOnePrint = true
        mrewardedInterstitialAd = null
    }

}
