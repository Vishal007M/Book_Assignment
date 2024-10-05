package com.example.bookasignment.Ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.antsglobe.restcommerse.network.RetrofitClient
import com.example.bookasignment.R
import com.example.bookasignment.ViewModel.TranslateDataViewModel
import com.example.bookasignment.ViewModel.ViewModelFactory
import com.example.bookasignment.databinding.FragmentTranslateBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class TranslateFragment : Fragment() {

    private lateinit var _binding: FragmentTranslateBinding
    private val binding get() = _binding

    private var getData: String? = "no data"
    private val client = OkHttpClient()

    private lateinit var translateViewModel: TranslateDataViewModel
    private val KEY = "AIzaSyB38IwbU85Rt-JDa7vX4uFPh1Nz8ihGuqg"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTranslateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        // Set up the ViewModel
        translateViewModel = ViewModelProvider(
            this,
            ViewModelFactory(RetrofitClient.apiService)
        )[TranslateDataViewModel::class.java]

        getData = arguments?.getString("inputData")

        if (!getData.isNullOrEmpty()) {
            val textToTranslate = getData?.take(2080) // Take only the first 2080 characters

            // Make the translate API call
            translateViewModel.getTranslateVM(KEY, "en", "hi", textToTranslate!!)
            translateViewModel.getTranslateResponse.observe(viewLifecycleOwner) { response ->
                // Ensure the response is not null or empty before accessing it
                if (response.isNotEmpty()) {
                    binding.translatedText.text = response[0]?.translatedText
                } else {
                    Toast.makeText(requireContext(), "Translation failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Parsing data for web search
        binding.searchButton.setOnClickListener {
            val selectedText = getSelectedText()

            if (selectedText.isNotEmpty()) {
                // Encode the selected text for the URL
                val url = "https://www.google.com/search?q=${Uri.encode(selectedText)}"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "No text selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun translateText(text: String) {
        val url = "https://api.mymemory.translated.net/get?q=${Uri.encode(text)}&langpair=en|hi"

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val jsonData = response.body?.string()
                    val jsonObject = JSONObject(jsonData)
                    val translatedText = jsonObject.getJSONObject("responseData").getString("translatedText")

                    // Update the TextView with the translated text
                    requireActivity().runOnUiThread {
                        binding.translatedText.text = translatedText
                    }
                }
            }
        })
    }

    // Get the selected text from the TextView
    private fun getSelectedText(): String {
        val start = binding.translatedText.selectionStart
        val end = binding.translatedText.selectionEnd

        return if (start >= 0 && end > start) {
            binding.translatedText.text.substring(start, end).toString()
        } else {
            ""
        }
    }
}
