package com.example.pdfcreator

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pdfcreator.databinding.FragmentRecentFolderBinding
import java.io.File

class RecentFolder : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRecentFolderBinding.inflate(inflater, container, false)
        val recyclerView = binding.pdfListRecyclerView
        val recentFolderPath = arguments?.getString("recentFolderPath")
        if (recentFolderPath != null) {
            val pdfFiles = File(recentFolderPath).listFiles { _, name ->
                name.endsWith(".pdf", true)
            }
            if (pdfFiles != null && pdfFiles.isNotEmpty()) {
                val adapter = PdfListAdapter(pdfFiles) { pdfFile ->
                    openPdfFile(pdfFile)
                }
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
            } else {
                Toast.makeText(context, "No PDF File Found!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Error showing PDF!", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    private fun openPdfFile(pdfFile: File) {
        val pdfIntent = Intent(Intent.ACTION_VIEW)
        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireActivity().packageName}.fileprovider",
            pdfFile
        )
        pdfIntent.setDataAndType(uri, "application/pdf")
        pdfIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

        try {
            startActivity(pdfIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "Error opening PDF!", Toast.LENGTH_SHORT).show()

        }
    }
}




