package com.custom.photoView.adapter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.custom.photoView.ImagePicker
import com.custom.photoView.bean.ImageItem
import com.custom.photoView.photoTapListener.PhotoView
import com.custom.photoView.util.Utils

import java.util.*

class ImagePagerAdapter(
        private val mActivity: Activity,
        private var images: ArrayList<ImageItem> = ArrayList()
) : PagerAdapter()  {


    private val screenWidth: Int
    private val screenHeight: Int
    var listener: com.custom.photoView.photoTapListener.PhotoViewAttacher.OnPhotoTapListener? = null

    init {
        val dm = Utils.getScreenPix(mActivity)
        screenWidth = dm.widthPixels
        screenHeight = dm.heightPixels
    }

    fun setData(images: ArrayList<ImageItem>) {
        this.images = images
        notifyDataSetChanged()
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val photoView = PhotoView(mActivity)
        //val photoView = CropImageView(mActivity)
        val imageItem = images[position]
        imageItem.path?.let { ImagePicker.imageLoader?.displayImagePreview(mActivity, it, photoView, screenWidth, screenHeight) }
        listener?.let {
            photoView.setOnPhotoTapListener(it)
        }
        container.addView(photoView)
        return photoView
    }

    override fun getCount(): Int = images.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean = view === `object`

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getItemPosition(`object`: Any): Int = PagerAdapter.POSITION_NONE


/*
    override fun getItemPosition(`object`: Any?): Int = PagerAdapter.POSITION_NONE
*/

}