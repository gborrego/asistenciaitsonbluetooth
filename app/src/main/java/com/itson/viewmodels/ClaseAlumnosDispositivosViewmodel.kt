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
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itson.models.Alumno
import com.itson.models.Dispositivo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ClaseAlumnosDispositivosViewmodel @Inject constructor(
    private val application: Application
) : ViewModel() {

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    private val _dispositivos = MutableLiveData<List<Dispositivo>>()
    val dispositivos: LiveData<List<Dispositivo>> get() = _dispositivos

    private val _alumnos = MutableLiveData<List<Alumno>>()
    val alumnos: LiveData<List<Alumno>> get() = _alumnos

    private val _permissionsRequired = MutableLiveData<Boolean>()
    val permissionsRequired: LiveData<Boolean> get() = _permissionsRequired

    private val dispositivosDescubiertos = mutableListOf<Dispositivo>()

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val dispositivo: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    dispositivo?.let {
                        addDispositivo(it)
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {

                }
            }
        }
    }

    init {
        if (!checkBluetoothPermissions()) {
            _permissionsRequired.postValue(true)
        } else {
            _permissionsRequired.postValue(false)
        }
    }

    fun startDiscovery() {
        if (checkBluetoothPermissions()) {
            val filter = IntentFilter(BluetoothDevice.ACTION_FOUND).apply {
                addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            }
            application.registerReceiver(receiver, filter)
            if (ActivityCompat.checkSelfPermission(
                    application,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                _permissionsRequired.postValue(true)
                return
            }
            bluetoothAdapter?.startDiscovery()
        } else {
            _permissionsRequired.postValue(true)
        }
    }

    fun stopDiscovery() {
        if (ActivityCompat.checkSelfPermission(
                application,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            _permissionsRequired.postValue(true)
            return
        }
        bluetoothAdapter?.cancelDiscovery()
        application.unregisterReceiver(receiver)
    }

    private fun checkBluetoothPermissions(): Boolean {
        val bluetoothPermission = ContextCompat.checkSelfPermission(application, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
        val bluetoothAdminPermission = ContextCompat.checkSelfPermission(application, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED
        val bluetoothConnectPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(application, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        } else true
        val fineLocationPermission = ContextCompat.checkSelfPermission(application, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

        val allPermissionsGranted = bluetoothPermission && bluetoothAdminPermission && bluetoothConnectPermission && fineLocationPermission

        if (!allPermissionsGranted) {
            _permissionsRequired.postValue(true)
        }

        return allPermissionsGranted
    }

    fun addDispositivo(dispositivoBluetooth: BluetoothDevice) {
        val nombreDispositivo = if (ActivityCompat.checkSelfPermission(application, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            dispositivoBluetooth.name ?: "Dispositivo desconocido"
        } else {
            "Dispositivo desconocido"
        }

        val dispositivo = Dispositivo(
            id = null,
            nombre = nombreDispositivo,
            direccion = dispositivoBluetooth.address
        )
        dispositivosDescubiertos.add(dispositivo)
        _dispositivos.postValue(dispositivosDescubiertos)
    }

    fun setAlumnos(alumnosList: List<Alumno>) {
        _alumnos.postValue(alumnosList)
    }
}