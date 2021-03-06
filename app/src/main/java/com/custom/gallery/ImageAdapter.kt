package com.custom.gallery

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.custom.photoView.bean.ImageItem
import java.io.File


class ImageAdapter constructor(
        private var images: List<ImageItem>
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    var listener: OnItemClickListener? = null

    fun updateData(images: List<ImageItem>) {
        this.images = images
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_image, parent, false))
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder?.imageView?.setOnClickListener({
            listener?.onItemClick(position)
        })
        holder?.imageView?.context?.let {
            App.picInstance.load(Uri.fromFile(File(images[position].path))).fit().centerCrop().placeholder(com.custom.photoView.R.drawable.ic_default_image).into(holder?.imageView)
        }

    }

    override fun getItemCount(): Int {
        return images.size
    }

    inner class ImageViewHolder constructor(var rootView: View) : RecyclerView.ViewHolder(rootView) {
        val imageView: ImageView = rootView.findViewById(R.id.iv) as ImageView
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}