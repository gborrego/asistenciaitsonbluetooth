package com.itson.activities

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.itson.R
import com.itson.models.Alumno
import com.itson.models.Clase
import com.itson.models.Dispositivo
import com.itson.repositories.DispositivosRepository
import com.itson.viewmodels.ClaseAlumnosDispositivosViewmodel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ClaseAlumnosDispositivosActivity : AppCompatActivity(){

    private val viewModel: ClaseAlumnosDispositivosViewmodel by viewModels()
    private val REQUEST_BLUETOOTH_PERMISSIONS = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_devices)

        val bluetoothDispositivoListView: ListView = findViewById(R.id.bluetooth_device_list_view)
        val alumnoListView: ListView = findViewById(R.id.student_list_view)
        val pairButton: Button = findViewById(R.id.pair_button)
        val manualButton: Button = findViewById(R.id.manual_button)

        val alumnos = intent.getParcelableArrayListExtra<Alumno>("ALUMNOS")

        setupObservers()

        if (alumnos == null) {
            Toast.makeText(this, "No se recibieron los alumnos", Toast.LENGTH_SHORT).show()
        }else{
            viewModel.setAlumnos(alumnos)
        }

        pairButton.setOnClickListener {
            val selectedDispositivoPosition = bluetoothDispositivoListView.checkedItemPosition
            val selectedAlumnoPosition = alumnoListView.checkedItemPosition

            if (selectedDispositivoPosition != ListView.INVALID_POSITION && selectedAlumnoPosition != ListView.INVALID_POSITION) {
                val selectedDispositivo = viewModel.dispositivos.value?.get(selectedDispositivoPosition)
                val selectedAlumno = viewModel.alumnos.value?.get(selectedAlumnoPosition)

                viewModel.asignarDispositivoToAlumno(selectedDispositivo,selectedAlumno)
                viewModel.removeDispositivo(selectedDispositivo);
                viewModel.removeAlumno(selectedAlumno);
            }
        }

        manualButton.setOnClickListener {
            val selectedAlumnoPosition = alumnoListView.checkedItemPosition

            if (selectedAlumnoPosition != ListView.INVALID_POSITION) {
                val selectedAlumno = viewModel.alumnos.value?.get(selectedAlumnoPosition)

                viewModel.removeAlumno(selectedAlumno);
            }
        }

        checkAndRequestPermissions()
    }

    private fun setupObservers() {
        viewModel.permissionsRequired.observe(this, Observer { required ->
            if (required) {
                requestBluetoothPermissions()
            } else {
                viewModel.startDiscovery()
            }
        })

        viewModel.dispositivos.observe(this, Observer { dispositivos ->
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, dispositivos.map { it.nombre })
            findViewById<ListView>(R.id.bluetooth_device_list_view).adapter = adapter
        })

        viewModel.alumnos.observe(this, Observer { alumnos ->
            if (alumnos.isEmpty()){
               navigateToMainActivity()
            }
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, alumnos.map { it.nombre })
            findViewById<ListView>(R.id.student_list_view).adapter = adapter
        })
    }

    private fun checkAndRequestPermissions() {
        if (!hasBluetoothPermissions()) {
            Log.i("Bluetooth no disponible", "El dispositivo actualmente no soporta Bluetooth.")
            requestBluetoothPermissions()
        } else {
            viewModel.startDiscovery()
        }
    }

    private fun hasBluetoothPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) &&
                (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestBluetoothPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,// Needed for device discovery
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        }
        ActivityCompat.requestPermissions(this, permissions.toTypedArray(), REQUEST_BLUETOOTH_PERMISSIONS)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                viewModel.startDiscovery()
            } else {
                Toast.makeText(this, "Se requieren permisos de Bluetooth para escanear dispositivos.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopDiscovery()
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}