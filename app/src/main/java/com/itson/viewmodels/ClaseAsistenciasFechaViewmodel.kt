package com.itson.viewmodels

import android.Manifest
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itson.models.Alumno
import com.itson.models.Asistencia
import com.itson.models.Clase
import com.itson.models.Dispositivo
import com.itson.repositories.AsistenciasRepository
import com.itson.repositories.ClasesRepository
import com.itson.repositories.DispositivosRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ClaseAsistenciasFechaViewmodel @Inject constructor(
    private val application: Application
) : ViewModel() {

    @Inject
    lateinit var clasesRepository: ClasesRepository

    @Inject
    lateinit var asistenciasRepository: AsistenciasRepository

    private val _clase = MutableLiveData<Clase>()
    val clase: LiveData<Clase> get() = _clase

    private val _asistenciaList = MutableLiveData<List<Asistencia>>()
    val asistenciaList: LiveData<List<Asistencia>> get() = _asistenciaList

    //Obtiene la clase con el id proporcionado y las asistencias de esa clase
    fun fetchAsistencias(claseId: Long, fecha: String) {
        viewModelScope.launch {
            val claseData = withContext(Dispatchers.IO) {
                clasesRepository.getById(claseId)
            }
            if (claseData != null) {
                _clase.postValue(claseData!!)
                val asistenciasData = withContext(Dispatchers.IO) {
                    asistenciasRepository.getByClaseAndFecha(claseData, fecha);
                }
                _asistenciaList.postValue(asistenciasData)
            }
        }
    }
}
