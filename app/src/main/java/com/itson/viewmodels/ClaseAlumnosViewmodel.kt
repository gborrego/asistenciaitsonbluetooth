package com.itson.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itson.models.Alumno
import com.itson.models.Asistencia
import com.itson.models.Clase
import com.itson.repositories.AlumnosRepository
import com.itson.repositories.AsistenciasRepository
import com.itson.repositories.ClasesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ClaseAlumnosViewmodel @Inject constructor(
    private val clasesRepository: ClasesRepository,
) : ViewModel() {

    private val _clase = MutableLiveData<Clase>()
    val clase: LiveData<Clase> get() = _clase

    private val _alumnoList = MutableLiveData<List<Alumno>>()
    val alumnoList: LiveData<List<Alumno>> get() = _alumnoList

    fun fetchClase(claseId: Long) {
        viewModelScope.launch {
            val claseData = withContext(Dispatchers.IO) {
                clasesRepository.getById(claseId)
            }
            if (claseData != null) {
                _clase.postValue(claseData!!)
                _alumnoList.postValue(claseData.alumnos ?: emptyList())
            }
        }
    }
}