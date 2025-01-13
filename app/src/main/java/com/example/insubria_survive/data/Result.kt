package com.example.insubria_survive.data

/**
 * Classe sigillata che rappresenta il risultato di un'operazione con il relativo stato.
 *
 * @param T Il tipo di dati restituito in caso di successo.
 */
sealed class Result<out T : Any> {

    /**
     * Rappresenta il caso di successo, con i dati ottenuti.
     *
     * @param data I dati ottenuti dall'operazione.
     */
    data class Success<out T : Any>(val data: T) : Result<T>()

    /**
     * Rappresenta il caso in cui si verifica un errore durante l'operazione.
     *
     * @param exception L'eccezione generata.
     */
    data class Error(val exception: Exception) : Result<Nothing>()

    /**
     * Metodo che restituisce una stringa rappresentativa dello stato del risultato.
     */
    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
        }
    }
}
