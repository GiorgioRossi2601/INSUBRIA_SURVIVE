package com.example.insubria_survive.ui.lezioni

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.insubria_survive.data.model.LezioniListItem
import com.example.insubria_survive.data.model.LezioniListItem.LessonItem
import com.example.insubria_survive.data.model.LezioniListItem.NoLessonItem
import com.example.insubria_survive.data.model.LezioniListItem.WeekHeader
import com.example.insubria_survive.databinding.ItemLezioniBinding
import com.example.insubria_survive.databinding.ItemNoLezioniBinding
import com.example.insubria_survive.databinding.ItemWeekHeaderBinding
import com.example.insubria_survive.utils.UtilsMethod

class LezioniAdapter(
    // Lambda per gestire il click sul bottone Calendario nell'item lezione
    private val onCalendarioClick: (com.example.insubria_survive.data.model.Lezione) -> Unit
) : ListAdapter<LezioniListItem, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        private const val VIEW_TYPE_WEEK_HEADER = 0
        private const val VIEW_TYPE_LESSON_ITEM = 1
        private const val VIEW_TYPE_NO_LESSON = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = getItem(position)) {
            is WeekHeader -> VIEW_TYPE_WEEK_HEADER
            is LessonItem -> VIEW_TYPE_LESSON_ITEM
            is NoLessonItem -> VIEW_TYPE_NO_LESSON
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_WEEK_HEADER -> {
                val binding = ItemWeekHeaderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false)
                WeekHeaderViewHolder(binding)
            }
            VIEW_TYPE_NO_LESSON -> {
                val binding = ItemNoLezioniBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false)
                NoLessonViewHolder(binding)
            }
            else -> { // VIEW_TYPE_LESSON_ITEM
                val binding = ItemLezioniBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false)
                LessonViewHolder(binding, onCalendarioClick)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is WeekHeader -> (holder as WeekHeaderViewHolder).bind(item)
            is LessonItem -> (holder as LessonViewHolder).bind(item)
            is NoLessonItem -> (holder as NoLessonViewHolder).bind()
        }
    }

    class WeekHeaderViewHolder(private val binding: ItemWeekHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WeekHeader) {
            binding.tvWeekHeader.text = item.title
        }
    }

    class LessonViewHolder(
        private val binding: ItemLezioniBinding,
        private val onCalendarioClick: (com.example.insubria_survive.data.model.Lezione) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LessonItem) {
            binding.tvCorso.text = item.lesson.corso

            val utils = UtilsMethod()
            // Format della data nel formato "dd/MM/yyyy"
            val dataTesto = item.lesson.data_inizio?.let { utils.firebaseTimestampToDateLongFormat(it) } ?: ""
            binding.tvData.text = dataTesto

            // Orario formattato come "HH:mm - HH:mm"
            val oraInizio = item.lesson.data_inizio?.let { utils.firebaseTimestampToTimeOnly(it) } ?: ""
            val oraFine   = item.lesson.data_fine?.let { utils.firebaseTimestampToTimeOnly(it) } ?: ""
            val oraTesto = if (oraInizio.isNotEmpty() && oraFine.isNotEmpty()) {
                "$oraInizio - $oraFine"
            } else {
                ""
            }
            binding.tvOra.text = oraTesto

            // Altri binding (ad esempio Aula e Padiglione)
            binding.tvAula.text = item.lesson.aula
            binding.tvPadiglione.text = item.lesson.padiglione

            binding.btCalendario.setOnClickListener {
                onCalendarioClick(item.lesson)
            }
        }
    }

    class NoLessonViewHolder(private val binding: ItemNoLezioniBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.tvNoLezioni.text = "Nessuna lezione prevista"
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<LezioniListItem>() {
        override fun areItemsTheSame(oldItem: LezioniListItem, newItem: LezioniListItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: LezioniListItem, newItem: LezioniListItem): Boolean {
            return oldItem == newItem
        }
    }
}
