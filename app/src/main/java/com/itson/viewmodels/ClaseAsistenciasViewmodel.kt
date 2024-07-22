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
import androidx.compose.foundation.gestures.rememberTransformableState
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
class ClaseAsistenciasViewmodel @Inject constructor(
    private val application: Application
) : ViewModel() {

    //Para utilizar los repositorios los inyectamos con la anotacion @Inject, estos se obtienen del modulo
    //en el paquete de repositories
    @Inject
    lateinit var dispositivosRepository: DispositivosRepository;

    @Inject
    lateinit var clasesRepository: ClasesRepository

    @Inject
    lateinit var asistenciasRepository: AsistenciasRepository

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    private val _clase = MutableLiveData<Clase>()
    val clase: LiveData<Clase> get() = _clase

    private val _asistenciaFechaList = MutableLiveData<List<Asistencia>>()
    val asistenciaFechaList: LiveData<List<Asistencia>> get() = _asistenciaFechaList

    private val _alumnos = MutableLiveData<List<Alumno>>()
    val alumnos: LiveData<List<Alumno>> get() = _alumnos

    private val _dispositivos = MutableLiveData<List<Dispositivo>>()
    //val dispositivos: LiveData<List<Dispositivo>> get() = _dispositivos

    //Booleano segun el cual el la activity solicita o no los permisos e inicia la busqueda de dispositivos
    private val _permissionsRequired = MutableLiveData<Boolean>()
    val permissionsRequired: LiveData<Boolean> get() = _permissionsRequired

    private val dispositivosDescubiertos = mutableListOf<Dispositivo>()

    //Recividor de transmision
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                //Al encontrar el dispositivo lo agregamos
                BluetoothDevice.ACTION_FOUND -> {
                    val dispositivo: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    dispositivo?.let {
                        Log.i("Dispositivo encontrado", dispositivo.toString())
                        addDispositivoBluetooth(it)
                    }
                }
                //Al iniciar la busqueda solo loggeamos
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    Log.i("Discovery", "Discovery Bluetooth iniciada en Asistencias")
                }
                //Al finalizar la busqueda solo loggeamos
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

    //Revisa si se tienen los permisos bluetooth necesarios
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

        //Si no existe el dispositivo no lo agregamos, ya que no es util para el pase de lista
        dispositivosRepository.getByDireccion(direccion) ?: return

        val dispositivo = Dispositivo(
            id = null,
            nombre = nombreDispositivo,
            direccion = direccion
        )
        dispositivosDescubiertos.add(dispositivo)
        _dispositivos.postValue(dispositivosDescubiertos)
    }

    //Realiza la funcion de pase de lista
    //IMPORTANTE!!!!
    //Por el momento los alumnos que no tengan un dispositivo vinculado se les va a crear una asistencia de tipo "AUSENTE"
    fun paseLista(){
        val alumnosMap = mutableMapOf<String, Alumno>()
        val alumnosList = _alumnos.value ?: return
        val dispositivosList = _dispositivos.value
        val clase = _clase.value ?: return
        val sdf = SimpleDateFormat("dd/M/yyyy")
        val fecha = sdf.format(Date())

        //Se crea un map donde se accede al a cada Alumno por la direccion de su dispositivo, de no tener un dispositivo asignado se agrega
        //a la lista de alumnos sin dispositivos
        for (alumno in alumnosList){
            val dispositivoAlumno = alumno.dispositivo
            if (dispositivoAlumno == null){
                //-----------TEMPORAL------------------
                val estado = Asistencia.Estado.AUSENTE
                val asistenciaAusente = Asistencia(null, estado, alumno, clase, null, fecha)
                asistenciasRepository.insert(asistenciaAusente)
                continue
                //-----------TEMPORAL------------------
            }
            alumnosMap[dispositivoAlumno.direccion] = alumno
        }

        //Por cada dispositivo encontrado, si existe una entrada en el map con esa direccion, se crea una asistencia de tipo "PRESENTE"
        //y se elimina esa entrada del map. Esto quiere decir que el dispositivo si fue detectado, por lo tanto el alumno esta presente.
        if (dispositivosList != null){
            for (dispositivo in dispositivosList){
                if(!alumnosMap.containsKey(dispositivo.direccion)){ return }

                val estado = Asistencia.Estado.PRESENTE
                val alumno = alumnosMap[dispositivo.direccion] ?: continue
                alumnosMap.remove(dispositivo.direccion)

                val asistenciaPresente = Asistencia(null, estado, alumno, clase, null, fecha)
                asistenciasRepository.insert(asistenciaPresente)
            }
        }

        //Las entradas restantes seran de alumnos cuyos dispositivos no fueron detectados, por lo tanto se les crea una asistencia de tipo "AUSENTE"
        for ((direccion, alumno) in alumnosMap){
            val estado = Asistencia.Estado.AUSENTE
            alumnosMap.remove(direccion)

            val asistenciaAusente = Asistencia(null, estado, alumno, clase, null, fecha)
            asistenciasRepository.insert(asistenciaAusente)
        }

        getUniqueFechaList(clase)
    }

    //Obtiene la clase con el id proporcionado y las asistencias de esa clase
    fun fetchClase(claseId: Long) {
        viewModelScope.launch {
            val claseData = withContext(Dispatchers.IO) {
                clasesRepository.getById(claseId)
            }
            if (claseData != null) {
                _clase.postValue(claseData!!)
                _alumnos.postValue(claseData.alumnos ?: emptyList())
                getUniqueFechaList(claseData)
            }
        }
    }

    //De momento la fechas en las que se ha pasado lista se obtienen jalando todas las asistencias tomadas
    //en esa clase y se filtran agregando una asistencia por cada fecha distinta en la que se paso lista.
    private fun getUniqueFechaList(clase: Clase) {
        val asistencias = asistenciasRepository.getByClase(clase)

        val uniqueFechas = mutableSetOf<String>()
        val uniqueAsistenciaList = mutableListOf<Asistencia>()

        for (asistencia in asistencias) {
            if (asistencia.fecha != null && uniqueFechas.add(asistencia.fecha)) {
                uniqueAsistenciaList.add(asistencia)
            }
        }
        _asistenciaFechaList.postValue(uniqueAsistenciaList)
    }
}
