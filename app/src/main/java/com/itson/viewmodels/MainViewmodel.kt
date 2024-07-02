package com.itson.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itson.models.Alumno
import com.itson.models.Clase
import com.itson.repositories.AlumnosRepository
import com.itson.repositories.ClasesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.opencsv.CSVReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

@HiltViewModel
class MainViewmodel @Inject constructor(
    private val clasesRepository: ClasesRepository
) : ViewModel() {

    private val _clases = MutableLiveData<List<Clase>>()
    val clases: LiveData<List<Clase>> get() = _clases

    init {
        fetchClases()
    }

    private fun fetchClases() {
        viewModelScope.launch {
            val clasesList = withContext(Dispatchers.IO) {
                clasesRepository.getAll()
            }
            _clases.postValue(clasesList)
        }
    }}