package com.example.pdfcreator

import FromDevice
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.pdfcreator.databinding.ActivityViewPdfBinding
import java.io.File

class ViewPdf : AppCompatActivity() {
    private lateinit var binding: ActivityViewPdfBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        openRecentFolder()
        Log.d("message","initial Folder opened")

        binding.navbar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.recent -> {
                    openRecentFolder()
                    true
                }
                R.id.fromDevice -> {
                    ReplaceFragment(FromDevice())
                    true
                }
                else -> false
            }
        }
    }

    private fun ReplaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame, fragment)
        fragmentTransaction.commit()
    }

    private fun openRecentFolder() {
        val fileName = "multiImagePdf.pdf"
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        val recentFolderPath = file.parent

        val bundle = bundleOf("recentFolderPath" to recentFolderPath)
        ReplaceFragment(RecentFolder().apply { arguments = bundle })
    }

}
