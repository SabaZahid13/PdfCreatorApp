package com.example.pdfcreator

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream

class ConvertTextToPdf : AppCompatActivity() {
    private lateinit var pdfDocument: PdfDocument
    private lateinit var page: PdfDocument.Page
    private lateinit var userText :EditText
    private lateinit var generatePdfButton: Button
    private val permissions =  if (Build.VERSION.SDK_INT >= 33) {
        arrayOf("android.permission.READ_MEDIA_AUDIO","android.permission.READ_MEDIA_VIDEO","android.permission.READ_MEDIA_IMAGES","android.permission.READ_MEDIA_VIDEO","android.permission.ACCESS_MEDIA_LOCATION")
    }else{
        arrayOf("android.permission.READ_EXTERNAL_STORAGE","android.permission.WRITE_EXTERNAL_STORAGE","android.permission.ACCESS_MEDIA_LOCATION")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_convert_text_to_pdf)
        generatePdfButton=findViewById(R.id.generatePdfButton)
        generatePdfButton.setOnClickListener{
            createPdfFromText()
        }
    }
    private fun createPdfFromText() {
        userText=findViewById(R.id.text)
        val pdfText = userText.text.toString()
        if (pdfText != null) {
            pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas
            val paint = Paint().apply {
                color = Color.BLACK
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL) // Set the font
            }
            canvas.drawText(pdfText, 50f, 50f, paint)
            pdfDocument.finishPage(page)

            val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "convertText.pdf")
            try {
                FileOutputStream(file).use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                    Toast.makeText(
                        this,
                        "PDF Generated at ${file.absolutePath}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error creating PDF!", Toast.LENGTH_SHORT).show()
            } finally {
                pdfDocument.close()
            }
        }
        else {
            Toast.makeText(this, "Add Text First!", Toast.LENGTH_SHORT).show()
        }
    }
}
