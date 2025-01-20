package com.example.insubria_survive.ui.timeline

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.insubria_survive.data.model.Padiglione
import com.example.insubria_survive.R
import android.widget.TextView

class TimelineAdapter(
    private var padiglioni: List<Padiglione>
): RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder>() {

    class TimelineViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvCodicePad: TextView = itemView.findViewById(R.id.tvCodicePad)
        val tvDescrizionePad: TextView = itemView.findViewById(R.id.tvDescrizionePad)
        val tvPosizionePad: TextView = itemView.findViewById(R.id.tvPosizionePad)
        val tvOrari: TextView = itemView.findViewById(R.id.tvOrariPad)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_timeline_padiglioni, parent, false)
        return TimelineViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        val padiglione = padiglioni[position]
        holder.tvCodicePad.text = padiglione.codice_padiglione
        holder.tvDescrizionePad.text = padiglione.descrizione
        val geoPad = padiglione.posizione
        holder.tvPosizionePad.text = if (geoPad != null) {
            "Lat: ${geoPad.latitude}, Lon: ${geoPad.longitude}"
        } else {
            "Posizione non disponibile"
        }
        holder.tvOrari.text = "Aperto dalle ${padiglione.ora_apertura} alle ${padiglione.ora_chiusura}"
    }

    override fun getItemCount(): Int = padiglioni.size

    // Aggiorna la lista e notifica l'adapter
    fun updateData(newPadiglioni: List<Padiglione>) {
        padiglioni = newPadiglioni
        notifyDataSetChanged()
    }

}