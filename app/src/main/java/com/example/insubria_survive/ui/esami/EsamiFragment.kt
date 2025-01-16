package com.example.insubria_survive.ui.esami

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.insubria_survive.calendario.CalendarManager
import com.example.insubria_survive.data.LoginRepository
import com.example.insubria_survive.data.db.LocalDbRepository
import com.example.insubria_survive.data.model.Esame
import com.example.insubria_survive.data.model.Stato
import com.example.insubria_survive.databinding.FragmentEsamiBinding
import com.example.insubria_survive.ui.preferenze.CambiaStatoDialogFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.calendar.CalendarScopes

/**
 * Fragment per la visualizzazione degli esami.
 */
class EsamiFragment : Fragment() {

    // Tag per il logging
    companion object {
        private const val TAG = "EsamiFragment"
    }

    // Binding per il layout del Fragment
    private var _binding: FragmentEsamiBinding? = null
    private val binding get() = _binding!!

    private lateinit var esamiAdapter: EsamiAdapter
    private lateinit var esamiViewModel: EsamiViewModel

    // Variabili per gestire il login con Google
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: inizializzazione del fragment")

        // Inizializzazione del repository locale e del ViewModel tramite factory
        val repository = LocalDbRepository(requireContext())
        val factory = EsamiViewModelFactory(repository)
        esamiViewModel = ViewModelProvider(this, factory).get(EsamiViewModel::class.java)

        _binding = FragmentEsamiBinding.inflate(inflater, container, false)

        // Configuriamo Google Sign In
        setupGoogleSignIn()
        // Registriamo il launcher per il flusso di login
        setupSignInLauncher()
        setupRecyclerView()
        observeViewModel()

