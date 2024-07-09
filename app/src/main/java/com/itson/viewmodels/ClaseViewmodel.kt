package com.itson.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itson.models.Asistencia
import com.itson.models.Clase
import com.itson.repositories.AsistenciasRepository
import com.itson.repositories.ClasesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ClaseViewModel @Inject constructor(
    private val clasesRepository: ClasesRepository,
    private val asistenciasRepository: AsistenciasRepository
) : ViewModel() {

    private val _clase = MutableLiveData<Clase>()
    val clase: LiveData<Clase> get() = _clase

    private val _asistenciaList = MutableLiveData<List<Asistencia>>()
    val asistenciaList: LiveData<List<Asistencia>> get() = _asistenciaList

    fun fetchClase(claseId: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                clasesRepository.getById(claseId)
            }.also {
                _clase.postValue(it)
            }
        }
    }

    fun fetchAsistenciaList(claseId: Long) {
        val claseWithFecha = clase.value ?: return
        viewModelScope.launch {
            val asistenciasData = withContext(Dispatchers.IO) {
                asistenciasRepository.getByClaseAndFecha(claseWithFecha,"");
            }
            _asistenciaList.postValue(asistenciasData)
        }
    }
}
