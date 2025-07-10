package com.ifpe.urgenciasegura

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object FileUtil {
    fun from(context: Context, uri: Uri): File {
        val mimeType = context.contentResolver.getType(uri) // Ex: "image/jpeg" ou "image/png"
        val extension = when (mimeType) {
            "image/png" -> ".png"
            "image/jpeg" -> ".jpg"
            else -> ".jpg" // padrão de segurança
        }
        val inputStream = context.contentResolver.openInputStream(uri)
        val fileName = "temp_image_${System.currentTimeMillis()}$extension"
        val file = File(context.cacheDir, fileName)
        val outputStream = FileOutputStream(file)

        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()

        return file
    }
}
