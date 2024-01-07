package com.rajnish.photoprints.nowInkotlin

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.github.barteksc.pdfviewer.PDFView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import com.rajnish.photoprints.R
import com.rajnish.photoprints.databinding.ActivityJpgtoPdfBinding
import com.rajnish.photoprints.BuyCoins
import com.rajnish.photoprints.nowInkotlin.utils.CommonFunUtils
import com.rajnish.photoprints.nowInkotlin.utils.InternetUtils
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject
import gun0912.tedimagepicker.builder.TedImagePicker
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


class JPGToPDF : AppCompatActivity(), OnUserEarnedRewardListener {
    private lateinit var binding: ActivityJpgtoPdfBinding
    var mrewardedInterstitialAd: RewardedInterstitialAd? = null
    private var progressDialog: Dialog? = null
    private lateinit var progressText: TextView
    private var totalImages: Int = 0
    private var convertedImages: Int = 0
    private lateinit var pdfFilePath: File


    var a = 0
    private var dialog: Dialog? = null
    private var afterOnePrint = false
    private var requestFor = "save"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJpgtoPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialog = Dialog(this)

        val shs = getSharedPreferences("Coins", 0)
        a = shs.getInt("Points", 10)
        if (a < 5) {
            loadAd()
        }

        progressDialog = Dialog(this)
        progressDialog?.setContentView(R.layout.custom_progress_dialog)
        progressDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        progressText = progressDialog?.findViewById(R.id.progressText) ?: return


