package com.sko.manifestmanagement.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sko.manifestmanagement.Fragment.ProfileFragment
import com.sko.manifestmanagement.Fragment.SearchFragment
import com.sko.manifestmanagement.R

class WorkingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_working)


        val navController = findNavController(R.id.fragmentContainerView2)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // Connect NavController with BottomNavigationView
        NavigationUI.setupWithNavController(bottomNavigationView, navController)
    }
}
