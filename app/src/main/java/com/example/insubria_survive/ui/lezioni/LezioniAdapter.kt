package com.example.insubria_survive.ui.lezioni

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.insubria_survive.data.model.LezioniListItem
import com.example.insubria_survive.data.model.LezioniListItem.LessonItem
import com.example.insubria_survive.data.model.LezioniListItem.WeekHeader
import com.example.insubria_survive.databinding.ItemLezioniBinding
import com.example.insubria_survive.databinding.ItemWeekHeaderBinding

class LezioniAdapter : ListAdapter<LezioniListItem, RecyclerView.ViewHolder>(DiffCallback()) {

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
                LessonViewHolder(binding)
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

    class LessonViewHolder(private val binding: ItemLezioniBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LessonItem) {
            binding.tvCorso.text = item.lesson.corso
            binding.tvDataInizio.text = item.lesson.data_inizio.toString()
            binding.tvAula.text = item.lesson.aula
            // Altri binding se necessario
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<LezioniListItem>() {
        override fun areItemsTheSame(oldItem: LezioniListItem, newItem: LezioniListItem): Boolean {
            // Se possibile, confronta gli ID degli elementi oppure la classe + il contenuto
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: LezioniListItem, newItem: LezioniListItem): Boolean {
            return oldItem == newItem
        }
    }
}
