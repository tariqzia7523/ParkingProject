package com.parking.project.actiivites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.parking.project.databinding.ActivityHomeScreenBinding

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.adminLogin.setOnClickListener {
            startActivity(Intent(this@HomeActivity,LoginActivity::class.java))
        }
        binding.viewParking.setOnClickListener {
            startActivity(Intent(this@HomeActivity,MainActivity::class.java))
        }

    }
}