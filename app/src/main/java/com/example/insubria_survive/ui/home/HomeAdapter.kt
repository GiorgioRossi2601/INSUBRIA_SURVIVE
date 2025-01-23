package com.example.insubria_survive.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.insubria_survive.R

/**
 * Adapter per visualizzare la lista delle sezioni della Home in un RecyclerView.
 *
 * @property sezioni Lista di [SezioneHome] da mostrare.
 */
class HomeAdapter(
    private val sezioni: List<SezioneHome>
) : RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    companion object {
        private const val TAG = "HomeAdapter"
    }

    /**
     * Interfaccia per la gestione del click sull'intero item.
     */
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var listener: OnItemClickListener? = null

    /**
     * Imposta il listener per il click sull'intero item.
     */
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    /**
     * ViewHolder per l'item della home.
     */
    class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewIcon)
        val textViewSubtitle: TextView = itemView.findViewById(R.id.textViewSubtitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home, parent, false)
        return HomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val sezione = sezioni[position]
        holder.apply {
            imageView.setImageResource(sezione.immagine)
            textViewSubtitle.setText(sezione.testo)
            itemView.setOnClickListener {
                Log.d(TAG, "onBindViewHolder: Click sull'item in posizione $position")
                listener?.onItemClick(position)
            }
        }
    }

    override fun getItemCount(): Int = sezioni.size
}
