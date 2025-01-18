package com.example.insubria_survive.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.insubria_survive.ui.esami.EsamiAdapter
import com.example.insubria_survive.R
import android.widget.ImageView
import android.widget.TextView
import com.example.insubria_survive.data.model.SezioneHome

class HomeAdapter(
    private val sezioni: List<SezioneHome>,
): RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    companion object {
        private const val TAG = "HomeAdapter"
    }

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    private var listener: HomeAdapter.onItemClickListener?=null

    fun setOnItemClickListener(listener: HomeAdapter.onItemClickListener){
        this.listener=listener
    }

    class HomeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val imageView: ImageView = itemView.findViewById(R.id.imageViewIcon)
        val textViewSubtitle: TextView = itemView.findViewById(R.id.textViewSubtitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_home, parent, false))
    }
    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val sezione= sezioni[position]
        holder.imageView.setImageResource(sezione.immagine)
        holder.textViewSubtitle.setText(sezione.testo)
        // Gestione click sull'intero item
        holder.itemView.setOnClickListener {
            // Verifica che listener non sia null e passa la posizione
            Log.d(TAG, "Click sull'item a posizione $position")
            listener?.onItemClick(position)
        }
    }
    override fun getItemCount(): Int {
        return sezioni.size
    }


}