package com.itson

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.itson.models.Alumno
import com.itson.models.Clase
import com.itson.repositories.AlumnosRepository
import com.itson.repositories.ClasesRepository
import com.itson.ui.theme.AsistenciaTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity() : ComponentActivity(){

    @Inject
    lateinit var alumnosRepository: AlumnosRepository;

    @Inject
    lateinit var clasesRepository: ClasesRepository;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AsistenciaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
        guardarAlumno()
        imprimirAlumnos()
        guardarClase()
        imprimirClases()
    }

    private fun guardarClase(){
       val clase = Clase(nombre= "agiles", alias= "a", ciclo= "verano", instructor = "borrego")
        clasesRepository.insert(clase);
    }

    private fun imprimirClases(){
        val clases = clasesRepository.getAll();
        Log.i("Clases", clases.toString());
    }

    private fun guardarAlumno(){
       val alumno = Alumno(matricula = 229499, nombre = "Miguel3", apellido = "frias2")
       alumnosRepository.insert(alumno)
    }

    private fun imprimirAlumnos(){
        val alumnos = alumnosRepository.getAll();
        Log.i("Alumnos", alumnos.toString());
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AsistenciaTheme {
        Greeting("Android")
    }
}