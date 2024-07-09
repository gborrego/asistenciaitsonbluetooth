package com.itson.activities

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.itson.R
import com.itson.utils.AsistenciaAdapter
import com.itson.viewmodels.ClaseViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClaseActivity : AppCompatActivity() {

    private val viewModel: ClaseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list)

        val claseId = intent.getLongExtra("CLASE_ID", -1)

        if (claseId != -1L) {
            viewModel.fetchClase(claseId)
            viewModel.fetchAsistenciaList(claseId)
        }

        //val claseNameTextView: TextView = findViewById(R.id.textViewNombreClase)
        val attendanceRecyclerView: RecyclerView = findViewById(R.id.lista_recycler_view)
        val listButton: Button = findViewById(R.id.list_boton)
        val studentsButton: Button = findViewById(R.id.students_boton)
        val attendanceButton: Button = findViewById(R.id.attendance_boton)

        viewModel.clase.observe(this, Observer { clase ->
              Log.i("Clase", clase.toString())
        //    claseNameTextView.text = clase.nombre
        })

        viewModel.asistenciaList.observe(this, Observer { asistenciaList ->
            attendanceRecyclerView.adapter = AsistenciaAdapter(asistenciaList)
            attendanceRecyclerView.layoutManager = LinearLayoutManager(this)
        })

        listButton.setOnClickListener {
        }

        studentsButton.setOnClickListener {
        }

        attendanceButton.setOnClickListener {
        }
    }
}