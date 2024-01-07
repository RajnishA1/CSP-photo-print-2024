package com.rajnish.photoprints.nowInkotlin


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import com.rajnish.photoprints.R
import com.rajnish.photoprints.databinding.ActivityImageResizeBinding
import com.rajnish.photoprints.BuyCoins
import com.rajnish.photoprints.nowInkotlin.utils.CommonFunUtils
import com.rajnish.photoprints.nowInkotlin.utils.CommonFunUtils.saveImageToGallery
import com.rajnish.photoprints.nowInkotlin.utils.InternetUtils
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.IOException


class ImageResize : AppCompatActivity() ,OnUserEarnedRewardListener {

    private lateinit var binding: ActivityImageResizeBinding
    var mrewardedInterstitialAd: RewardedInterstitialAd? = null
    // Inside your activity or fragment


    lateinit var bitmap: Bitmap
    lateinit var compressBitmap: Bitmap
    private lateinit var resizeImageBitmap: Bitmap
    private lateinit var selectedImageUri: Uri
    var a = 0
    var isSeekbarMove = false
    private var dialog: Dialog? = null
    private var afterOnePrint = false
    private var requestFor = "save"

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intent = result.data

                if (intent != null) {
                    startImageCropping(intent.data!!)
                }
            }
        }


    @SuppressLint("SetTextI18n")
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageResizeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.seekBars.max = 100
        binding.seekBars.progress = 100
        dialog = Dialog(this)

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
                    compressBitmap = CommonFunUtils.compressBitmap(bitmap, progress)

                    withContext(Dispatchers.Main) {
                        binding.ivSelectedImage.setImageBitmap(compressBitmap)
                        binding.textOnImage.text = CommonFunUtils.getBitmapFileSize(compressBitmap)
                        isSeekbarMove = true
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

        binding.btnCreate.setOnClickListener {

            if (!TextUtils.isEmpty(binding.etWidth.text) && !TextUtils.isEmpty(
                    binding.etWidth.text)
            ) {



                val width = binding.etWidth.text.toString().toInt()
                val height = binding.etHeight.text.toString().toInt()
                 resizeImageBitmap =
                    resizeImage(
                        CommonFunUtils.uriToBitmap(this, selectedImageUri)!!,
                        width,
                        height,

                    )
                binding.ivSelectedImage.setImageBitmap(resizeImageBitmap)
                val uri = CommonFunUtils.getImageUri(this, resizeImageBitmap)

                val dimensions = uri?.let { it1 -> getImageDimensionsAndDpi(it1) }
                showCustomDialog(
                    uri!!,
                    dimensions!!.first,
                    dimensions.second,
                )

            }else{
                Toast.makeText(this, "Invalid Input", Toast.LENGTH_SHORT).show()
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
                selectedImageUri = result.uri
                val dimensions  =  getImageDimensionsAndDpi(selectedImageUri)
                binding.etWidth.setText(dimensions .first.toString())
                binding.etHeight.setText(dimensions .second.toString())
                binding.ivSelectedImage.setImageURI(selectedImageUri)
                bitmap = CommonFunUtils.uriToBitmap(this, selectedImageUri)!!
                binding.textOnImage.text = CommonFunUtils.getBitmapFileSize(bitmap)
                binding.tvUploadImage.text = "Change Image"
                binding.textOnImage.text = CommonFunUtils.getBitmapFileSize(bitmap)
                binding.editContainer.visibility = View.VISIBLE


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                var error: Exception? = null
                if (result != null) {
                    error = result.error
                }
                Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun startImageCropping(imageUri: Uri) {
        CropImage.activity(imageUri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAutoZoomEnabled(true)
            .start(this)
    }

    private fun resizeImage(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        val qualityBitmap = resizedBitmap.copy(Bitmap.Config.ARGB_8888, true)
        resizedBitmap.recycle()
        return qualityBitmap
    }

    private fun getImageDimensionsAndDpi(imageUri: Uri): Pair<Int, Int> {
        var width = -1
        var height = -1

        try {
            val contentResolver: ContentResolver = contentResolver
            contentResolver.openInputStream(imageUri)?.use { inputStream ->
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }

                BitmapFactory.decodeStream(inputStream, null, options)
                width = options.outWidth
                height = options.outHeight
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return Pair(width, height)
    }

    @SuppressLint("SetTextI18n")
    private fun showCustomDialog(uri: Uri, w:Int, h:Int) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.image_resize_dialog_layout)

        val imageView: ImageView = dialog.findViewById(R.id.imageView)
        val textView: TextView = dialog.findViewById(R.id.textView)
        val btnSave: Button = dialog.findViewById(R.id.btnSave)
        val btnShare: Button = dialog.findViewById(R.id.btnShare)
        val imgCross: ImageView = dialog.findViewById(R.id.imgCross)

        imageView.setImageURI(uri)
        textView.text = "Image \n$w width \n$h height"

        btnSave.setOnClickListener {


                payment("save", uri)

                dialog.dismiss()

        }
        imgCross.setOnClickListener {
            requestFor = "save"
            dialog.dismiss()
        }

        btnShare.setOnClickListener {


                requestFor = "share"
                payment("share", uri)

        }

        dialog.show()
    }

    private fun payment(doFor:String,uri: Uri){
        if (InternetUtils.isInternetConnected(this)){
            if ( a > 0) {
                val sharedPreferences = getSharedPreferences("Coins", 0)
                val myEdit = sharedPreferences.edit()
                myEdit.putInt("Points", a - 5)
                myEdit.apply()
                if (doFor == "save"){
                    saveImageToGallery(this, bitmap)
                }
                else{
                    CommonFunUtils.shareImage(this, uri)
                }

            }else{
                if (afterOnePrint){
                    if (doFor == "save"){
                        saveImageToGallery(this, bitmap)
                    }else{
                        saveImageToGallery(this,bitmap)
                    }

                }else{
                    lowCoinsDialog()
                }
            }
        }
        else {
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
                mrewardedInterstitialAd!!.show(this@ImageResize, this@ImageResize)
                mrewardedInterstitialAd!!.fullScreenContentCallback = object :
                    FullScreenContentCallback() {}
            }
        }
        buycoin.setOnClickListener {
            val intent = Intent(this@ImageResize, BuyCoins::class.java)
            startActivity(intent)
        }
    }

    override fun onUserEarnedReward(p0: RewardItem) {
        dialog!!.dismiss()

          if (requestFor == "save"){
              saveImageToGallery(this,resizeImageBitmap)
          }else{
              CommonFunUtils.getImageUri(this,resizeImageBitmap)?.let { CommonFunUtils.shareImage(this, it) }
          }

        afterOnePrint = true
        mrewardedInterstitialAd = null
    }

}