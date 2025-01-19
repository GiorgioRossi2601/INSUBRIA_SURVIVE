package com.example.insubria_survive.ui.esami

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.insubria_survive.calendario.CalendarManager
import com.example.insubria_survive.data.LoginRepository
import com.example.insubria_survive.data.db.LocalDbRepository
import com.example.insubria_survive.data.model.Esame
import com.example.insubria_survive.data.model.Stato
import com.example.insubria_survive.databinding.FragmentEsamiBinding
import com.example.insubria_survive.ui.preferenze.CambiaStatoDialogFragment
import com.example.insubria_survive.utils.ConfirmAddEventDialogFragment
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

    companion object {
        private const val TAG = "EsamiFragment"
    }

    private var _binding: FragmentEsamiBinding? = null
    private val binding get() = _binding!!

    private lateinit var esamiAdapter: EsamiAdapter
    private lateinit var esamiViewModel: EsamiViewModel

    // Variabili per gestire il login con Google
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>

    // Variabile per memorizzare temporaneamente l'esame da salvare in caso di login necessario
    private var pendingExam: Esame? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: Inizializzazione del fragment")
        _binding = FragmentEsamiBinding.inflate(inflater, container, false)

        // Inizializzazione del repository locale e del ViewModel tramite factory
        val repository = LocalDbRepository(requireContext())
        val factory = EsamiViewModelFactory(repository)
        esamiViewModel = ViewModelProvider(this, factory).get(EsamiViewModel::class.java)

        setupGoogleSignIn()
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
            // Richiediamo lo scope Calendar per gestire il calendario
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
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account: GoogleSignInAccount? = task.getResult(Exception::class.java)
                    if (account != null) {
                        Toast.makeText(requireContext(), "Login Google riuscito", Toast.LENGTH_SHORT)
                            .show()
                        pendingExam?.let { exam ->
                            addExamToCalendarWithAccount(exam, account)
                            pendingExam = null
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Errore nel recupero dell'account Google: ${e.message}", e)
                    Toast.makeText(requireContext(), "Errore login Google", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(requireContext(), "Login annullato", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Gestisce il click sull'esame per aggiungerlo al calendario.
     * Se l'utente non è loggato, lancia il flusso di login.
     */
    private fun handleExamClick(esame: Esame) {
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        if (account != null) {
            addExamToCalendarWithAccount(esame, account)
        } else {
            pendingExam = esame
            signInLauncher.launch(googleSignInClient.signInIntent)
        }
    }

    /**
     * Crea il GoogleAccountCredential e richiama il CalendarManager per aggiungere l'evento.
     */
    private fun addExamToCalendarWithAccount(esame: Esame, account: GoogleSignInAccount) {
        val credential = GoogleAccountCredential.usingOAuth2(
            requireContext(),
            listOf(CalendarScopes.CALENDAR)
        ).apply {
            selectedAccount = account.account
        }
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
     * Configura il RecyclerView e l'adapter per la lista degli esami.
     */
    private fun setupRecyclerView() {
        esamiAdapter = EsamiAdapter(emptyList()) { esame ->
            // Mostra il dialog per la modifica dello stato dell'esame
            showEsameStatusDialog(esame)
        }
        binding.recyclerViewEsami.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = esamiAdapter
        }
        Log.d(TAG, "setupRecyclerView: RecyclerView configurato correttamente")

        // Imposta il listener per il click sull'intero item
        esamiAdapter.setOnItemClickListener(object : EsamiAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val esameSelezionato = esamiAdapter.getEsameAt(position)
                Toast.makeText(
                    requireContext(),
                    "Hai cliccato sull'item: ${esameSelezionato.corso}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d(TAG, "onItemClick: Click sull'item: ${esameSelezionato.id}")
                showConfermaAggiuntaCalendario(esameSelezionato)
            }
        })
    }

    /**
     * Osserva le modifiche alla lista degli esami e aggiorna l'interfaccia di conseguenza.
     */
    private fun observeViewModel() {
        esamiViewModel.esamiList.observe(viewLifecycleOwner) { esami ->
            if (!esami.isNullOrEmpty()) {
                Log.d(TAG, "observeViewModel: Esami caricati: ${esami.size}")
                esamiAdapter.updateData(esami)
            } else {
                Log.d(TAG, "observeViewModel: Lista esami vuota")
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
     * Mostra il dialog per la modifica dello stato dell'esame.
     *
     * @param esame L'esame selezionato.
     */
    private fun showEsameStatusDialog(esame: Esame) {
        Log.d(TAG, "showEsameStatusDialog: Mostro dialog per lo stato dell'esame: ${esame.id}")
        val repository = LocalDbRepository(requireContext())
        val username = LoginRepository.user?.username.orEmpty()
        val preferenza = repository.getPreferenzaByEsameAndUser(esame.id, username)
        val statoCorrente = preferenza?.stato?.let { Stato.valueOf(it) } ?: Stato.IN_FORSE

        val dialog = CambiaStatoDialogFragment.newInstance(esame, statoCorrente)
        dialog.show(parentFragmentManager, "CambiaStatoDialogFragment")
    }

    /**
     * Mostra il dialog per la conferma del salvataggio dell'evento su calendario.
     *
     * @param esame L'esame da inserire.
     */
    private fun showConfermaAggiuntaCalendario(esame: Esame) {
        Log.d(TAG, "showConfermaAggiuntaCalendario: Mostro dialog per conferma aggiunta calendario: ${esame.id}")
        val dialog = ConfirmAddEventDialogFragment().apply {
            callback = { dialogResult ->
                if (dialogResult == "si") {
                    handleExamClick(esame)
                } else {
                    Toast.makeText(requireContext(), "Operazione annullata", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialog.show(parentFragmentManager, "ConfirmAddEventDialogFragment")
    }
}
