package com.example.insubria_survive.ui.preferenze

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.insubria_survive.R
import com.example.insubria_survive.data.model.EsameConPreferenza
import com.example.insubria_survive.data.model.Preferenza
import java.text.SimpleDateFormat
import java.util.Locale

class PreferenzeAdapter(
    private var data: List<EsameConPreferenza>,
    private val onItemClick: (EsameConPreferenza) -> Unit
) : RecyclerView.Adapter<PreferenzeAdapter.ExamViewHolder>() {

    inner class ExamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvExamName: TextView = itemView.findViewById(R.id.tvExamName)
        val tvExamDate: TextView = itemView.findViewById(R.id.tvExamDate)
        val tvExamAula: TextView = itemView.findViewById(R.id.tvExamAula)
        val tvExamPadiglione: TextView = itemView.findViewById(R.id.tvExamPadiglione)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_preferenza, parent, false)
        return ExamViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExamViewHolder, position: Int) {
        val item = data[position]
        holder.tvExamName.text = item.esame.corso ?: "Esame non disponibile"

        // Formattazione della data
        val formattedDate = item.esame.data?.toDate()?.let { date ->
            SimpleDateFormat("dd MMM yyyy - HH:mm", Locale.getDefault()).format(date)
        } ?: "Data non disponibile"
        holder.tvExamDate.text = "Data: $formattedDate"

        holder.tvExamAula.text = "Aula: ${item.esame.aula ?: "N/D"}"
        holder.tvExamPadiglione.text = "Padiglione: ${item.esame.padiglione ?: "N/D"}"

        // Imposta lo sfondo del container in base allo stato
        val backgroundColor = when (item.stato) {
            "DA_FARE" -> holder.itemView.context.getColor(R.color.colorDaFare)  // definito in colors.xml
            "NON_FARE" -> holder.itemView.context.getColor(R.color.colorNonFare)
            else -> holder.itemView.context.getColor(R.color.colorInForse)
        }
        // Se il layout radice del layout item è il CardView, ad esempio:
        holder.itemView.setBackgroundColor(backgroundColor)


        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = data.size

    fun updatePreferenze(newData: List<EsameConPreferenza>) {
        data = newData
        notifyDataSetChanged()
    }
}