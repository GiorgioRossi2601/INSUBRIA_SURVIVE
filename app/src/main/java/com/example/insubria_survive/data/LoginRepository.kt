package com.example.insubria_survive.data

import android.util.Log
import com.example.insubria_survive.data.model.LoggedInUser

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

object LoginRepository {
    val dataSource: LoginDataSource = LoginDataSource()

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    fun logout() {
        user = null
        dataSource.logout()
    }

    fun login(username: String, password: String, onAfterLogin: (Result<LoggedInUser>) -> Unit) {
        dataSource.login(username, password) { result ->
            onInternalLogin(result, onAfterLogin)
        }
    }

    private fun onInternalLogin(result: Result<LoggedInUser>, onAfterLogin: (Result<LoggedInUser>) -> Unit) {
        Log.d("LOGIN","EVENTO RAGGIUNTO 1")
        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }
        onAfterLogin(result);
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
        println("User information ${this.user} saved successfully")
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }


}