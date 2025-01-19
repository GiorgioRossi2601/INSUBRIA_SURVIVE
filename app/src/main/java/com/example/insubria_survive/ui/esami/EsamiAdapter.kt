package com.example.insubria_survive.ui.esami

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.insubria_survive.data.model.Esame
import com.example.insubria_survive.R
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Adapter per la visualizzazione della lista di esami in un RecyclerView.
 *
 * @param esami Lista degli esami da visualizzare.
 * @param onEsameStatusClick Lambda da eseguire al click sul bottone delle preferenze.
 */
class EsamiAdapter(
    private var esami: List<Esame>,
    private val onEsameStatusClick: (Esame) -> Unit
) : RecyclerView.Adapter<EsamiAdapter.EsameViewHolder>() {

    /**
     * Interfaccia per gestire il click sull'intero item.
     */
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    companion object {
        private const val TAG = "EsamiAdapter"
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
    fun getEsameAt(position: Int): Esame = esami[position]

    /**
     * ViewHolder per l'item dell'esame.
     */
    class EsameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNomeEsame: TextView = itemView.findViewById(R.id.tvNomeEsame)
        val tvDataEsame: TextView = itemView.findViewById(R.id.tvDataEsame)
        val tvAulaEsame: TextView = itemView.findViewById(R.id.tvAulaEsame)
        val tvPadiglioneEsame: TextView = itemView.findViewById(R.id.tvPadiglioneEsame)
        val btPreferenzeEsame: TextView = itemView.findViewById(R.id.btPreferenzeEsame)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EsameViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_esami, parent, false)
        Log.d(TAG, "onCreateViewHolder: ViewHolder creato per il layout item_esami")
        return EsameViewHolder(view)
    }

    override fun onBindViewHolder(holder: EsameViewHolder, position: Int) {
        val esame = esami[position]
        Log.d(TAG, "onBindViewHolder: Associazione dati per l'item in posizione: $position")
        holder.apply {
            tvNomeEsame.text = esame.corso ?: "Corso non disponibile"
            val dataFormattata = esame.data?.toDate()?.let { date ->
                SimpleDateFormat("dd MMMM yyyy - HH:mm", Locale.getDefault()).format(date)
            } ?: "Data non disponibile"
            tvDataEsame.text = dataFormattata
            tvAulaEsame.text = esame.aula.orEmpty()
            tvPadiglioneEsame.text = esame.padiglione.orEmpty()

            // Gestione click sul bottone delle preferenze
            btPreferenzeEsame.setOnClickListener {
                Log.d(TAG, "Bottone preferenze cliccato per esame: ${esame.id}")
                onEsameStatusClick(esame)
            }

            // Click sull'intero item
            itemView.setOnClickListener {
                Log.d(TAG, "Item cliccato in posizione: $adapterPosition")
                listener?.onItemClick(adapterPosition)
            }
        }
    }

    override fun getItemCount(): Int = esami.size

    /**
     * Aggiorna la lista degli esami e notifica il RecyclerView.
     *
     * @param newEsami Nuova lista di esami.
     */
    fun updateData(newEsami: List<Esame>) {
        Log.d(TAG, "updateData: Aggiornamento dati, nuovi esami ricevuti: ${newEsami.size}")
        esami = newEsami
        notifyDataSetChanged()
    }
}
