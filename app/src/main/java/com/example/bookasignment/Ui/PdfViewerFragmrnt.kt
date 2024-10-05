package com.example.bookasignment.Ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.bookasignment.Utils.DataParse
import com.example.bookasignment.databinding.FragmentPdfViewerFragmrntBinding
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import java.io.File
import java.io.IOException

class PdfViewerFragmrnt : Fragment() {

    private var _binding: FragmentPdfViewerFragmrntBinding? = null
    private val binding get() = _binding!!
    private lateinit var dataParse: DataParse

    var pdfRenderer: PdfRenderer? = null
    private var pageIndex: Int = 0
    private var currentPage: PdfRenderer.Page? = null // Make currentPage nullable
    lateinit var pdfFileName: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPdfViewerFragmrntBinding.inflate(inflater, container, false)

        // Get the data from the arguments
        pdfFileName = arguments?.getString("input_data").toString()

        dataParse = requireActivity() as DataParse

        return binding.root
    }


    override fun onStart() {
        super.onStart()
        try {
            openRender(requireContext())
            showPage(pageIndex)

            // for extract text from pdf
            extractTextFromPdf(requireContext(), pdfFileName)
            val pdfText = extractTextFromPdf(
                requireContext(), pdfFileName
            )

            //parsing pdf text data to translation fragment
            dataParse.parseData(pdfText.toString())


        } catch (e: IOException) {
            Toast.makeText(requireContext(), "Failed to open PDF: ${e.message}", Toast.LENGTH_SHORT)
                .show()
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()

        // go to next page
        binding.buttonNextDoc.setOnClickListener {
            showPage((currentPage?.index ?: 0) + 1)
        }

        // go to previous page
        binding.buttonPreDoc.setOnClickListener {
            showPage((currentPage?.index ?: 0) - 1)
        }

        pageIndex = 0
    }

    override fun onStop() {
        super.onStop()
        try {
            closeRenderer()
        } catch (e: IOException) {
            Toast.makeText(
                requireContext(), "Failed to close PDF: ${e.message}", Toast.LENGTH_SHORT
            ).show()
        }
    }

    @Throws(IOException::class)
    private fun openRender(context: Context) {
        val file = File(context.cacheDir, "$pdfFileName.pdf")

        // fetching pdf from assets folder
        if (!file.exists()) {
            try {
                val assetManager = context.assets
                val inputStream = assetManager.open("$pdfFileName.pdf")
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                inputStream.close()
            } catch (e: IOException) {
                Toast.makeText(context, "Error loading PDF: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
                return
            }
        }

        try {
            val fileDescriptor =
                ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            pdfRenderer = PdfRenderer(fileDescriptor)
        } catch (e: IOException) {
            Toast.makeText(context, "Error opening PDF: ${e.message}", Toast.LENGTH_SHORT).show()
            throw e
        }

    }

    private fun closeRenderer() {
        try {
            // Safely close the current page if it's open
            currentPage?.close()
            pdfRenderer?.close()
        } catch (e: IOException) {
            Toast.makeText(requireContext(), "Error closing PDF: ${e.message}", Toast.LENGTH_SHORT)
                .show()
        } finally {
            pdfRenderer = null
        }
    }

    private fun showPage(index: Int) {

        // Close the current page if it's open
        currentPage?.close()

        // Ensure pdfRenderer is not null and the index is within page count
        if (pdfRenderer == null || index < 0 || index >= (pdfRenderer?.pageCount ?: 0)) {
            return
        }

        // Open the specified page
        currentPage = pdfRenderer?.openPage(index)

        // Create a Bitmap to hold the page content
        val bitmap = Bitmap.createBitmap(
            currentPage?.width?.plus(1) ?: 0, // Adding padding to width
            currentPage?.height?.plus(1) ?: 0, // Adding padding to height
            Bitmap.Config.ARGB_8888
        )

        // Render the page content onto the Bitmap
        currentPage?.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

        // Display the Bitmap in the ImageView
        binding.pdfImage.setImageBitmap(bitmap)

        // Update page index
        pageIndex = index

        // Update the UI
        updateUi()
    }

    // Update the UI based on the current page index
    private fun updateUi() {
        val index = currentPage?.index ?: 0
        val pageCount = pdfRenderer?.pageCount ?: 0
        if (index == 0) {
            binding.buttonPreDoc.visibility = View.GONE
        }else{
            binding.buttonPreDoc.visibility = View.VISIBLE
        }
        if (index == pageCount - 1) {
            binding.buttonNextDoc.visibility = View.GONE
        }else{
            binding.buttonNextDoc.visibility = View.VISIBLE
        }
    }

    fun getPageCount(): Int {
        return pdfRenderer?.pageCount ?: 0
    }

    // Extract text from the PDF
    private fun extractTextFromPdf(context: Context, pdfFileName: String): String {
        val file = File(context.cacheDir, "$pdfFileName.pdf")
        val pdfText = StringBuilder()

        try {
            // Open the PDF document
            val pdfReader = PdfReader(file.absolutePath)
            val pdfDocument = PdfDocument(pdfReader)

            // Extract text from all pages
            val numberOfPages = pdfDocument.numberOfPages
            for (i in 1..numberOfPages) {
                val page = pdfDocument.getPage(i)
                pdfText.append(PdfTextExtractor.getTextFromPage(page))
                pdfText.append("\n")
            }

            // Close the PDF document
            pdfDocument.close()
            pdfReader.close()

        } catch (e: IOException) {
            e.printStackTrace()
        }

        return pdfText.toString() // Return the extracted text
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
