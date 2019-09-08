package com.custom.library.Adjustment

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_adjustment.*
import kotlinx.android.synthetic.main.activity_adjustment.brightness
import kotlinx.android.synthetic.main.activity_adjustment.sb_value
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import com.custom.library.CropView.BitmapUtils
import com.custom.library.ui.ImageEditActivity


class AdjustmentActivity : AppCompatActivity() {
var editedBitmap: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.custom.library.R.layout.activity_adjustment)

        cancel.setOnClickListener { finish() }

        btn_back.setOnClickListener { finish() }

        val path = Uri.parse(intent.getStringExtra("Path"))

        adjustment_iv.setImageURI(path)

       /* val drawable = adjustment_iv.getDrawable() as BitmapDrawable
        val bitmap = drawable.bitmap*/

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(intent.getStringExtra("Path"), options)
        val displayMetrics = resources.displayMetrics
        options.inSampleSize = calculateInSampleSize(options, displayMetrics.widthPixels, displayMetrics.heightPixels)
        options.inJustDecodeBounds = false
        val bitmap = BitmapFactory.decodeFile(intent.getStringExtra("Path"), options)

        brightness.setOnClickListener {
            sb_value.max = 255
            sb_value.progress = 1
            sb_value.visibility = View.VISIBLE
            sb_value.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    editedBitmap = changeBitmapContrastBrightness(bitmap, 1F, progress.toFloat())
                     adjustment_iv!!.setImageBitmap(editedBitmap)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {

                }
            })

        }

        contrast.setOnClickListener {
            sb_value.max = 10
            sb_value.visibility = View.VISIBLE
            sb_value.progress = 1
            sb_value.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    editedBitmap = changeBitmapContrastBrightness(bitmap, progress.toFloat(), 1F)
                    adjustment_iv!!.setImageBitmap(editedBitmap)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {

                }
            })

        }

        save.setOnClickListener {
            val uri = editedBitmap?.let { it1 -> BitmapUtils.writeTempStateStoreBitmap(this, it1,null) }

            val intent = Intent()
            intent.putExtras(getIntent())
            if (uri != null) {
                intent.putExtra("Path",uri.path)
            }

            setResult(ImageEditActivity.ADJUSTMENT_IMAGE_REQUEST_CODE,intent)
            finish()
        }

    }

    fun changeBitmapContrastBrightness(mBitmap:Bitmap, contrast: Float, brightness: Float): Bitmap? {
        if (mBitmap == null) {
            return null
        }
        val cm = ColorMatrix(floatArrayOf(contrast, 0f, 0f, 0f, brightness, 0f, contrast, 0f, 0f, brightness, 0f, 0f, contrast, 0f, brightness, 0f, 0f, 0f, 1f, 0f))

        val ret = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), mBitmap.getConfig())

        val canvas = Canvas(ret)

        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(cm)
        canvas.drawBitmap(mBitmap, 0f, 0f, paint)
        return ret
    }


    fun setBrightness(progress: Int): PorterDuffColorFilter {
        if (progress >= 100) {
            val value = (progress - 100) * 255 / 100

            return PorterDuffColorFilter(Color.argb(value, 255, 255, 255), PorterDuff.Mode.SRC_OVER)

        } else {
            val value = (100 - progress) * 255 / 100
            return PorterDuffColorFilter(Color.argb(value, 0, 0, 0), PorterDuff.Mode.SRC_ATOP)


        }
    }

    fun reduceResolution(filePath: String, viewWidth: Int, viewHeight: Int): Bitmap {
        var reqHeight = viewHeight
        var reqWidth = viewWidth

        val options = BitmapFactory.Options()

        // First decode with inJustDecodeBounds=true to check dimensions
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, options)

        val viewAspectRatio = 1.0 * viewWidth / viewHeight
        val bitmapAspectRatio = 1.0 * options.outWidth / options.outHeight

        if (bitmapAspectRatio > viewAspectRatio)
            reqHeight = (viewWidth / bitmapAspectRatio).toInt()
        else
            reqWidth = (viewHeight * bitmapAspectRatio).toInt()

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        System.gc()                                        // TODO - remove explicit gc calls
        return BitmapFactory.decodeFile(filePath, options)
    }


    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {

        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and width
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        return inSampleSize
    }

    /*  brightness.setOnClickListener {
      sb_value.max = 255
      sb_value.progress = 125
      sb_value.visibility = View.VISIBLE
      sb_value.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
          override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
               adjustment_iv!!.setImageBitmap(changeBitmapContrastBrightness(bitmap,progress / 100f, 1F))
          }

          override fun onStartTrackingTouch(seekBar: SeekBar) {

          }

          override fun onStopTrackingTouch(seekBar: SeekBar) {

          }
      })

  }*/

}