        binding.apply {
            chooseImageLayout.setOnClickListener {
                selectMultipleImages()
            }
        }
    }

    private fun selectMultipleImages() {
        TedImagePicker.with(this)
            .startMultiImage { uriList ->
                if (uriList.isNotEmpty()) {
                    val selectedImages: ArrayList<Uri> = ArrayList(uriList)
                    convertImagesToPDF(selectedImages)
                }
            }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun convertImagesToPDF(imageUris: List<Uri>) {
        val outputDir = getOutputDir() // Directory to save the PDF file
        pdfFilePath = File(outputDir, CommonFunUtils.generateRandomFileName())

        // Show the progress dialog and initialize progress variables
        showProgressDialog("Converting images to PDF... 0/${imageUris.size}")
        totalImages = imageUris.size
        convertedImages = 0

        // Start a coroutine
        GlobalScope.launch {
            try {
                val document = PDDocument()
                for (imageUri in imageUris) {
                    val imageBitmap = withContext(Dispatchers.IO) {
                        // Load the bitmap in a blocking context (IO dispatcher)
                        val imageStream = contentResolver.openInputStream(imageUri)
                        BitmapFactory.decodeStream(imageStream)
                    }

                    val page = PDPage(PDRectangle.A4)
                    document.addPage(page)

                    val imageXObject = PDImageXObject.createFromByteArray(
                        document,
                        imageBitmapToByteArray(imageBitmap),
                        null
                    )
                    val contentStream = PDPageContentStream(document, page)
                    contentStream.drawImage(
                        imageXObject,
                        0f,
                        0f,
                        PDRectangle.A4.width,
                        PDRectangle.A4.height
                    )
                    contentStream.close()

                    // Update the progress on the main/UI thread
                    withContext(Dispatchers.Main) {
                        convertedImages++
                        updateProgress()
                    }
                }

                document.save(pdfFilePath)
                document.close()

                // Dismiss the progress dialog on the main/UI thread
                withContext(Dispatchers.Main) {
                    progressDialog?.dismiss()
                    showCustomDialog(pdfFilePath)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    progressDialog?.dismiss()
                    Toast.makeText(
                        this@JPGToPDF,
                        "Failed to convert images to PDF",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun showCustomDialog(pdfFile: File) {
        dialog?.dismiss()

        val customDialog = Dialog(this)
        customDialog.setContentView(R.layout.custom_pdf_dialog)

        val pdfView: PDFView = customDialog.findViewById(R.id.pdfViewDialog)
        val btnSavePDF = customDialog.findViewById<Button>(R.id.btnSavePDF)
        val btnSharePDF = customDialog.findViewById<Button>(R.id.btnSharePDF)
        val iconCross = customDialog.findViewById<ImageView>(R.id.iconCross)
        val tvPageCount = customDialog.findViewById<TextView>(R.id.tvPageCount)
        val tvPdfName = customDialog.findViewById<TextView>(R.id.tvPdfName)


        pdfView.fromFile(pdfFile)
            .defaultPage(0)
            .enableSwipe(true)
            .swipeHorizontal(false)
            .onLoad { numPages ->
                // Set the page count when the PDF is loaded
                tvPageCount.text = "Page Count: $numPages"
            }
            .load()

        // Set the PDF name
        tvPdfName.text = "PDF Name: ${pdfFile.name}"

        btnSavePDF.setOnClickListener {
            requestFor = "save"
            payment(pdfFile)
            customDialog.dismiss()

        }
        iconCross.setOnClickListener {
            afterOnePrint = false
            customDialog.dismiss()
        }

        btnSharePDF.setOnClickListener {
            requestFor = "share"
            payment(pdfFile)

        }

        customDialog.show()
    }

    private fun payment(pdfFile: File) {
        if (InternetUtils.isInternetConnected(this)) {
            if (a > 0) {
                val sharedPreferences = getSharedPreferences("Coins", 0)
                val myEdit = sharedPreferences.edit()
                myEdit.putInt("Points", a - 5)
                myEdit.apply()

                if (requestFor == "save") {
                    savePDF(pdfFile)
                } else {
                    sharePDF(pdfFile)
                }


            } else {
                if (afterOnePrint) {
                    if (requestFor == "save") {
                        savePDF(pdfFile)
                    } else {
                        sharePDF(pdfFile)
                    }

                } else {
                    lowCoinsDialog()
                }
            }
        } else {
            Toast.makeText(this, "No Internet Connected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun savePDF(pdfFile: File) {
        val displayName = CommonFunUtils.generateRandomFileName()
        val mimeType = "application/pdf"

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val resolver: ContentResolver = applicationContext.contentResolver

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For API level 29 and above, use the IS_PENDING flag to mark the file as complete
            values.put(MediaStore.MediaColumns.IS_PENDING, 1)

            // Insert the file into MediaStore
            val uri = resolver.insert(MediaStore.Files.getContentUri("external"), values)

            try {
                uri?.let { it ->
                    // Save the PDF file using the output stream
                    val outputStream = resolver.openOutputStream(it)
                    val inputStream = FileInputStream(pdfFile)
                    val buffer = ByteArray(1024)
                    var read: Int

                    while (inputStream.read(buffer).also { read = it } != -1) {
                        outputStream?.write(buffer, 0, read)
                    }

                    outputStream?.flush()
                    outputStream?.close()
                    inputStream.close()

                    // Update the IS_PENDING flag to mark the file as complete
                    values.clear()
                    values.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(uri, values, null, null)
                    afterOnePrint = false
                    // Show a success message
                    Toast.makeText(this, "PDF saved successfully", Toast.LENGTH_SHORT).show()
                } ?: run {
                    // Handle the case when the uri is null (file insertion failed)
                    Toast.makeText(this, "Failed to save PDF", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                // Show an error message if saving fails
                Toast.makeText(this, "Failed to save PDF", Toast.LENGTH_SHORT).show()
            }
        } else {
            // For API level 28, use FileOutputStream to save the PDF
            try {
                val downloadsDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, "$displayName.pdf")
                val outputStream = FileOutputStream(file)

                val inputStream = FileInputStream(pdfFile)
                val buffer = ByteArray(1024)
                var read: Int

                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                }

                outputStream.flush()
                outputStream.close()
                inputStream.close()

                // Notify the system about the new file
                MediaScannerConnection.scanFile(this, arrayOf(file.path), arrayOf(mimeType), null)

                afterOnePrint = false
                // Show a success message
                Toast.makeText(this, "PDF saved successfully", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
                // Show an error message if saving fails
                Toast.makeText(this, "Failed to save PDF", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun imageBitmapToByteArray(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    private fun getOutputDir(): File {
        val dir =
            File(getExternalFilesDir(null), "PDFs") // Change "PDFs" to your desired directory name
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    private fun sharePDF(pdfFile: File) {
        val pdfUri =
            FileProvider.getUriForFile(this, "com.rajnish.photoprints.fileprovider", pdfFile)

        // Create the intent to share the PDF
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "application/pdf"
        shareIntent.putExtra(Intent.EXTRA_STREAM, pdfUri)

        try {
            startActivity(Intent.createChooser(shareIntent, "Share PDF with"))
        } catch (e: ActivityNotFoundException) {
            // Handle the case when no app on the device can handle sharing
            Toast.makeText(this, "No app found to share PDF", Toast.LENGTH_SHORT).show()
        }
    }


    @SuppressLint("SetTextI18n")
    private fun updateProgress() {
        progressText.text = "Converting images to PDF... $convertedImages/$totalImages"
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissProgressDialog()
    }

    private fun showProgressDialog(message: String) {
        progressText.text = message
        progressDialog?.show()
    }

    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
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
                mrewardedInterstitialAd!!.show(this@JPGToPDF, this@JPGToPDF)
                mrewardedInterstitialAd!!.fullScreenContentCallback = object :
                    FullScreenContentCallback() {}
            }
        }
        buycoin.setOnClickListener {
            val intent = Intent(this@JPGToPDF, BuyCoins::class.java)
            startActivity(intent)
        }
    }

    override fun onUserEarnedReward(p0: RewardItem) {
        dialog!!.dismiss()

        if (requestFor == "save") {
            savePDF(pdfFilePath)
        } else {
            sharePDF(pdfFilePath)
            afterOnePrint = true
        }

        mrewardedInterstitialAd = null
    }


}
