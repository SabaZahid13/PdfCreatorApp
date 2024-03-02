package com.example.pdfcreator

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ConvertImageToPdf : AppCompatActivity() {

    private lateinit var generatePdfButton: Button
    private lateinit var selectImageButton: ImageButton
    private val pickImage = 100
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var selectedImage: ImageView
    private val selectedImagesList = mutableListOf<ImageItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_convert_image_to_pdf)

        Log.d("message","Inside onCreate")
        selectImageButton = findViewById(R.id.selectImagesButton)
        generatePdfButton=findViewById(R.id.generatePdfButton)
        selectedImage = findViewById(R.id.selectedImage)
        recyclerView = findViewById(R.id.horizontalRecyclerView)
        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        imageAdapter = ImageAdapter(selectedImagesList) { selectedImageItem ->
            showSelectedImageView(selectedImageItem.uri)
        }
        recyclerView.adapter = imageAdapter

        selectImageButton.setOnClickListener{
            openGallery()
        }
        generatePdfButton.setOnClickListener{
            createPdfFromImages(selectedImagesList,this)
        }
    }

    private fun showSelectedImageView(imageUri: Uri) {
        selectedImage.setImageURI(imageUri)
    }

    private fun showSelectedImages(images: List<ImageItem>) {
        selectedImagesList.clear()
        selectedImagesList.addAll(images)
        imageAdapter.notifyDataSetChanged()
    }

    private fun openGallery() {
        var intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, pickImage);
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == pickImage) {

            val selectedImages = mutableListOf<ImageItem>()

            // if multiple images are selected
            if (data?.clipData != null) {
                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageUri: Uri = data.clipData!!.getItemAt(i).uri
                    selectedImages.add(ImageItem(imageUri))
                }

            } else if (data?.data != null) {
                // if single image is selected
                val imageUri: Uri = data.data!!
                selectedImages.add(ImageItem(imageUri))
            }
            showSelectedImages(selectedImages)
        }
    }
    private fun createPdfFromImages(selectedImages: List<ImageItem>, context: Context) {
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Processing, please wait...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                if (selectedImages.isNotEmpty()) {
                    val pdfDocument = PdfDocument()

                    for ((index, imageItem) in selectedImages.withIndex()) {
                        val inputStream = context.contentResolver.openInputStream(imageItem.uri)
                        val bitmap = BitmapFactory.decodeStream(inputStream)

                        if (bitmap != null) {
                            val pageInfo =
                                PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, index + 1)
                                    .create()
                            val page = pdfDocument.startPage(pageInfo)
                            val canvas = page.canvas
                            canvas.drawBitmap(bitmap, 0f, 0f, null)
                            pdfDocument.finishPage(page)
                        } else {
                            Log.d(
                                "message",
                                "Error decoding image stream or bitmap is null for image at index $index"
                            )
                        }
                    }

                    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(
                        Date()
                    )
                    val fileName = "multiImagePdf_$timestamp.pdf"
                    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
                    Log.d("message", "Inside createPdfFromImages ${file.absolutePath}")

                    try {
                        FileOutputStream(file).use { outputStream ->
                            pdfDocument.writeTo(outputStream)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "PDF Generated at ${file.absolutePath}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Error creating PDF!", Toast.LENGTH_SHORT).show()
                        }
                    } finally {
                        pdfDocument.close()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "No images selected", Toast.LENGTH_SHORT).show()
                    }
                }
            } finally {
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                }
            }
        }
    }
}