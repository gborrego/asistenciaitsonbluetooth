package com.itson.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.itson.R
import com.itson.models.Alumno
import com.itson.models.Clase
import com.itson.viewmodels.CrearClaseViewmodel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CrearClaseActivity : AppCompatActivity() {
    private val viewModel: CrearClaseViewmodel by viewModels()

    private val PICK_CSV_REQUEST = 1

    private lateinit var editTextAlias: EditText
    private lateinit var textViewClase: TextView
    private lateinit var textViewCiclo: TextView
    private lateinit var textViewInstructor: TextView
    private lateinit var textViewAlumnos: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_group)

        editTextAlias = findViewById(R.id.edittext_alias)
        textViewClase = findViewById(R.id.textview_clase)
        textViewCiclo = findViewById(R.id.textview_ciclo)
        textViewInstructor = findViewById(R.id.textview_instructor)
        textViewAlumnos = findViewById(R.id.textview_alumnos)

        val button = findViewById<Button>(R.id.button_select_file)
        button.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "text/*"
            }
            startActivityForResult(intent, PICK_CSV_REQUEST)
        }

        val createButton: Button = findViewById(R.id.create_boton)
        createButton.setOnClickListener {
            val alias = editTextAlias.text.toString()
            viewModel.setAlias(alias)
            val createdClase = viewModel.createClase()
            if( createdClase == null){
                Toast.makeText(this, "No se pudo crear la clase", Toast.LENGTH_SHORT).show()
            } else{
                navigateToClaseAlumnosDispositivosActivity()
            }
        }

        viewModel.nombre.observe(this, Observer { nombre ->
            textViewClase.text = "Clase: $nombre"
        })

        viewModel.ciclo.observe(this, Observer { ciclo ->
            textViewCiclo.text = "Ciclo: $ciclo"
        })

        viewModel.instructor.observe(this, Observer { instructor ->
            textViewInstructor.text = "Instructor: $instructor"
        })

        viewModel.alumnos.observe(this, Observer { alumnos ->
            val stringBuilder = StringBuilder()
            for (alumno in alumnos) {
                stringBuilder.append("${alumno.matricula}   ${alumno.nombre}\n")
            }
            textViewAlumnos.text = stringBuilder.toString()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_CSV_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                viewModel.parseCSV(uri)
            }
        }
    }

    private fun navigateToClaseAlumnosDispositivosActivity() {
        val intent = Intent(this, ClaseAlumnosDispositivosActivity::class.java)
        val alumnos = viewModel.alumnos.value?.let { ArrayList(it) }
        intent.putParcelableArrayListExtra("ALUMNOS", alumnos);
        startActivity(intent)
    }

}