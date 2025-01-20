package com.sko.manifestmanagement

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.gson.GsonBuilder
import com.sko.manifestmanagement.Database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class ExportDataHelper(private val context: Context) {

    suspend fun exportDataToFile() {
        if (checkAndRequestPermissions()) {
            val appDatabase = AppDatabase.getInstance(context)
            val guests = appDatabase.guestDao().getAllGuests()

            val gson = GsonBuilder().setPrettyPrinting().create()
            val jsonData = gson.toJson(guests)

            saveToDownloadsFolder(jsonData)
        }
    }

    private fun checkAndRequestPermissions(): Boolean {
        val permissions = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else {
            emptyArray() // No permissions needed for Android 10+
        }

        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }

        return if (missingPermissions.isEmpty()) {
            true
        } else {
            if (context is android.app.Activity) {
                ActivityCompat.requestPermissions(context, missingPermissions.toTypedArray(), 1001)
            } else {
                throw IllegalStateException("Context must be an Activity to request permissions.")
            }
            false
        }
    }

    suspend fun saveToDownloadsFolder(data: String) {
        try {
            val fileName = "guests_data.txt"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Use MediaStore for Android Q+ (API 29+)
                val contentResolver = context.contentResolver

                // Check if the file already exists
                val existingUri = contentResolver.query(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    arrayOf(MediaStore.MediaColumns._ID),
                    "${MediaStore.MediaColumns.DISPLAY_NAME} = ?",
                    arrayOf(fileName),
                    null
                )?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val id =
                            cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                        return@use MediaStore.Downloads.EXTERNAL_CONTENT_URI.buildUpon()
                            .appendPath(id.toString()).build()
                    }
                    null
                }

                // If file exists, delete it
                existingUri?.let {
                    contentResolver.delete(it, null, null)
                }

                // Create or overwrite the file
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_DOWNLOADS + "/ManifestPortal"
                    )
                }

                val uri =
                    contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    contentResolver.openOutputStream(uri)?.use { outputStream ->
                        outputStream.write(data.toByteArray())
                        outputStream.flush()
                        println("File saved to Downloads/MyAppName folder.")
                    }
                } else {
                    println("Failed to create file in Downloads.")
                }
            } else {
                // For Android 9 and below
                val downloadsFolder = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "MyAppName"
                )
                if (!downloadsFolder.exists()) {
                    downloadsFolder.mkdirs()
                }

                val file = File(downloadsFolder, fileName)

                // Overwrite the file if it exists
                if (file.exists()) {
                    file.delete()
                }

                FileOutputStream(file).use { outputStream ->
                    outputStream.write(data.toByteArray())
                    println("File saved successfully at: ${file.absolutePath}")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