        return binding.root
    }

    /**
     * Configura il client di Google Sign In.
     */
    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            // Richiediamo anche lo scope per il Calendar (necessario per manipolare il calendario)
            .requestScopes(Scope(CalendarScopes.CALENDAR))
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    /**
     * Registra l'ActivityResultLauncher che gestirà il risultato del login Google.
     */
    private fun setupSignInLauncher() {
        signInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Otteniamo l'account Google dal risultato
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account: GoogleSignInAccount? = task.getResult(Exception::class.java)
                    if (account != null) {
                        // Procediamo con l'inserimento dell'evento usando il calendario.
                        // Per esempio, in questo caso potresti memorizzare l'account e poi ripetere l'azione richiesta.
                        // Qui mostriamo un Toast di conferma.
                        Toast.makeText(requireContext(), "Login Google riuscito", Toast.LENGTH_SHORT).show()
                        // Esempio: riprova l'azione che aveva scatenato il login
                        // (ad es. salvare l'esame nel calendario)
                        // Supponiamo di aver memorizzato l'esame in una variabile temporanea
                        pendingExam?.let { exam ->
                            addExamToCalendarWithAccount(exam, account)
                            pendingExam = null
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Errore nel recupero dell'account Google: ${e.message}", e)
                    Toast.makeText(requireContext(), "Errore login Google", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Login annullato", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Variabile per memorizzare temporaneamente l'esame da salvare in caso di login necessario
    private var pendingExam: Esame? = null

    /**
     * Funzione che gestisce l'aggiunta dell'esame al calendario.
     * Se l'utente non è loggato, avvia il flusso di login.
     */
    private fun handleExamClick(esame: Esame) {
        // Verifichiamo se esiste già un account Google loggato
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        if (account != null) {
            // L'utente è già loggato: procediamo con l'inserimento nel calendario
            addExamToCalendarWithAccount(esame, account)
        } else {
            // L'utente non è loggato: memorizziamo l'esame in attesa e lanciamo il flusso di login
            pendingExam = esame
            val signInIntent = googleSignInClient.signInIntent
            signInLauncher.launch(signInIntent)
        }
    }

    /**
     * Metodo che crea il GoogleAccountCredential e richiama il CalendarManager per aggiungere l'evento.
     */
    private fun addExamToCalendarWithAccount(esame: Esame, account: GoogleSignInAccount) {
        // Creiamo il credential utilizzando l'account Google ottenuto
        val credential = GoogleAccountCredential.usingOAuth2(
            requireContext(),
            listOf(CalendarScopes.CALENDAR)
        )
        credential.selectedAccount = account.account

        val calendarManager = CalendarManager(requireContext(), credential)
        calendarManager.addExamToCalendar(esame) { success, info ->
            requireActivity().runOnUiThread {
                if (success) {
                    Toast.makeText(
                        requireContext(),
                        "Evento creato con successo!\nVisualizzalo qui: $info",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Errore nella creazione dell'evento: $info",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    /**
     * Configura il RecyclerView e l'adapter per la lista di esami.
     */
    private fun setupRecyclerView() {
        esamiAdapter = EsamiAdapter(emptyList()) { esame ->
            // Mostriamo anche il dialog per lo stato dell'esame (già implementato)
            showEsameStatusDialog(esame)
        }
        binding.recyclerViewEsami.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = esamiAdapter
        }
        Log.d(TAG, "RecyclerView configurato correttamente")

        // Imposta il listener per il click sull'intero item della RecyclerView
        esamiAdapter.setOnItemClickListener(object : EsamiAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                // Utilizzo del metodo getEsameAt per recuperare direttamente l'Esame
                val esameSelezionato = esamiAdapter.getEsameAt(position)
                Toast.makeText(
                    requireContext(),
                    "Hai cliccato sull'item: ${esameSelezionato.corso}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d(TAG, "Click sull'item: ${esameSelezionato.id}")
                // Eventuali ulteriori azioni (es. navigazione o aggiornamenti)
                showConfermaAggiuntaCalendario(esameSelezionato)
            }

        })
    }

    /**
     * Osserva le modifiche alla lista di esami e aggiorna l'UI di conseguenza.
     */
    private fun observeViewModel() {
        esamiViewModel.esamiList.observe(viewLifecycleOwner) { esami ->
            if (!esami.isNullOrEmpty()) {
                Log.d(TAG, "Esami caricati: ${esami.size}")
                esamiAdapter.updateData(esami)
            } else {
                Log.d(TAG, "Lista esami vuota")
                Toast.makeText(requireContext(), "Nessun esame trovato.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d(TAG, "onDestroyView: binding nulled")
    }

    /**
     * Mostra il dialog per la modifica dello stato dell'esame, basandosi sulla preferenza salvata.
     *
     * @param esame L'esame selezionato.
     */
    private fun showEsameStatusDialog(esame: Esame) {
        Log.d(TAG, "Mostro dialog per lo stato dell'esame: ${esame.id}")

        val repository = LocalDbRepository(requireContext())
        // Ottiene l'username dell'utente loggato (se disponibile)
        val username = LoginRepository.user?.username.orEmpty()
        // Recupera la preferenza esistente, se presente
        val preferenza = repository.getPreferenzaByEsameAndUser(esame.id, username)
        val statoCorrente = preferenza?.stato?.let { Stato.valueOf(it) } ?: Stato.IN_FORSE

        // Mostra il dialog con lo stato corrente
        val dialog = CambiaStatoDialogFragment.newInstance(esame, statoCorrente)
        dialog.show(parentFragmentManager, "CambiaStatoDialogFragment")
    }

    private fun showConfermaAggiuntaCalendario(esame: Esame) {
        Log.d(TAG, "Mostro dialog per la conferma del salvataggio dell'evento su calendario: ${esame.id}")

        // Mostra il dialog con lo stato corrente
        val dialog = ConfirmAddEventDialogFragment()
        dialog.callback = { dialogResult ->
            // Al click sull'item, gestiamo il login (se necessario) e poi l'inserimento dell'evento nel calendario
            if (dialogResult == "si")
                handleExamClick(esame)
            else{
                Toast.makeText(requireContext(), "Operazione annullata", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show(parentFragmentManager, "CambiaStatoDialogFragment")
    }


}
