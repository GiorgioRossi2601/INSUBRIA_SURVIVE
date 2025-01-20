package com.example.insubria_survive.ui.timeline

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.insubria_survive.data.model.Padiglione
import com.example.insubria_survive.R
import android.widget.TextView
import com.example.insubria_survive.data.model.Esame
import com.example.insubria_survive.ui.esami.EsamiAdapter.OnItemClickListener


class TimelineAdapter(
    private var padiglioni: List<Padiglione>
): RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder>() {

    /**
     * Interfaccia per gestire il click sull'intero item.
     */
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    companion object{
        private const val TAG = "TimelineAdapter"
    }

    private var listener: OnItemClickListener? = null

    /**
     * Imposta il listener per il click sull'intero item.
     */
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    /**
     * Restituisce l'esame presente in una determinata posizione.
     *
     * @param position La posizione dell'item all'interno della lista.
     */
    fun getPadiglioneAt(position: Int): Padiglione = padiglioni[position]

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
        holder.apply {
            tvCodicePad.text = padiglione.codice_padiglione
            tvDescrizionePad.text = padiglione.descrizione
            val geoPad = padiglione.posizione
            tvPosizionePad.text = if (geoPad != null) {
                "Lat: ${geoPad.latitude}, Lon: ${geoPad.longitude}"
            } else {
                "Posizione non disponibile"
            }
            tvOrari.text = "Aperto dalle ${padiglione.ora_apertura} alle ${padiglione.ora_chiusura}"

            // Click sull'intero item
            itemView.setOnClickListener {
                Log.d(TAG, "Item cliccato in posizione: $position")
                listener?.onItemClick(position)
            }
        }
    }

    override fun getItemCount(): Int = padiglioni.size

    // Aggiorna la lista e notifica l'adapter
    fun updateData(newPadiglioni: List<Padiglione>) {
        padiglioni = newPadiglioni
        notifyDataSetChanged()
    }

}