package com.custom.library.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.custom.library.C
import com.custom.library.Edit.CropImage
import com.custom.library.Edit.CropImageOptions
import com.custom.library.R
import com.custom.library.adapter.SmallPreviewAdapter
import com.custom.library.bean.ImageItem
import kotlinx.android.synthetic.main.activity_image_preview.*
import kotlinx.android.synthetic.main.include_top_bar.*
import uk.co.senab.photoview.PhotoViewAttacher

/**
 * Created by hubert
 *
 * Created on 2017/10/19.
 */
class ImagePreviewActivity : ImagePreviewBaseActivity(), View.OnClickListener, PhotoViewAttacher.OnPhotoTapListener {

    private lateinit var imageItems: ArrayList<ImageItem>
    private var current: Int = 0
    private var previewAdapter: SmallPreviewAdapter = SmallPreviewAdapter(this, pickHelper.selectedImages)

    companion object {

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
        btn_ok.setOnClickListener(this)
        crop.setOnClickListener(this)
        edit.setOnClickListener {
           // CropImage.activity(null).setGuidelines(CropImageView.Guidelines.ON).setMaxZoom(3).start(this)


           val intent = Intent()
            intent.setClass(this, CropActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(CropImage.CROP_IMAGE_EXTRA_OPTIONS, CropImageOptions())
            intent.putExtra(CropImage.CROP_IMAGE_EXTRA_BUNDLE, bundle)
            intent.putExtra("Path", imageItems[current].path!!)
            startActivity(intent)

           /* val mSource = Uri.parse(imageItems[current].path)
            val intent = Intent()
            intent.setClass(this, CropImageActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(CropImage.CROP_IMAGE_EXTRA_SOURCE, mSource)
            bundle.putParcelable(CropImage.CROP_IMAGE_EXTRA_OPTIONS, CropImageOptions())
            intent.putExtra(CropImage.CROP_IMAGE_EXTRA_BUNDLE, bundle)
            startActivity(intent)*/
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
            //checkBox 点击时会自动处理isCheck的状态转变，也就是说如果是选中状态，点击触发OnclickListener时，isCheck已经变成false了
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
        if (selected == 0) {
            btn_ok.isEnabled = false
            btn_ok.text = getString(R.string.ip_complete)
            btn_ok.setTextColor(resources.getColor(R.color.ip_text_secondary_inverted))
        } else {
            btn_ok.isEnabled = true
            btn_ok.text = getString(R.string.ip_select_complete, selected, limit)
            btn_ok.setTextColor(resources.getColor(R.color.ip_text_primary_inverted))
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_ok -> {
                setResult(Activity.RESULT_OK)
                finish()
            }
            R.id.crop -> {
               // CropImageActivity().cropImage()

                //CropImage.activity(null).setGuidelines(com.huburt.library.Crop.CropImageView.Guidelines.ON).setMaxZoom(3).start(this)
                 //ImageCropActivity.start(this, ImageGridActivity.REQUEST_CROP)
            }
        }
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