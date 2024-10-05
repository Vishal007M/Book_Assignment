package com.example.bookasignment.Ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.bookasignment.databinding.ActivityBookListBinding

class BookListActivity : AppCompatActivity() {

    lateinit var binding: ActivityBookListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //go to ViewPdfNExplain activity
        binding.ScienceMenu.setOnClickListener {
            val intent = Intent(this, ViewPdfNExplain::class.java)
            intent.putExtra("input_data", "Science")
            startActivity(intent)
            finish()
        }

        //go to ViewPdfNExplain activity
        binding.EnglishMenu.setOnClickListener {
            val intent = Intent(this, ViewPdfNExplain::class.java)
            intent.putExtra("input_data", "English")
            startActivity(intent)
        }

        //go to ViewPdfNExplain activity
        binding.HistoryMenu.setOnClickListener {
            val intent = Intent(this, ViewPdfNExplain::class.java)
            intent.putExtra("input_data", "History")
            startActivity(intent)
        }

        //go to ViewPdfNExplain activity
        binding.Back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    // go back with finish
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}