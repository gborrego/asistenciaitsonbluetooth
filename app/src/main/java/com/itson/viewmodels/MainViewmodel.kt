package com.itson.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itson.models.Alumno
import com.itson.models.Clase
import com.itson.repositories.AlumnosRepository
import com.itson.repositories.ClasesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.opencsv.CSVReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

//Los viewmodels se utilizan para guardar el estado de los datos en cada pantalla, por cada activity hay un viewmodel
@HiltViewModel
class MainViewmodel @Inject constructor(
    //Para utilizar los repositorios los inyectamos con la anotacion @Inject, estos se obtienen del modulo
    //en el paquete de repositories
    private val clasesRepository: ClasesRepository
) : ViewModel() {

    //Los campos con un "_" al inicio son campos privados, ya que son modificables.
    private val _clases = MutableLiveData<List<Clase>>()
    //Los campos con el mismo nombre pero sin "_", son la manera publica no modificable con la que podemos obtener estos datos desde la activity.
    //Se utiliza el tipo de livedata para que se nos notifique en la activity si se realizo algun cambio en esta variable y asi hacer los cambios necesarios en la UI.
    val clases: LiveData<List<Clase>> get() = _clases

    init {
        fetchClases()
    }

    private fun fetchClases() {
        viewModelScope.launch {
            val clasesList = withContext(Dispatchers.IO) {
                clasesRepository.getAll()
            }
            _clases.postValue(clasesList)
        }
    }
}