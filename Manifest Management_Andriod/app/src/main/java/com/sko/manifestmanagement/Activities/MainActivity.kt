package com.sko.manifestmanagement.Activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.sko.manifestmanagement.ExportDataHelper
import com.sko.manifestmanagement.R
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var navController: NavController

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        // Export data after ensuring permissions are granted
        lifecycleScope.launch {
            val exportHelper = ExportDataHelper(this@MainActivity)
            exportHelper.exportDataToFile()
        }
    }



    override fun onSupportNavigateUp(): Boolean {
        navController = findNavController(R.id.fragmentContainerView)
        return super.onSupportNavigateUp()
    }

}

