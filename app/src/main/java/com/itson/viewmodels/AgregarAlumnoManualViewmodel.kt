package com.itson.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.itson.models.Alumno
import com.itson.repositories.AlumnosRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AgregarAlumnoManualViewmodel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var alumnosRepository: AlumnosRepository;

    @Inject
    lateinit var dispositivosRepository: AlumnosRepository;

    private val _idAlumno = MutableLiveData<String>()
    val idAlumno: LiveData<String> get() = _idAlumno

    private val _nombreAlumno = MutableLiveData<String>()
    val nombreAlumno: LiveData<String> get() = _nombreAlumno

    private val _apellidosAlumno = MutableLiveData<String>()
    val apellidosAlumno: LiveData<String> get() = _apellidosAlumno

    private val _dispositivoVinculado = MutableLiveData<Boolean>()
    val dispositivoVinculado: LiveData<Boolean> get() = _dispositivoVinculado

    fun setIdAlumno(id: String) {
        _idAlumno.value = id
    }

    fun setNombreAlumno(nombre: String) {
        _nombreAlumno.value = nombre
    }

    fun setApellidosAlumno(apellidos: String) {
        _apellidosAlumno.value = apellidos
    }

    fun setDispositivoVinculado(vinculado: Boolean) {
        _dispositivoVinculado.value = vinculado
    }

    fun createAlumno(): Boolean {
        val id = _idAlumno.value?.toLong();
        val nombre = _nombreAlumno.value
        val apellidos = _apellidosAlumno.value
        val findAlumno = id?.let { alumnosRepository.getById(it) }
        val dispositivo = _dispositivoVinculado.value

        if (findAlumno == null && id != null && apellidos != null && nombre != null && dispositivo != null){
            val alumno = Alumno(id,nombre,apellidos,null)
            alumnosRepository.insert(alumno)
            return true
        }
        return false
    }
}