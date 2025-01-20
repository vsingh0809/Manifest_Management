package com.sko.manifestmanagement.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64




object BitmapUtils {
    //    fun decodeBase64ToBitmap(base64Str: String): Bitmap? {
//        return try {
//            val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
//            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
//        } catch (e: IllegalArgumentException) {
//            e.printStackTrace()
//            null
//        }
//    }
//}

    fun encodeBitmapToBase64(bitmap: Bitmap): String {
        return try {
            val outputStream = java.io.ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            val byteArray = outputStream.toByteArray()
            Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            ""

        }

    }

}

