package com.example.insubria_survive.ui.preferenze

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.insubria_survive.R
import com.example.insubria_survive.data.model.Preferenza

class PreferenzeAdapter(
    private var preferenze: List<Preferenza>,
    private val onItemClick: (Preferenza) -> Unit
) : RecyclerView.Adapter<PreferenzeAdapter.PreferenzaViewHolder>() {

    class PreferenzaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvEsameCodice: TextView = itemView.findViewById(R.id.tvEsameCodice)
        val tvStatoPreferenza: TextView = itemView.findViewById(R.id.tvStatoPreferenza)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreferenzaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_preferenza, parent, false)
        return PreferenzaViewHolder(view)
    }

    override fun onBindViewHolder(holder: PreferenzaViewHolder, position: Int) {
        val preferenza = preferenze[position]
        holder.tvEsameCodice.text = preferenza.esame_codice ?: "Esame non disponibile"
        holder.tvStatoPreferenza.text = preferenza.stato ?: "Stato non disponibile"

        holder.itemView.setOnClickListener {
            onItemClick(preferenza)
        }
    }

    override fun getItemCount(): Int = preferenze.size

    fun updatePreferenze(newPreferenze: List<Preferenza>) {
        preferenze = newPreferenze
        notifyDataSetChanged()
    }
}