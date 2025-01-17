package com.example.insubria_survive.ui.lezioni

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.insubria_survive.data.model.Lezione
import com.example.insubria_survive.data.model.LezioniListItem
import com.example.insubria_survive.data.model.LezioniListItem.LessonItem
import com.example.insubria_survive.data.model.LezioniListItem.WeekHeader
import com.example.insubria_survive.databinding.ItemLezioniBinding
import com.example.insubria_survive.databinding.ItemWeekHeaderBinding
import com.example.insubria_survive.utils.UtilsMethod

class LezioniAdapter(
    // Lambda chiamata al click sul bottone Calendario dell’item
    private val onCalendarioClick: (Lezione) -> Unit
) : ListAdapter<LezioniListItem, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        private const val VIEW_TYPE_WEEK_HEADER = 0
        private const val VIEW_TYPE_LESSON_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is WeekHeader -> VIEW_TYPE_WEEK_HEADER
            is LessonItem -> VIEW_TYPE_LESSON_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_WEEK_HEADER -> {
                val binding = ItemWeekHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                WeekHeaderViewHolder(binding)
            }
            else -> {
                val binding = ItemLezioniBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                LessonViewHolder(binding, onCalendarioClick)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is WeekHeader -> (holder as WeekHeaderViewHolder).bind(item)
            is LessonItem -> (holder as LessonViewHolder).bind(item)
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
        private val onCalendarioClick: (Lezione) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LessonItem) {
            binding.tvCorso.text = item.lesson.corso
            binding.tvDataInizio.text = item.lesson.data_inizio?.let {
                UtilsMethod().firebaseTimestampToString(it)
            } ?: ""
            binding.tvDataFine.text = item.lesson.data_fine?.let {
                UtilsMethod().firebaseTimestampToString(it)
            } ?: ""
            binding.tvAula.text = item.lesson.aula
            binding.tvPadiglioneEsame.text = item.lesson.padiglione

            // Click sul bottone Calendario: esegue la callback passata dal Fragment
            binding.btCalendario.setOnClickListener {
                onCalendarioClick(item.lesson)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<LezioniListItem>() {
        override fun areItemsTheSame(oldItem: LezioniListItem, newItem: LezioniListItem): Boolean {
            // Confronta gli elementi in base al contenuto (meglio utilizzare un ID se disponibile)
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: LezioniListItem, newItem: LezioniListItem): Boolean {
            return oldItem == newItem
        }
    }
}
