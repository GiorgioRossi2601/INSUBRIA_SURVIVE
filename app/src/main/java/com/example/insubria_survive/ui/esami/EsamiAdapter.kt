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

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    // Tag per il logging
    companion object {
        private const val TAG = "EsamiAdapter"
    }

    private var listener: onItemClickListener?=null

    fun setOnItemClickListener(listener: onItemClickListener){
        this.listener=listener
    }
    /**
     * Restituisce l'esame presente in una determinata posizione.
     *
     * @param position La posizione dell'item all'interno della lista.
     * @return L'oggetto Esame corrispondente.
     */
    fun getEsameAt(position: Int): Esame {
        return esami[position]
    }

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
        // Inflate del layout per l'item e creazione del view holder
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_esami, parent, false)
        Log.d(TAG, "ViewHolder creato per il layout item_esami")
        return EsameViewHolder(view)
    }

    override fun onBindViewHolder(holder: EsameViewHolder, position: Int) {
        val esame = esami[position]
        Log.d(TAG, "Associazione dati per l'item in posizione: $position")

        // Imposta il testo per il nome del corso (con fallback)
        holder.tvNomeEsame.text = esame.corso ?: "Corso non disponibile"

        // Converte il Timestamp in una stringa formattata
        val dataFormattata = esame.data?.toDate()?.let { date ->
            SimpleDateFormat("dd MMMM yyyy - HH:mm", Locale.getDefault()).format(date)
        } ?: "Data non disponibile"
        holder.tvDataEsame.text = dataFormattata

        // Imposta aula e padiglione
        holder.tvAulaEsame.text = esame.aula ?: ""
        holder.tvPadiglioneEsame.text = esame.padiglione ?: ""

        // Gestione click del bottone per le preferenze
        holder.btPreferenzeEsame.setOnClickListener {
            Log.d(TAG, "Click su preferenze per esame: ${esame.id}")
            onEsameStatusClick(esame)
        }

        // Gestione click sull'intero item
        holder.itemView.setOnClickListener {
            // Verifica che listener non sia null e passa la posizione
            Log.d(TAG, "Click sull'item a posizione $position")
            listener?.onItemClick(position)
        }
    }

    override fun getItemCount(): Int = esami.size


    /**
     * Aggiorna la lista degli esami e notifica il RecyclerView.
     *
     * @param newEsami Nuova lista di esami.
     */
    fun updateData(newEsami: List<Esame>) {
        Log.d(TAG, "Aggiornamento dati: nuovi esami ricevuti (${newEsami.size})")
        esami = newEsami
        notifyDataSetChanged()
    }
}
