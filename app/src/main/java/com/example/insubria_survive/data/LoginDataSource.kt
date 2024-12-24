package com.example.insubria_survive.data

import android.util.Log
import com.example.insubria_survive.data.model.LoggedInUser
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {
    val db = Firebase.firestore

    fun login(
        username: String,
        password: String,
        onLoginCompleted: (Result<LoggedInUser>) -> Unit
    ) {
        db.collection("users")
            .whereEqualTo("username", username)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { documents ->
                Log.d("LOGIN","CALL OK")
                if (!documents.isEmpty) {
                    var user = documents.elementAt(0)
                    onLoginCompleted(
                        Result.Success(
                            LoggedInUser(
                                user.getString("id"),
                                user.getString("username"),
                                user.getString("nome"),
                                user.getString("cognome")
                            )
                        )
                    )
                }
                else
                    onLoginCompleted(Result.Error(IOException("Error logging in")))

            }
            .addOnFailureListener { e ->
                onLoginCompleted(Result.Error(IOException("Error logging in", e)))
            }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}