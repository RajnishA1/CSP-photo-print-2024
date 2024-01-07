package com.rajnish.photoprints.nowInkotlin.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object CommonFunUtils {


    fun saveImageToGallery(context: Context, bitmap: Bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val displayName = "Compress_${System.currentTimeMillis()}.jpg"
            val mimeType = "image/jpeg"
            val relativePath = Environment.DIRECTORY_PICTURES

            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
                put(MediaStore.Images.Media.MIME_TYPE, mimeType)
                put(MediaStore.Images.Media.RELATIVE_PATH, relativePath)
            }

            val contentResolver = context.contentResolver
            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            uri?.let {
                try {
                    val outputStream = contentResolver.openOutputStream(it)
                    if (outputStream != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        outputStream.close()
                        Toast.makeText(context, "Image saved successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Failed to save Compressed Image", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    Toast.makeText(context, "Failed to save Compressed Image", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // For API level below 29, you can use the previous code to save the image
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "Compress")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.WIDTH, bitmap.width)
                put(MediaStore.Images.Media.HEIGHT, bitmap.height)
            }

            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            uri?.let {
                try {
                    val outputStream: OutputStream? = resolver.openOutputStream(uri)
                    if (outputStream != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        outputStream.close()
                        Toast.makeText(context, "Image saved successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Failed to save Compressed Image", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    Toast.makeText(context, "Failed to save Compressed Image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


     fun getImageUri(context: Context, bitmap: Bitmap): Uri? {
        val imageFile = File(context.cacheDir, "compressed_image.jpg")
        try {
            val outputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", imageFile)
    }

     fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.RGB_565
            BitmapFactory.decodeStream(inputStream, null, options)
        } catch (e: Exception) {
            e.printStackTrace()
            null
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

     fun getBitmapFileSize(bitmap: Bitmap): String {
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

    fun shareImage(context: Context,uri: Uri) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/*"
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        context.startActivity(Intent.createChooser(shareIntent, "Share Image"))
    }
     fun generateRandomFileName(): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val randomString = UUID.randomUUID().toString().substring(0, 6)
        return "PDF_$timeStamp$randomString.pdf"
    }

}
