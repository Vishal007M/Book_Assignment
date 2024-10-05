package com.example.bookasignment.Ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bookasignment.Utils.DataParse
import com.example.bookasignment.R
import com.example.bookasignment.databinding.ActivityViewPdfBinding

class ViewPdfNExplain : AppCompatActivity(), DataParse {

    private lateinit var binding: ActivityViewPdfBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get data from the intent
        val inputData = intent.getStringExtra("input_data")

        binding.toolbarPdfName.text = "$inputData PDF"

        if (savedInstanceState == null) {
            val fragment = PdfViewerFragmrnt()

            // Pass data to the fragment using arguments
            val bundle = Bundle()
            bundle.putString("input_data", inputData)
            fragment.arguments = bundle

            // Add the fragment to the activity
            supportFragmentManager.beginTransaction()
                .replace(R.id.PdfViewerFrame, fragment)
                .commit()
        }

        if (savedInstanceState == null) {
            val fragment2 = TranslateFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.TranslateFrame, fragment2)
                .commit()
        }

        binding.Back.setOnClickListener {
            val intent = Intent(this, BookListActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    // Implement the DataParse interface
    override fun parseData(data: String) {
        val bundle = Bundle()
        bundle.putString("inputData", data)
        val fragment2 = TranslateFragment()
        fragment2.arguments = bundle
        supportFragmentManager.beginTransaction()
            .add(R.id.TranslateFrame, fragment2)
            .commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}
