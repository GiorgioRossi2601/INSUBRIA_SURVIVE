package com.example.insubria_survive.utils

import android.util.Log
import com.google.firebase.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Classe helper contenente metodi per la conversione di date e Timestamp,
 * utilizzata per operazioni di formattazione in Firebase e nelle UI.
 */
class UtilsMethod {

    companion object {
        private const val TAG = "UtilsMethod"
    }

    /**
     * Converte una stringa formattata in "yyyy-MM-dd HH:mm" in un oggetto [Timestamp] (Firebase).
     *
     * @param dateString La stringa da convertire.
     * @return Un [Timestamp] se la conversione ha successo, altrimenti null.
     */
    fun stringToTimestamp(dateString: String): Timestamp? {
        if (dateString.isEmpty()) return null
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val date = sdf.parse(dateString)
            Timestamp(date!!)
        } catch (e: ParseException) {
            Log.e(TAG, "Errore nella conversione della stringa in Timestamp: $dateString", e)
            null
        }
    }

    /**
     * Converte un [Timestamp] Firebase in una stringa formattata in "yyyy-MM-dd HH:mm".
     *
     * @param timestamp Il [Timestamp] da convertire.
     * @return La data formattata.
     */
    fun firebaseTimestampToString(timestamp: Timestamp): String {
        val date = timestamp.toDate()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return sdf.format(date)
    }

    /**
     * Converte un [Timestamp] Firebase in una stringa formattata con data lunga (es. "dd MMMM yyyy").
     *
     * @param timestamp Il [Timestamp] da convertire.
     * @return La data formattata in formato lungo in italiano.
     */
    fun firebaseTimestampToDateLongFormat(timestamp: Timestamp): String {
        val date = timestamp.toDate()
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("it"))
        return sdf.format(date)
    }

    /**
     * Converte un [Timestamp] Firebase in una stringa che rappresenta solo l'orario (es. "HH:mm").
     *
     * @param timestamp Il [Timestamp] da convertire.
     * @return La stringa contenente solo l'orario.
     */
    fun firebaseTimestampToTimeOnly(timestamp: Timestamp): String {
        val date = timestamp.toDate()
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(date)
    }
}
