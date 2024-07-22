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
import com.itson.utils.AsistenciaClaseFechaAdapter
import com.itson.viewmodels.ClaseAsistenciasFechaViewmodel
import com.itson.viewmodels.ClaseAsistenciasViewmodel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClaseAsistenciasFechaActivity : AppCompatActivity() {

    private val viewModel: ClaseAsistenciasFechaViewmodel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.attendance_summary)

        val claseId = intent.getLongExtra("CLASE_ID", -1)
        val fecha = intent.getStringExtra("FECHA")

        if (claseId != -1L && fecha != null) {
            viewModel.fetchAsistencias(claseId,fecha)
        }

        val asistenciasRecyclerView: RecyclerView = findViewById(R.id.recyclerViewAlumnos)
        val terminarButton: Button = findViewById(R.id.finish_button)
        val claseNombreTextView: TextView = findViewById(R.id.group_name_textview)

        viewModel.clase.observe(this, Observer { clase ->
            claseNombreTextView.text = clase.nombre
        })

        viewModel.asistenciaList.observe(this, Observer { asistenciaList ->
            asistenciasRecyclerView.adapter = AsistenciaClaseFechaAdapter(asistenciaList)
            asistenciasRecyclerView.layoutManager = LinearLayoutManager(this)
        })

        terminarButton.setOnClickListener {
            navigateToClaseAsistenciasActivity(claseId)
        }
    }

    private fun navigateToClaseAsistenciasActivity(claseId: Long) {
        val intent = Intent(this, ClaseAsistenciasActivity::class.java)
        intent.putExtra("CLASE_ID", claseId)
        startActivity(intent)
        finish()
    }




}