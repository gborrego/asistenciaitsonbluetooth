package com.itson.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.itson.R
import com.itson.repositories.AlumnosRepository
import com.itson.viewmodels.AgregarAlumnoManualViewmodel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AgregarAlumnoManualActivity : AppCompatActivity() {

    private val viewModel: AgregarAlumnoManualViewmodel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_manual)

        val claseId = intent.getLongExtra("CLASE_ID", -1)

        if (claseId != -1L) {
            viewModel.fetchClase(claseId)
        }

        val idAlumnoEditText: EditText = findViewById(R.id.edittext_id_alumno)
        val nombreAlumnoEditText: EditText = findViewById(R.id.edittext_nombre_alumno)
        val apellidosAlumnoEditText: EditText = findViewById(R.id.edittext_apellidos_alumno)
        val dispositivoButton: ImageButton = findViewById(R.id.imageButton_device)
        val createButton: Button = findViewById(R.id.create_button)

        dispositivoButton.setOnClickListener {
            viewModel.setDispositivoVinculado(true)
        }

        createButton.setOnClickListener {
            val idAlumno = idAlumnoEditText.text.toString()
            val nombreAlumno = nombreAlumnoEditText.text.toString()
            val apellidosAlumno = apellidosAlumnoEditText.text.toString()

            if (idAlumno.isEmpty()) {
                Toast.makeText(this, "Por favor, complete los campos necesarios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val idLong = idAlumno.toLong()

            if (viewModel.checkIfAlumnoExists(idLong)){
                if (viewModel.addExistingAlumno(idLong)) navigateToClaseAlumnosActivity(claseId) else Toast.makeText(this, "No se pudo agregar al alumno", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nombreAlumno.isEmpty() || apellidosAlumno.isEmpty()){
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (viewModel.addNewAlumno()) navigateToClaseAlumnosActivity(claseId) else Toast.makeText(this, "No se pudo crear al alumno", Toast.LENGTH_SHORT).show()
        }
        setupObservers(idAlumnoEditText, nombreAlumnoEditText, apellidosAlumnoEditText)
        setupEditTextListeners(idAlumnoEditText, nombreAlumnoEditText, apellidosAlumnoEditText)
    }

    private fun setupObservers(idAlumnoEditText: EditText, nombreAlumnoEditText: EditText, apellidosAlumnoEditText: EditText) {
        viewModel.idAlumno.observe(this, Observer { idAlumno ->
            if (idAlumnoEditText.text.toString() != idAlumno) {
                idAlumnoEditText.setText(idAlumno)
            }
        })

        viewModel.nombreAlumno.observe(this, Observer { nombreAlumno ->
            if (nombreAlumnoEditText.text.toString() != nombreAlumno) {
                nombreAlumnoEditText.setText(nombreAlumno)
            }
        })

        viewModel.apellidosAlumno.observe(this, Observer { apellidosAlumno ->
            if (apellidosAlumnoEditText.text.toString() != apellidosAlumno) {
                apellidosAlumnoEditText.setText(apellidosAlumno)
            }
        })

        viewModel.dispositivoVinculado.observe(this, Observer { vinculado ->
            if (vinculado) {
                Toast.makeText(this, "Dispositivo vinculado", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupEditTextListeners(idAlumnoEditText: EditText, nombreAlumnoEditText: EditText, apellidosAlumnoEditText: EditText) {
        idAlumnoEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setIdAlumno(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        nombreAlumnoEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setNombreAlumno(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        apellidosAlumnoEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setApellidosAlumno(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun navigateToClaseAlumnosActivity(claseId: Long) {
        val intent = Intent(this, ClaseAlumnosActivity::class.java)
        intent.putExtra("CLASE_ID", claseId)
        startActivity(intent)
        finish()
    }
}