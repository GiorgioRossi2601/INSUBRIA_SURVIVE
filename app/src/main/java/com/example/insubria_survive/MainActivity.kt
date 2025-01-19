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
 * Gestisce il Navigation Drawer, la NavigationComponent e mostra i dati dell'utente loggato nella header.
 */
class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    // Repository per gestire i dati di login (singleton)
    private val loginRepository: LoginRepository = LoginRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Inizializzazione MainActivity")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Imposta la toolbar come ActionBar
        setSupportActionBar(binding.appBarMain.toolbar)

        // Configura DrawerLayout e NavigationView
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        // Imposta il testo dell'utente nella header del NavigationView
        val headerView: View = navView.getHeaderView(0)
        val textViewUsername: TextView = headerView.findViewById(R.id.usernameUtente)
        val userDisplayName = loginRepository.user?.let { "${it.nome} ${it.cognome}" } ?: "Nessun utente"
        textViewUsername.text = userDisplayName
        Log.d(TAG, "onCreate: Utente visualizzato nella header -> $userDisplayName")

        // Configura il NavController e le destinazioni top-level
        val navController = findNavController(R.id.nav_host_fragment_content_main)
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
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        Log.d(TAG, "onCreate: Configurazione completata")

        // Verifica l'aggiornamento dei Google Play Services per la sicurezza della connessione
        try {
            ProviderInstaller.installIfNeeded(this)
        } catch (e: GooglePlayServicesRepairableException) {
            Log.w(TAG, "Google Play Services necessitano di aggiornamento.", e)
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.connectionStatusCode, 0)?.show()
        } catch (e: GooglePlayServicesNotAvailableException) {
            Log.w(TAG, "Google Play Services non supportati.", e)
            e.printStackTrace()
        }
        Log.d(TAG, "onCreate: Configurazione completata")
    }

    /**
     * Inflates il menu della ActionBar.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        Log.d(TAG, "onCreateOptionsMenu: Menu creato")
        return true
    }

    /**
     * Gestisce il comportamento del bottone Up della ActionBar.
     */
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        val navigated = navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
        Log.d(TAG, "onSupportNavigateUp: navigateUp restituisce $navigated")
        return navigated
    }

    /**
     * Metodo invocato al click sul menu "Esci".
     * Effettua il logout e termina l'activity.
     */
    fun onEsciClick(item: MenuItem) {
        Log.d(TAG, "onEsciClick: Logout in corso")
        loginRepository.logout()
        finish()
        Log.d(TAG, "onEsciClick: Activity terminata")
    }
}
