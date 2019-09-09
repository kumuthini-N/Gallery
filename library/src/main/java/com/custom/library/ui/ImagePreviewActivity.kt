package com.custom.library.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.custom.library.C
import com.custom.library.R
import com.custom.library.adapter.SmallPreviewAdapter
import com.custom.library.bean.ImageItem
import kotlinx.android.synthetic.main.activity_image_preview.*
import kotlinx.android.synthetic.main.include_top_bar.*
import uk.co.senab.photoview.PhotoViewAttacher


class ImagePreviewActivity : ImagePreviewBaseActivity(), View.OnClickListener, PhotoViewAttacher.OnPhotoTapListener {

    private lateinit var imageItems: ArrayList<ImageItem>
    private var current: Int = 0
    private var previewAdapter: SmallPreviewAdapter = SmallPreviewAdapter(this, pickHelper.selectedImages)

    companion object {

        const val IMAGE_PREVIEW_REQUEST_CODE = 601

        fun startForResult(activity: Activity, requestCode: Int, position: Int, imageItems: ArrayList<ImageItem>) {
            val intent = Intent(activity, ImagePreviewActivity::class.java)
            intent.putExtra(C.EXTRA_IMAGE_ITEMS, imageItems)
            intent.putExtra(C.EXTRA_POSITION, position)
            activity.startActivityForResult(intent, requestCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageItems = intent.extras[C.EXTRA_IMAGE_ITEMS] as ArrayList<ImageItem>
        current = intent.extras[C.EXTRA_POSITION] as Int
        init()
    }

    private fun init() {

        edit.visibility = View.VISIBLE
        btn_ok.setOnClickListener(this)
        crop.setOnClickListener(this)
        edit.setOnClickListener {

            val intent = Intent(this, ImageEditActivity::class.java)
            intent.putExtra("Path", imageItems[current].path!!)
            intent.putExtra("position", current)
            startActivityForResult(intent,IMAGE_PREVIEW_REQUEST_CODE)
        }
        bottom_bar.visibility = View.VISIBLE

        tv_des.text = getString(R.string.ip_preview_image_count, current + 1, imageItems.size)
        viewpager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                current = position
                val imageItem = imageItems[position]
                val contains = pickHelper.selectedImages.contains(imageItem)
                cb_check.isChecked = contains
                tv_des.text = getString(R.string.ip_preview_image_count, position + 1, imageItems.size)
                updatePreview()
            }
        })

        imagePagerAdapter.setData(imageItems)
        viewpager.currentItem = current

        onCheckChanged(pickHelper.selectedImages.size, pickHelper.limit)

        cb_check.isChecked = pickHelper.selectedImages.contains(imageItems[current])
        cb_check.setOnClickListener {
            //cbCheck.isChecked = !cbCheck.isChecked
            val imageItem = imageItems[current]
            if (cb_check.isChecked) {
                if (pickHelper.canSelect()) {
                    pickHelper.selectedImages.add(imageItem)
                } else {
                    showToast(getString(R.string.ip_select_limit, pickHelper.limit))
                    cb_check.isChecked = false
                }
                previewAdapter.notifyItemInserted(pickHelper.selectedImages.size - 1)
            } else {
                val index = pickHelper.selectedImages.indexOf(imageItem)
                pickHelper.selectedImages.remove(imageItem)
                previewAdapter.notifyItemRemoved(index)
            }
            onCheckChanged(pickHelper.selectedImages.size, pickHelper.limit)
            updatePreview()
        }

        rv_small.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        previewAdapter.listener = object : SmallPreviewAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, imageItem: ImageItem) {
                viewpager.setCurrentItem(imageItems.indexOf(imageItem), false)
            }
        }
        rv_small.adapter = previewAdapter
        updatePreview()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PREVIEW_REQUEST_CODE) {

            if (data != null){
               val path = data.getStringExtra("Path")
                val position = data.getIntExtra("position",0)
                pickHelper.selectedImages[position].path = path
                imageItems[position].path = path
                init()
            }
        }
    }


    private fun updatePreview() {
        if (pickHelper.selectedImages.size > 0) {
            rv_small.visibility = View.VISIBLE
            val index = pickHelper.selectedImages.indexOf(imageItems[current])
            previewAdapter.current = if (index >= 0) pickHelper.selectedImages[index] else null
            if (index >= 0) {
                rv_small.smoothScrollToPosition(index)
            }
        } else {
            rv_small.visibility = View.GONE
        }
    }

    private fun onCheckChanged(selected: Int, limit: Int) {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_ok -> {
                setResult()
            }
        }
    }

    private fun setResult() {
        val result = intent
        result.putExtra(C.EXTRA_IMAGE_ITEMS, pickHelper.selectedImages)
        setResult(Activity.RESULT_OK, result)
        finish()
    }


    override fun onOutsidePhotoTap() {
        changeTopAndBottomBar()
    }

    override fun onPhotoTap(view: View?, x: Float, y: Float) {
        changeTopAndBottomBar()
    }

    private fun changeTopAndBottomBar() {
        if (top_bar.visibility == View.VISIBLE) {
            top_bar.animation = AnimationUtils.loadAnimation(this, R.anim.top_out)
            bottom_bar.animation = AnimationUtils.loadAnimation(this, R.anim.fade_out)
            top_bar.visibility = View.GONE
            bottom_bar.visibility = View.GONE
        } else {
            top_bar.animation = AnimationUtils.loadAnimation(this, R.anim.top_in)
            bottom_bar.animation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
            top_bar.visibility = View.VISIBLE
            bottom_bar.visibility = View.VISIBLE
        }
    }
}
