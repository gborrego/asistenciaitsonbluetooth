package com.itson.viewmodels

import android.bluetooth.BluetoothAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itson.models.Alumno
import com.itson.models.Dispositivo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ClaseAlumnosDispositivosViewmodel @Inject constructor() : ViewModel() {

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    private val _dispositivos = MutableLiveData<List<Dispositivo>>()
    val dispositivos: LiveData<List<Dispositivo>> get() = _dispositivos

    private val _alumnos = MutableLiveData<List<Alumno>>()
    val alumnos: LiveData<List<Alumno>> get() = _alumnos

    init {
        fetchBluetoothDevices()
    }


    private fun fetchBluetoothDevices() {
        viewModelScope.launch {
            val dispositivosList = withContext(Dispatchers.IO) {
                try {
                    bluetoothAdapter?.bondedDevices?.map {
                        Dispositivo(
                            id = null,
                            nombre = it.name ?: "Dispositivo sin nombre",
                            direccion = it.address
                        )
                    } ?: emptyList()
                } catch (e: SecurityException) {
                    emptyList()
                }
            }
            _dispositivos.postValue(dispositivosList)
        }
    }

    fun setAlumnos(alumnosList: List<Alumno>) {
        _alumnos.postValue(alumnosList)
    }
}