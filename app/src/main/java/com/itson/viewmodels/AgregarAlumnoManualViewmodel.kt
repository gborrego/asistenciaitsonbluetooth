package com.itson.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itson.models.Alumno
import com.itson.models.Clase
import com.itson.repositories.AlumnosRepository
import com.itson.repositories.ClasesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AgregarAlumnoManualViewmodel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var alumnosRepository: AlumnosRepository;

    @Inject lateinit var clasesRepository: ClasesRepository;

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

    private val _clase = MutableLiveData<Clase>()
    val clase: LiveData<Clase> get() = _clase

    fun fetchClase(claseId: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                clasesRepository.getById(claseId)
            }.also {
                _clase.postValue(it)
            }
        }
    }

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

    fun addNewAlumno(): Boolean {
        val id = _idAlumno.value?.toLong();
        val nombre = _nombreAlumno.value
        val apellidos = _apellidosAlumno.value
        //Falta por implementar
        val dispositivo = _dispositivoVinculado.value
        val claseForAlumno = clase.value

        if (id != null && apellidos != null && nombre != null && claseForAlumno != null){
            val alumno = Alumno(id,nombre,apellidos,null)
            alumnosRepository.insert(alumno)
            alumnosRepository.setToClase(alumno, claseForAlumno)
            return true
        }
        return false
    }

    fun addExistingAlumno(id: Long): Boolean {
        val claseForAlumno = clase.value
        val findAlumno = alumnosRepository.getById(id)

        if (claseForAlumno == null || findAlumno == null) return false

        val claseContainsAlumno = clasesRepository.claseContainsAlumno(claseForAlumno, findAlumno)

        if (!claseContainsAlumno){
            alumnosRepository.setToClase(findAlumno, claseForAlumno)
            return true
        }
        return false
    }

    fun checkIfAlumnoExists(id: Long): Boolean {
        return alumnosRepository.getById(id) != null
    }

}