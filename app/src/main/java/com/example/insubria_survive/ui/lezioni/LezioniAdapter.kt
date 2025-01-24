package com.example.insubria_survive.ui.lezioni

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.insubria_survive.data.model.Lezione
import com.example.insubria_survive.ui.lezioni.LezioniListItem.*
import com.example.insubria_survive.databinding.*
import com.example.insubria_survive.utils.UtilsMethod
import com.example.insubria_survive.R

/**
 * Adapter per la visualizzazione della lista delle lezioni.
 *
 * Gestisce tre tipologie di item:
 * - Header della settimana (WeekHeader)
 * - Item lezione (LessonItem)
 * - Item "Nessuna lezione" (NoLessonItem)
 *
 * @param onCalendarioClick Callback per gestire il click sul bottone Calendario, passando la [Lezione] corrispondente.
 */
class LezioniAdapter(
    private val onCalendarioClick: (Lezione) -> Unit
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
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_WEEK_HEADER -> {
                val binding = ItemWeekHeaderBinding.inflate(inflater, parent, false)
                WeekHeaderViewHolder(binding)
            }

            VIEW_TYPE_NO_LESSON -> {
                val binding = ItemNoLezioniBinding.inflate(inflater, parent, false)
                NoLessonViewHolder(binding)
            }

            else -> { // VIEW_TYPE_LESSON_ITEM
                val binding = ItemLezioniBinding.inflate(inflater, parent, false)
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

    /**
     * ViewHolder per l'header della settimana.
     */
    class WeekHeaderViewHolder(private val binding: ItemWeekHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WeekHeader) {
            binding.tvWeekHeader.text = item.title
        }
    }

    /**
     * ViewHolder per un item lezione.
     */
    class LessonViewHolder(
        private val binding: ItemLezioniBinding,
        private val onCalendarioClick: (Lezione) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LessonItem) {
            binding.apply {
                tvCorso.text = item.lesson.corso

                val utils = UtilsMethod()
                // Format della data nel formato "dd/MM/yyyy"
                tvData.text =
                    item.lesson.data_inizio?.let { utils.firebaseTimestampToDateLongFormat(it) }
                        ?: ""

                // Orario formattato come "HH:mm - HH:mm"
                val oraInizio =
                    item.lesson.data_inizio?.let { utils.firebaseTimestampToTimeOnly(it) } ?: ""
                val oraFine =
                    item.lesson.data_fine?.let { utils.firebaseTimestampToTimeOnly(it) } ?: ""
                tvOra.text = if (oraInizio.isNotEmpty() && oraFine.isNotEmpty()) {
                    "$oraInizio - $oraFine"
                } else {
                    ""
                }

                // Imposta aula e padiglione
                tvAula.text = item.lesson.aula.orEmpty()
                tvPadiglione.text = item.lesson.padiglione.orEmpty()

                // Gestione click sul bottone Calendario
                btCalendario.setOnClickListener {
                    onCalendarioClick(item.lesson)
                }
            }
        }
    }

    /**
     * ViewHolder per l'item "Nessuna lezione".
     */
    class NoLessonViewHolder(private val binding: ItemNoLezioniBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.tvNoLezioni.text = "Nessuna lezione prevista"
        }
    }

    /**
     * DiffUtil.ItemCallback per ottimizzare gli aggiornamenti degli item.
     */
    class DiffCallback : DiffUtil.ItemCallback<LezioniListItem>() {
        override fun areItemsTheSame(oldItem: LezioniListItem, newItem: LezioniListItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: LezioniListItem,
            newItem: LezioniListItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}
