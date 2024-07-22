package com.itson.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.itson.R
import com.itson.utils.AsistenciaClaseAdapter
import com.itson.viewmodels.ClaseAsistenciasViewmodel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClaseAsistenciasActivity : AppCompatActivity() {

    private val viewModel: ClaseAsistenciasViewmodel by viewModels()
    private val REQUEST_BLUETOOTH_PERMISSIONS = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list)

        val claseId = intent.getLongExtra("CLASE_ID", -1)

        if (claseId != -1L) {
            viewModel.fetchClase(claseId)
        }

        val claseNombreTextView: TextView = findViewById(R.id.nombre_grupo_textView)
        val asistenciasRecyclerView: RecyclerView = findViewById(R.id.lista_recycler_view)
        val listaButton: Button = findViewById(R.id.list_boton)
        val alumnosButton: Button = findViewById(R.id.students_boton)
        val paseListaButton: Button = findViewById(R.id.attendance_boton)

        viewModel.clase.observe(this, Observer { clase ->
            claseNombreTextView.text = clase.nombre
        })

        viewModel.asistenciaFechaList.observe(this, Observer { asistencias ->
            asistenciasRecyclerView.adapter = AsistenciaClaseAdapter(asistencias){ asistencia ->
                if(asistencia.fecha != null) navigateToClaseAsistenciasFechaActivity(claseId, asistencia.fecha)
            }
            asistenciasRecyclerView.layoutManager = LinearLayoutManager(this)
        })

        viewModel.permissionsRequired.observe(this, Observer { required ->
            if (required) {
                requestBluetoothPermissions()
            } else {
                viewModel.startDiscovery()
            }
        })

        paseListaButton.setOnClickListener {
            viewModel.paseLista()
        }

        alumnosButton.setOnClickListener {
            navigateToClaseAlumnosActivity(claseId)
        }

        listaButton.setOnClickListener {
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

    private fun navigateToClaseAlumnosActivity(claseId: Long) {
        val intent = Intent(this, ClaseAlumnosActivity::class.java)
        intent.putExtra("CLASE_ID", claseId)
        startActivity(intent)
        finish()
    }

    private fun navigateToClaseAsistenciasFechaActivity(claseId: Long, fecha: String) {
        val intent = Intent(this, ClaseAsistenciasFechaActivity::class.java)
        intent.putExtra("CLASE_ID", claseId)
        intent.putExtra("FECHA", fecha)
        startActivity(intent)
        finish()
    }

}