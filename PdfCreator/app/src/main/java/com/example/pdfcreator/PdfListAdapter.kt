package com.example.pdfcreator

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pdfcreator.databinding.ItemPdfBinding
import java.io.File

class PdfListAdapter(
    private val pdfFiles: Array<File>?,
    private val onItemClick: (File) -> Unit
) : RecyclerView.Adapter<PdfListAdapter.PdfViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfViewHolder {
        val binding = ItemPdfBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PdfViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PdfViewHolder, position: Int) {
        val pdfFile = pdfFiles?.get(position)
        if (pdfFile != null) {
            holder.bind(pdfFile)
        }
    }

    override fun getItemCount(): Int {
        return pdfFiles?.size ?: 0
    }

    inner class PdfViewHolder(private val binding: ItemPdfBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pdfFile: File) {
            binding.textViewFileName.text = pdfFile.name
            binding.root.setOnClickListener {
                onItemClick.invoke(pdfFile)
            }
        }
    }
}
