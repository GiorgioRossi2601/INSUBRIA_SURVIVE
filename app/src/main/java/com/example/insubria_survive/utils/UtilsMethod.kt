package com.example.insubria_survive.utils

import android.util.Log
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

class UtilsMethod {

    companion object{
        private const val TAG = "UtilsMethod"

    }
    /**
     * Converte una stringa formattata in "yyyy-MM-dd HH:mm" in un oggetto Timestamp (Firebase).
     *
     * @param dateString La stringa da convertire.
     * @return Timestamp? L'oggetto Timestamp se la conversione ha successo, altrimenti null.
     */
    final fun stringToTimestamp(dateString: String): Timestamp? {
        if (dateString.isEmpty()) return null
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val date = sdf.parse(dateString)
            Timestamp(date!!)
        } catch (e: Exception) {
            Log.e(TAG, "Errore nella conversione della stringa in Timestamp: $dateString", e)
            null
        }
    }

    final fun firebaseTimestampToString(timestamp: Timestamp): String {
        // Converti il Timestamp in oggetto Date
        val date = timestamp.toDate()
        // Definisci il formato desiderato
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        // Ritorna la data formattata in stringa
        return sdf.format(date)
    }
}