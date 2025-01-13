package com.example.insubria_survive.ui.preferenze

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.insubria_survive.R
import com.example.insubria_survive.data.model.EsameConPreferenza
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Adapter per visualizzare la lista degli esami con preferenza.
 *
 * @param data Lista di EsameConPreferenza.
 * @param onItemClick Lambda da eseguire al click sull'item.
 */
class PreferenzeAdapter(
    private var data: List<EsameConPreferenza>,
    private val onItemClick: (EsameConPreferenza) -> Unit
) : RecyclerView.Adapter<PreferenzeAdapter.EsamiPreferitiViewHolder>() {

    // Tag per il logging
    companion object {
        private const val TAG = "PreferenzeAdapter"
    }

    /**
     * ViewHolder per l'item di Preferenza.
     */
    inner class EsamiPreferitiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvExamName: TextView = itemView.findViewById(R.id.tvExamName)
        val tvExamDate: TextView = itemView.findViewById(R.id.tvExamDate)
        val tvExamAula: TextView = itemView.findViewById(R.id.tvExamAula)
        val tvExamPadiglione: TextView = itemView.findViewById(R.id.tvExamPadiglione)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EsamiPreferitiViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_preferenza, parent, false)
        Log.d(TAG, "ViewHolder creato per item_preferenza")
        return EsamiPreferitiViewHolder(view)
    }

    override fun onBindViewHolder(holder: EsamiPreferitiViewHolder, position: Int) {
        val item = data[position]
        Log.d(TAG, "Binding item in posizione $position per esame ${item.esame.id}")

        // Imposta il nome del corso (con fallback)
        holder.tvExamName.text = item.esame.corso ?: "Esame non disponibile"

        // Format della data in una stringa leggibile
        val formattedDate = item.esame.data?.toDate()?.let { date ->
            SimpleDateFormat("dd MMM yyyy - HH:mm", Locale.getDefault()).format(date)
        } ?: "Data non disponibile"
        holder.tvExamDate.text = formattedDate

        holder.tvExamAula.text = item.esame.aula ?: "Aula non disponibile"
        holder.tvExamPadiglione.text = "Padiglione ${item.esame.padiglione ?: "N/D"}"

        // Imposta il click listener per l'item
        holder.itemView.setOnClickListener {
            Log.d(TAG, "Item cliccato: esame ${item.esame.id}")
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = data.size

    /**
     * Aggiorna la lista di dati visualizzati e notifica il RecyclerView.
     *
     * @param listaEsamiPreferiti Nuova lista di EsameConPreferenza.
     */
    fun updatePreferenze(listaEsamiPreferiti: List<EsameConPreferenza>) {
        Log.d(TAG, "Aggiornamento dati: ${listaEsamiPreferiti.size} elementi ricevuti")
        data = listaEsamiPreferiti
        notifyDataSetChanged()
    }
}
