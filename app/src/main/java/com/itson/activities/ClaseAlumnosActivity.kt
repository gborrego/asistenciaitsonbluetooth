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
import com.itson.viewmodels.ClaseAlumnosViewModel

class ClaseAlumnosActivity : AppCompatActivity() {

    private val viewModel: ClaseAlumnosViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_list)

        val claseId = intent.getLongExtra("CLASE_ID", -1)

        if (claseId != -1L) {
            viewModel.fetchClase(claseId)
            viewModel.fetchAlumnosList(claseId)
        }

        val nombreGrupoTextView: TextView = findViewById(R.id.nombreGrupo)
        val botonLista: Button = findViewById(R.id.botonLista)
        val botonAlumnos: Button = findViewById(R.id.botonAlumnos)
        val addButton: Button = findViewById(R.id.add_button)
        val recyclerView: RecyclerView = findViewById(R.id.lista_alumnos_recycler_view)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Adapter for RecyclerView
        //val adapter = AlumnosAdapter()
        //recyclerView.adapter = adapter

        viewModel.clase.observe(this, Observer { clase ->
            nombreGrupoTextView.text = clase.nombre
        })

        viewModel.alumnoList.observe(this, Observer { alumnosList ->
            //adapter.submitList(alumnosList)
        })

        botonLista.setOnClickListener {

        }

        botonAlumnos.setOnClickListener {
            // Handle button click
        }

        addButton.setOnClickListener {
            // Handle button click
        }
    }

    private fun navigateToClaseAsistenciasActivity(claseId: Long) {
        val intent = Intent(this, ClaseAsistenciasActivity::class.java)
        intent.putExtra("CLASE_ID", claseId)
        startActivity(intent)
    }
}