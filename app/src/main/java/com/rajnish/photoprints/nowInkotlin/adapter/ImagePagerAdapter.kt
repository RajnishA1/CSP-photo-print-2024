package com.rajnish.photoprints.nowInkotlin.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.chrisbanes.photoview.PhotoView
import com.rajnish.photoprints.R
import java.io.File

class ImagePagerAdapter(
    private val context: Context,
    private val imageUris: List<Uri>
) : RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUri = imageUris[position]
        val imagePath = imageUri.path

        if (imagePath != null) {
            val imageFile = File(imagePath)
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            holder.photoView.setImageBitmap(bitmap)
        }
    }

    override fun getItemCount(): Int {
        return imageUris.size
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoView: PhotoView = itemView.findViewById(R.id.photoView)
    }
}
