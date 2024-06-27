package com.itson

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class StudentPairingActivity : AppCompatActivity() {

    private lateinit var studentListView: ListView
    private lateinit var deviceListView: ListView
    private lateinit var pairButton: Button

    private val students = mutableListOf<String>()
    private val devices = mutableListOf<BluetoothDeviceCustom>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_devices)

        studentListView = findViewById(R.id.student_list_view)
        deviceListView = findViewById(R.id.bluetooth_device_list_view)
        pairButton = findViewById(R.id.pair_button)

        loadStudents()
        loadDevices()

        pairButton.setOnClickListener {

            val selectedDeviceIndex = deviceListView.checkedItemPosition
            val selectedStudentIndex = studentListView.checkedItemPosition

            if (selectedDeviceIndex != ListView.INVALID_POSITION && selectedStudentIndex != ListView.INVALID_POSITION) {

                val selectedDevice = devices[selectedDeviceIndex]
                val selectedStudent = students[selectedStudentIndex]


                Toast.makeText(
                    this,
                    "Vinculado: $selectedStudent con ${selectedDevice.name}",
                    Toast.LENGTH_SHORT
                ).show()


                devices.removeAt(selectedDeviceIndex)
                students.removeAt(selectedStudentIndex)


                updateDeviceListView()
                updateStudentListView()
            } else {
                Toast.makeText(this, "Seleccione un dispositivo y un alumno primero", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadStudents() {

        students.addAll(listOf("Omar Felix", "Lucia Perez", "Juan Gomez", "Maria Rodriguez", "Pedro Sanchez"))
        updateStudentListView()
    }

    private fun loadDevices() {
        devices.addAll(listOf(
            BluetoothDeviceCustom("Xioami G6", "00:11:22:33:44:55"),
            BluetoothDeviceCustom("iPhone de Maria", "66:77:88:99:AA:BB"),
            BluetoothDeviceCustom("Poco X5", "CC:DD:EE:FF:00:11"),
            BluetoothDeviceCustom("Moto G10", "22:33:44:55:66:77"),
            BluetoothDeviceCustom("Galaxy 22", "88:99:AA:BB:CC:DD")
        ))
        updateDeviceListView()
    }

    private fun updateStudentListView() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, students)
        studentListView.adapter = adapter
    }

    private fun updateDeviceListView() {
        val deviceNames = devices.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, deviceNames)
        deviceListView.adapter = adapter
    }
}

data class BluetoothDeviceCustom(val name: String, val address: String)