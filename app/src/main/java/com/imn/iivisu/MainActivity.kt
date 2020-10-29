package com.imn.iivisu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.imn.iivisu.databinding.ActivityMainBinding
import com.imn.iivisu.utils.showToast

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recordButton.setOnClickListener { showToast("Hello World") }
    }
}