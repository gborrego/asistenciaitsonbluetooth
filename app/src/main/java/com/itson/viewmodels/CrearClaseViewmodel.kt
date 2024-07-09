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
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

@HiltViewModel
class CrearClaseViewmodel @Inject constructor(
    private val application: Application
) : ViewModel() {

    @Inject
    lateinit var clasesRepository: ClasesRepository;

    @Inject
    lateinit var alumnosRepository: AlumnosRepository;

    private val _alumnos = MutableLiveData<List<Alumno>>()
    val alumnos: LiveData<List<Alumno>> get() = _alumnos

    private val _alias= MutableLiveData<String>()
    val alias: LiveData<String> get() = _alias

    private val _nombre= MutableLiveData<String>()
    val nombre: LiveData<String> get() = _nombre

    private val _ciclo= MutableLiveData<String>()
    val ciclo: LiveData<String> get() = _ciclo

    private val _instructor= MutableLiveData<String>()
    val instructor: LiveData<String> get() = _instructor

    fun setAlias(alias: String) {
        _alias.value = alias
    }

    fun parseCSV(uri: Uri){
        viewModelScope.launch {
            val alumnosList = mutableListOf<Alumno>()
            try {
                application.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val reader =
                        CSVReader(InputStreamReader(inputStream, StandardCharsets.ISO_8859_1))
                    val metadatos = mutableListOf<String>()
                    repeat(10) { metadatos.add(reader.readNext().joinToString()) }

                    _nombre.postValue(metadatos[4].split(":")[2].trim())
                    _ciclo.postValue(metadatos[5].split(":")[2].trim())
                    _instructor.postValue(metadatos[7].split(":")[1].trim())

                    reader.forEach { values ->
                        if (values.size >= 2) {
                            val matricula = values[0].trim()
                            val nombreCompleto = values[1].trim().split(',');
                            val nombre = nombreCompleto[1];
                            val apellido = nombreCompleto[0];
                            alumnosList.add(Alumno(matricula.toLong(), nombre, apellido, null))
                        }
                    }
                    _alumnos.postValue(alumnosList);
                    inputStream.close()
                }
            }
            catch (e: Exception){
                e.printStackTrace();
            }
        }
    }
    fun createClase(): Clase? {
        val nombre = _nombre.value
        val alias = _alias.value
        val ciclo = _ciclo.value
        val instructor = _instructor.value
        val alumnos = _alumnos.value


        if (nombre != null && alias != null && ciclo != null && instructor != null && alumnos != null) {
            val clase = Clase(
                id = null,
                nombre = nombre,
                alias = alias,
                ciclo = ciclo,
                instructor = instructor,
            )
            clasesRepository.insert(clase)
            val savedClase = clasesRepository.getLast() ?: return null;
            for (alumno in alumnos){
                alumnosRepository.getById(alumno.matricula) ?: alumnosRepository.insert(alumno);
                alumnosRepository.setToClase(alumno,savedClase);
            }
            return savedClase;
        } else {
            return null
        }
    }
}