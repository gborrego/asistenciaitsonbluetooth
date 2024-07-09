package com.itson.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.itson.R
import com.itson.utils.ClaseAdapter
import com.itson.viewmodels.CrearClaseViewmodel
import com.itson.viewmodels.MainViewmodel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewmodel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val claseRecyclerView: RecyclerView = findViewById(R.id.clase_recycler_view)
        val createButton: Button = findViewById(R.id.create_boton)

        createButton.setOnClickListener {
            navigateToCrearClaseActivity()
        }

        viewModel.clases.observe(this, Observer {
            if (it != null) {
                claseRecyclerView.adapter = ClaseAdapter(it) { clase ->
                    if (clase.id != null) {
                        navigateToClaseActivity(clase.id)
                    }
                }
                claseRecyclerView.layoutManager = LinearLayoutManager(this)
            }
        })
    }

    private fun navigateToCrearClaseActivity() {
        val intent = Intent(this, CrearClaseActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToClaseActivity(claseId: Long) {
        val intent = Intent(this, ClaseActivity::class.java)
        intent.putExtra("CLASE_ID", claseId)
        startActivity(intent)
    }
}
