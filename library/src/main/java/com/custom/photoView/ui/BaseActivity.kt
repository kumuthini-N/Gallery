package com.custom.photoView.ui

import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


open class BaseActivity : AppCompatActivity() {
    fun showToast(charSequence: CharSequence) {
        Toast.makeText(this, charSequence, Toast.LENGTH_SHORT).show()
    }

    fun hideStatusBar(){
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}