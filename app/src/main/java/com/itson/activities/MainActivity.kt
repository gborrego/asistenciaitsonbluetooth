package com.itson.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.itson.R
import com.itson.utils.ClaseAdapter
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

        //Observamos la lista livedata de clases, si existe un cambio en esta lista se muestra la nueva lista en pantalla
        //Cada clase de la lista tiene un OnClickListener donde al seleccionarla, nos lleva a dicha clase
        viewModel.clases.observe(this, Observer {clasesList ->
            if (clasesList != null) {
                claseRecyclerView.adapter = ClaseAdapter(clasesList) { clase ->
                    if (clase.id != null) {
                        navigateToClaseAsistenciasActivity(clase.id)
                    }
                }
                claseRecyclerView.layoutManager = LinearLayoutManager(this)
            }
        })
    }

    //Metodos con los cuales cambiamos de activity
    private fun navigateToCrearClaseActivity() {
        val intent = Intent(this, CrearClaseActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToClaseAsistenciasActivity(claseId: Long) {
        val intent = Intent(this, ClaseAsistenciasActivity::class.java)
        intent.putExtra("CLASE_ID", claseId)
        startActivity(intent)
    }

}
