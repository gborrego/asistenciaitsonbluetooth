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
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itson.models.Alumno
import com.itson.models.Dispositivo
import com.itson.repositories.AlumnosRepository
import com.itson.repositories.ClasesRepository
import com.itson.repositories.DispositivosRepository
import com.itson.repositories.DispositivosRepositoryDB
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ClaseAlumnosDispositivosViewmodel @Inject constructor(
    private val application: Application
) : ViewModel() {

    @Inject
    lateinit var dispositivosRepository: DispositivosRepository;

    @Inject
    lateinit var clasesRepository: ClasesRepository;

    @Inject
    lateinit var alumnosRepository: AlumnosRepository;

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    private val _dispositivos = MutableLiveData<List<Dispositivo>>()
    val dispositivos: LiveData<List<Dispositivo>> get() = _dispositivos

    private val _alumnos = MutableLiveData<List<Alumno>>()
    val alumnos: LiveData<List<Alumno>> get() = _alumnos

    //Booleano segun el cual el la activity solicita o no los permisos e inicia la busqueda de dispositivos
    private val _permissionsRequired = MutableLiveData<Boolean>()
    val permissionsRequired: LiveData<Boolean> get() = _permissionsRequired

    private val dispositivosDescubiertos = mutableListOf<Dispositivo>()

    //Lista modificable con el fin de poder eliminar los alumnos al momento de vincularlos a un dispositivo.
    //Esto se hace para eliminar al alumno del listview
    private val alumnosList = mutableListOf<Alumno>()

    //Recividor de transmision
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    //Al encontrar el dispositivo lo agregamos
                    val dispositivo: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    dispositivo?.let {
                        Log.i("Dispositivo encontrado", dispositivo.toString())
                        addDispositivoBluetooth(it)
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    Log.i("Discovery", "Discovery Bluetooth iniciada en AlumnosDispositivos")
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    Log.i("Discovery", "Discovery Bluetooth finalizada")
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

    //Inicia la busqueda de dispositivos
    fun startDiscovery() {
        if (checkBluetoothPermissions()) {
            val filter = IntentFilter(BluetoothDevice.ACTION_FOUND).apply {
                addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
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

    //Detiene la busqueda de dispostivos
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
            (ContextCompat.checkSelfPermission(application, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(application, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED)
        } else true
        val fineLocationPermission = ContextCompat.checkSelfPermission(application, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseLocationPermission = ContextCompat.checkSelfPermission(application, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        val allPermissionsGranted = bluetoothPermission && bluetoothAdminPermission && bluetoothConnectPermission && fineLocationPermission && coarseLocationPermission

        if (!allPermissionsGranted) {
            _permissionsRequired.postValue(true)
        }

        return allPermissionsGranted
    }

    //Agrega un dispositivo bluetooth encontrado a la lista de dispositivos para el pase de lista
    private fun addDispositivoBluetooth(dispositivoBluetooth: BluetoothDevice) {
        val nombreDispositivo = if (ActivityCompat.checkSelfPermission(application, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            dispositivoBluetooth.name ?: "Dispositivo desconocido"
        } else {
            "Dispositivo desconocido"
        }

        val direccion = dispositivoBluetooth.address

        val dispositivoEncontrado = dispositivosRepository.getByDireccion(direccion)

        //Si existe el dispositivo no lo agregamos, ya que ya esta vinculado a otro alumno
        if (dispositivoEncontrado != null) return

        val dispositivo = Dispositivo(
            id = null,
            nombre = nombreDispositivo,
            direccion = direccion
        )
        dispositivosDescubiertos.add(dispositivo)
        _dispositivos.postValue(dispositivosDescubiertos)
    }

    fun fetchAlumnos(claseId: Long) {
        viewModelScope.launch {
            val claseData = withContext(Dispatchers.IO) {
                clasesRepository.getById(claseId)
            }
            if (claseData != null) {
                _alumnos.postValue(claseData.alumnos ?: emptyList())
            }
        }
    }

    fun removeDispositivo(dispositivo: Dispositivo?){
        dispositivosDescubiertos.remove(dispositivo);
        _dispositivos.postValue(dispositivosDescubiertos);
    }

    fun removeAlumno(alumno: Alumno?){
        alumnosList.remove(alumno)
        _alumnos.postValue(alumnosList)
    }

    fun asignarDispositivoToAlumno(dispositivo: Dispositivo?, alumno: Alumno?) {
        if (dispositivo != null && alumno != null) {
            dispositivosRepository.insertToAlumno(dispositivo,alumno)
        }
    }
}