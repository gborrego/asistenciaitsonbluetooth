package com.itson.utils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageButton
import com.itson.R
import com.itson.models.Alumno

class AlumnoAdapter(private val alumnoList: List<Alumno>) :
    RecyclerView.Adapter<AlumnoAdapter.AlumnoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlumnoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alumno, parent, false)
        return AlumnoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlumnoViewHolder, position: Int) {
        holder.bind(alumnoList[position])
    }

    override fun getItemCount(): Int = alumnoList.size

    class AlumnoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val idTextView: TextView = itemView.findViewById(R.id.textViewid)
        private val nombreTextView: TextView = itemView.findViewById(R.id.textViewNombre)
        private val porcentajeTextView: TextView = itemView.findViewById(R.id.textViewPorcentaje)
        private val dispositivoImageButton: ImageButton = itemView.findViewById(R.id.imageButton)

        fun bind(alumno: Alumno) {
            idTextView.text = alumno.matricula.toString()
            nombreTextView.text = alumno.nombre
            porcentajeTextView.text = "100%"
            // Falta agregar listener al dispositivoImageButton
        }
    }
}