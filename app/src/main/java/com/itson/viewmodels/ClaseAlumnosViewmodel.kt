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
class ClaseAlumnosViewModel @Inject constructor(
    private val clasesRepository: ClasesRepository,
    private val alumnosRepository: AlumnosRepository
) : ViewModel() {

    private val _clase = MutableLiveData<Clase>()
    val clase: LiveData<Clase> get() = _clase

    private val _alumnoList = MutableLiveData<List<Alumno>>()
    val alumnoList: LiveData<List<Alumno>> get() = _alumnoList

    fun fetchClase(claseId: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                clasesRepository.getById(claseId)
            }.also {
                _clase.postValue(it)
            }
        }
    }

    fun fetchAlumnosList(claseId: Long) {
        val clase = clase.value ?: return
        viewModelScope.launch {
            val alumnosData = withContext(Dispatchers.IO) {
                alumnosRepository.getAllByClase(clase)
            }
            _alumnoList.postValue(alumnosData)
        }
    }
}