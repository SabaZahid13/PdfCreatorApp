package com.example.pdfcreator

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val pdfFiles = MutableLiveData<List<String>>()
}