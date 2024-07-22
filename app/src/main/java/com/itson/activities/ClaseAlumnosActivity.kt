package com.itson.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.itson.R
import com.itson.models.Alumno
import com.itson.utils.AlumnoAdapter
import com.itson.viewmodels.ClaseAlumnosViewmodel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClaseAlumnosActivity : AppCompatActivity() {

    private val viewModel: ClaseAlumnosViewmodel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_list)

        val claseId = intent.getLongExtra("CLASE_ID", -1)

        if (claseId != -1L) {
            viewModel.fetchClase(claseId)
        }

        val nombreGrupoTextView: TextView = findViewById(R.id.nombreGrupo)
        val botonLista: Button = findViewById(R.id.botonLista)
        val botonAlumnos: Button = findViewById(R.id.botonAlumnos)
        val botonAgregarAlumno: Button = findViewById(R.id.add_student_button)
        val botonAgregarDispositivos: Button = findViewById(R.id.add_device_button)
        val recyclerView: RecyclerView = findViewById(R.id.lista_alumnos_recycler_view)

        recyclerView.layoutManager = LinearLayoutManager(this)


        viewModel.clase.observe(this, Observer { clase ->
            nombreGrupoTextView.text = clase.nombre
        })

        viewModel.alumnoList.observe(this, Observer { alumnosList ->
            recyclerView.adapter = AlumnoAdapter(alumnosList)
        })

        botonLista.setOnClickListener {
            navigateToClaseAsistenciasActivity(claseId)
        }

        botonAlumnos.setOnClickListener {
            // Handle button click
        }

        botonAgregarAlumno.setOnClickListener {
            navigateToAgregarAlumnoManualActiviy(claseId)
        }

        botonAgregarDispositivos.setOnClickListener {
            navigateToClaseAlumnosDispositivosActivity(claseId)
        }
    }

    private fun navigateToClaseAsistenciasActivity(claseId: Long) {
        val intent = Intent(this, ClaseAsistenciasActivity::class.java)
        intent.putExtra("CLASE_ID", claseId)
        startActivity(intent)
        finish()
    }

    private fun navigateToAgregarAlumnoManualActiviy(claseId: Long) {
        val intent = Intent(this, AgregarAlumnoManualActivity::class.java)
        intent.putExtra("CLASE_ID", claseId)
        startActivity(intent)
    }

    private fun navigateToClaseAlumnosDispositivosActivity(claseId: Long) {
        val intent = Intent(this, ClaseAlumnosDispositivosActivity::class.java)
        intent.putExtra("CLASE_ID", claseId)
        startActivity(intent)
        finish()
    }
}