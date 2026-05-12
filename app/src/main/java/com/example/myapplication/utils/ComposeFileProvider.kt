package com.example.myapplication.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

class ComposeFileProvider : FileProvider() {
    companion object {
        // This function MUST take (context, authority) to work with your screen code
        fun getImageUri(context: Context, authority: String): Uri {
            val directory = File(context.cacheDir, "images")
            directory.mkdirs()
            val file = File.createTempFile("selected_image_", ".jpg", directory)

            return getUriForFile(context, authority, file)
        }
    }
}