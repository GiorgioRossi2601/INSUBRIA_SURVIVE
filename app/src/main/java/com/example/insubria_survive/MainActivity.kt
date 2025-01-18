package com.example.insubria_survive

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.insubria_survive.data.LoginRepository
import com.example.insubria_survive.databinding.ActivityMainBinding
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.security.ProviderInstaller
import com.google.android.material.navigation.NavigationView

/**
 * MainActivity è il punto di ingresso dell'applicazione.
 * Gestisce il Drawer, la Navigation e mostra i dati dell'utente loggato nella header.
 */
class MainActivity : AppCompatActivity() {

    // Tag per il logging
    companion object {
        private const val TAG = "MainActivity"
    }

    // Configurazione per la navigazione (usata per gestire le destinazioni top-level)
    private lateinit var appBarConfiguration: AppBarConfiguration

    // Binding per accedere agli elementi del layout
    private lateinit var binding: ActivityMainBinding

    // Repository per gestire i dati di login (singleton)
    private val loginRepository: LoginRepository = LoginRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate: Inizializzazione MainActivity")

        // Inizializza il binding e imposta il layout della Activity
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Imposta la toolbar come ActionBar
        setSupportActionBar(binding.appBarMain.toolbar)

        // Configurazione del DrawerLayout e NavigationView
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        // Imposta il testo dell'utente nella header del NavigationView
        val headerView: View = navView.getHeaderView(0)
        val textViewUsername: TextView = headerView.findViewById(R.id.usernameUtente)
        // Componiamo il nome completo dell'utente o mostriamo un fallback se non presente
        val userDisplayName = loginRepository.user?.let { "${it.nome} ${it.cognome}" } ?: "Nessun utente"
        textViewUsername.text = userDisplayName
        Log.d(TAG, "onCreate: Utente visualizzato nella header -> $userDisplayName")

        // Configura il NavController per la navigazione tra le destinazioni
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Configura le destinazioni top-level (che non mostreranno il bottone "Up")
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_exams,
                R.id.nav_lessons,
                R.id.nav_timeline,
                R.id.nav_navigator,
                R.id.nav_preferences
            ),
            drawerLayout
        )
        // Configura la ActionBar e il NavigationView con il NavController
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        Log.d(TAG, "onCreate: Configurazione completata")
        try {
            ProviderInstaller.installIfNeeded(this)
        } catch (e: GooglePlayServicesRepairableException) {
            Log.w("ProviderInstaller", "Google Play Services necessitano di aggiornamento.", e)
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.connectionStatusCode, 0)?.show()
        } catch (e: GooglePlayServicesNotAvailableException) {
            Log.w("ProviderInstaller", "Google Play Services non supportati.", e)
            e.printStackTrace()
        }
        Log.d(TAG, "onCreate: Configurazione completata xdvr")
    }

    /**
     *  menu della ActionBar.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate del menu: aggiunge elementi alla ActionBar se presente
        menuInflater.inflate(R.menu.main, menu)
        Log.d(TAG, "onCreateOptionsMenu: Menu creato")
        return true
    }

    /**
     * Gestisce il comportamento del navigateUp (bottone Up sulla ActionBar)
     */
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        val navigated = navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
        Log.d(TAG, "onSupportNavigateUp: navigateUp restituisce $navigated")
        return navigated
    }

    /**
     * Metodo chiamato al click sul menu "Esci".
     * Esegue il logout e chiude l'activity.
     */
    fun onEsciClick(item: MenuItem) {
        Log.d("MainActivity", "onEsciClick: Logout in corso")
        loginRepository.logout()
        finish()
        Log.d(TAG, "onEsciClick: Activity terminata")
    }
}
