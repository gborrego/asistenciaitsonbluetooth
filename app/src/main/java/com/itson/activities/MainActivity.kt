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
import com.itson.R
import com.itson.viewmodels.CrearClaseViewmodel
import com.itson.viewmodels.MainViewmodel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewmodel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val claseListView: ListView = findViewById(R.id.group_list_view)
        val createButton: Button = findViewById(R.id.create_boton)

        createButton.setOnClickListener {
            navigateToCrearClaseActivity()
        }

        viewModel.clases.observe(this, Observer {
            val adapter = ArrayAdapter(
                this, // Contexto
                android.R.layout.simple_list_item_1, // Layout para cada item
                it
            )
            claseListView.adapter = adapter;
        })
    }

    private fun navigateToCrearClaseActivity() {
        val intent = Intent(this, CrearClaseActivity::class.java)
        startActivity(intent)
    }

}