package com.itson.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.itson.R
import com.itson.models.Alumno
import com.itson.models.Clase
import com.itson.models.Dispositivo
import com.itson.viewmodels.ClaseAlumnosDispositivosViewmodel
import dagger.hilt.android.AndroidEntryPoint

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

        if (alumnos != null){
            viewModel.setAlumnos(alumnos)
        }

        if (checkBluetoothPermissions()) {
            setupObservers()
        } else {
            requestBluetoothPermissions()
        }

        pairButton.setOnClickListener {
            val selectedDispositivoPosition = bluetoothDispositivoListView.checkedItemPosition
            val selectedAlumnoPosition = alumnoListView.checkedItemPosition

            if (selectedDispositivoPosition != ListView.INVALID_POSITION && selectedAlumnoPosition != ListView.INVALID_POSITION) {
                val selectedDispositivo = viewModel.dispositivos.value?.get(selectedDispositivoPosition)
                val selectedAlumno = viewModel.alumnos.value?.get(selectedAlumnoPosition)

                pairDispositivoToAlumno(selectedDispositivo, selectedAlumno)
            }
        }

        manualButton.setOnClickListener {

        }
    }

    private fun checkBluetoothPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED &&
                (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH_CONNECT
            ), REQUEST_BLUETOOTH_PERMISSIONS)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
            ), REQUEST_BLUETOOTH_PERMISSIONS)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                setupObservers()
                val alumnos = intent.getParcelableArrayListExtra<Alumno>("ALUMNOS")
                if (alumnos != null){
                    viewModel.setAlumnos(alumnos)
                }
            } else {

            }
        }
    }

    private fun setupObservers() {
        viewModel.dispositivos.observe(this, Observer { dispositivos ->
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, dispositivos.map { it.nombre })
            findViewById<ListView>(R.id.bluetooth_device_list_view).adapter = adapter
        })

        viewModel.dispositivos.observe(this, Observer { alumnos ->
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, alumnos.map { it.nombre })
            findViewById<ListView>(R.id.student_list_view).adapter = adapter
        })
    }

    //Juntar alumno con dispositivo
    private fun pairDispositivoToAlumno(device: Dispositivo?, alumno: Alumno?) {
    }
}