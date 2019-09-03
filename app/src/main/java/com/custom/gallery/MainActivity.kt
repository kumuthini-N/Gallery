package com.custom.gallery

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.custom.library.Edit.CropImage
import com.custom.library.ImagePicker
import com.custom.library.bean.ImageItem
import com.custom.library.util.Utils
import com.custom.library.view.GridSpacingItemDecoration
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), ImagePicker.OnPickImageResultListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ImagePicker.defaultConfig()
        btn_camera.visibility = View.GONE
//        ImagePicker.limit(12);
        cb_crop.setOnCheckedChangeListener({ _, isChecked -> ImagePicker.isCrop(isChecked) })
        cb_multi.isChecked = true
        cb_multi.setOnCheckedChangeListener { _, isChecked -> ImagePicker.multiMode(isChecked) }
        ImagePicker.multiMode(true)
        btn_pick.setOnClickListener {
            ImagePicker.pick(this@MainActivity, this@MainActivity)
        }
        btn_camera.setOnClickListener {
            CropImage.activity(null).setGuidelines(com.custom.library.Edit.CropImageView.Guidelines.ON).setMaxZoom(3).start(this)

            // ImagePicker.camera(this@MainActivity, this@MainActivity)
        }
        recycler_view.layoutManager = GridLayoutManager(this, 3)
        val imageAdapter = ImageAdapter(ArrayList())
        imageAdapter.listener = object : ImageAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                ImagePicker.review(this@MainActivity, position, this@MainActivity)
            }
        }
        recycler_view.addItemDecoration(GridSpacingItemDecoration(3, Utils.dp2px(this, 2f), false))
        recycler_view.adapter = imageAdapter
    }

    override fun onImageResult(imageItems: ArrayList<ImageItem>) {
        (recycler_view.adapter as ImageAdapter).updateData(imageItems)
    }

    override fun onDestroy() {
        super.onDestroy()
        ImagePicker.clear()
    }
}