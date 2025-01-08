package com.example.insubria_survive.ui.esami

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.insubria_survive.data.model.Esame
import com.example.insubria_survive.R

class EsamiAdapter(private var esami: List<Esame>, private val onEsameStatusClick: (Esame) -> Unit) : RecyclerView.Adapter<EsamiAdapter.EsameViewHolder>() {

    class EsameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNomeEsame: TextView = itemView.findViewById(R.id.tvNomeEsame)
        val tvDataEsame: TextView = itemView.findViewById(R.id.tvDataEsame)
        val tvAulaEsame: TextView = itemView.findViewById(R.id.tvAulaEsame)
        val tvPadiglioneEsame: TextView = itemView.findViewById(R.id.tvPadiglioneEsame)
        val btPreferenzeEsame: TextView = itemView.findViewById(R.id.btPreferenzeEsame)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EsameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_esami, parent, false)
        return EsameViewHolder(view)
    }

    override fun onBindViewHolder(holder: EsameViewHolder, position: Int) {
        val esame = esami[position]
        holder.tvNomeEsame.text = esame.corso ?: "Corso non disponibile"
        // Converte il Timestamp in una stringa leggibile
        val dataFormattata = esame.data?.toDate()?.let { date ->
            java.text.SimpleDateFormat("dd MMMM yyyy", java.util.Locale.getDefault()).format(date)
        } ?: "Data non disponibile"
        holder.tvDataEsame.text = dataFormattata
        holder.tvAulaEsame.text = esame.aula
        holder.tvPadiglioneEsame.text = esame.padiglione

        // Gestione click del bottone
        holder.btPreferenzeEsame.setOnClickListener {
            onEsameStatusClick(esame)
        }
    }

    override fun getItemCount(): Int = esami.size

    fun updateData(newEsami: List<Esame>) {
        esami = newEsami
        notifyDataSetChanged()
    }
}
