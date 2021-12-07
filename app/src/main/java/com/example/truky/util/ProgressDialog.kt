package com.example.truky.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build.VERSION
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import coil.ImageLoader
import coil.api.load
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.truky.R
import com.example.truky.databinding.LayoutDialogBinding

class ProgressDialog(context: Context) : Dialog(context) {

    private lateinit var binding: LayoutDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCancelable(false)
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.layout_dialog,
            null, false
        )
        setContentView(binding.root)
        loadGif()
    }

    private fun loadGif() {
        val imageLoader = ImageLoader.Builder(context)
            .componentRegistry {
                if (VERSION.SDK_INT >= 28) add(ImageDecoderDecoder()) else add(GifDecoder())
            }
            .build()
        binding.ivProgress.load(imageLoader = imageLoader, uri = PROGRESS_LOADER_URL)
    }

    companion object {
        const val PROGRESS_LOADER_URL = "file:///android_asset/loading.gif"
    }
}