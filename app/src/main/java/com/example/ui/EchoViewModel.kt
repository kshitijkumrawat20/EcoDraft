package com.example.ui

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiService
import com.example.audio.SpeechManager
import com.example.auth.RevenueCatConfig
import com.example.data.db.AppDatabase
import com.example.data.model.DraftEntry
import com.example.data.model.WeeklyPatternReport
import com.example.data.repository.DraftRepository
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.interfaces.UpdatedCustomerInfoListener
import com.revenuecat.purchases.purchaseWith
import com.revenuecat.purchases.restorePurchasesWith
import com.revenuecat.purchases.getCustomerInfoWith
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed interface Screen {
    object Timeline : Screen
    object VoiceCapture : Screen
    data class EntryDetail(val entryId: Long) : Screen
    object WeeklyPatterns : Screen
    object Settings : Screen
    object Paywall : Screen
}

enum class NavigationTab {
    DRAFTS,
    CAPTURE,
    REFLECT
}

class EchoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DraftRepository
    val speechManager: SpeechManager = SpeechManager(application, viewModelScope)

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Timeline)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _screenStack = mutableListOf<Screen>(Screen.Timeline)

    private val _activeTab = MutableStateFlow(NavigationTab.DRAFTS)
    val activeTab: StateFlow<NavigationTab> = _activeTab.asStateFlow()

    private val _isUntangling = MutableStateFlow(false)
    val isUntangling: StateFlow<Boolean> = _isUntangling.asStateFlow()

    private val _isGeneratingWeeklyPatterns = MutableStateFlow(false)
    val isGeneratingWeeklyPatterns: StateFlow<Boolean> = _isGeneratingWeeklyPatterns.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // Location options for recording
    val locationOptions = listOf("Recorded in Bedroom", "Bedside record", "Desk notebook", "Transit thought", "Late-night walk")
    private val _selectedLocation = MutableStateFlow("Recorded in Bedroom")
    val selectedLocation: StateFlow<String> = _selectedLocation.asStateFlow()

    // Quota tracking — free tier limit
    private val maxFreeEntries = 3

    // RevenueCat Entitlement State
    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    // Permission request flow: emits sampleText (nullable) when recording is requested
    private val _recordingRequested = MutableSharedFlow<String?>()
    val recordingRequested = _recordingRequested.asSharedFlow()

    // Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive.asStateFlow()

    // Manual text entry mode on the capture screen
    private val _isManualEntryMode = MutableStateFlow(false)
    val isManualEntryMode: StateFlow<Boolean> = _isManualEntryMode.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = DraftRepository(database.draftDao())
        initRevenueCat()
    }

    private fun initRevenueCat() {
        if (Purchases.isConfigured) {
            try {
                // Initial entitlement check
                Purchases.sharedInstance.getCustomerInfoWith(
                    onError = {
                        // Keep initial false state
                    },
                    onSuccess = { customerInfo ->
                        val entitled = customerInfo.entitlements[RevenueCatConfig.ENTITLEMENT_ID]?.isActive == true
                        _isPremium.value = entitled
                    }
                )

                // Live entitlement listener for instant updates without app restart
                Purchases.sharedInstance.updatedCustomerInfoListener = UpdatedCustomerInfoListener { customerInfo ->
                    val entitled = customerInfo.entitlements[RevenueCatConfig.ENTITLEMENT_ID]?.isActive == true
                    _isPremium.value = entitled
                }
            } catch (e: Exception) {
                // Fallback for safety
            }
        }
    }

    val draftsList: StateFlow<List<DraftEntry>> = repository.allDrafts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val latestWeeklyPattern: StateFlow<WeeklyPatternReport?> = repository.latestPattern
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Weekly entry count (rolling 7-day window from ViewModel creation)
    val weeklyEntryCount: StateFlow<Int> = repository.getWeeklyEntryCount(
        System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    // Dynamic quota check combining Room weekly count & RevenueCat entitlement
    val canRecordEntry: StateFlow<Boolean> = combine(weeklyEntryCount, isPremium) { count, premium ->
        premium || count < maxFreeEntries
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    // Search results — re-queries Room whenever the search query changes
    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults: StateFlow<List<DraftEntry>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) flowOf(emptyList())
            else repository.searchDrafts(query)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun navigateTo(screen: Screen) {
        // Gate Weekly Pattern Report behind Premium entitlement
        val targetScreen = if (screen is Screen.WeeklyPatterns && !isPremium.value) {
            Screen.Paywall
        } else {
            screen
        }

        if (_currentScreen.value != targetScreen) {
            _screenStack.add(targetScreen)
            _currentScreen.value = targetScreen
            updateActiveTab(targetScreen)
        }
    }

    private fun updateActiveTab(screen: Screen) {
        when (screen) {
            is Screen.Timeline -> _activeTab.value = NavigationTab.DRAFTS
            is Screen.VoiceCapture -> _activeTab.value = NavigationTab.CAPTURE
            is Screen.WeeklyPatterns -> _activeTab.value = NavigationTab.REFLECT
            else -> {}
        }
    }

    fun navigateBack(): Boolean {
        if (_screenStack.size > 1) {
            _screenStack.removeAt(_screenStack.lastIndex)
            val previous = _screenStack.last()
            _currentScreen.value = previous
            updateActiveTab(previous)
            return true
        } else if (_currentScreen.value !is Screen.Timeline) {
            _currentScreen.value = Screen.Timeline
            _screenStack.clear()
            _screenStack.add(Screen.Timeline)
            _activeTab.value = NavigationTab.DRAFTS
            return true
        }
        return false
    }

    fun selectTab(tab: NavigationTab) {
        _activeTab.value = tab
        when (tab) {
            NavigationTab.DRAFTS -> navigateTo(Screen.Timeline)
            NavigationTab.CAPTURE -> requestRecording()
            NavigationTab.REFLECT -> {
                if (!isPremium.value) {
                    navigateTo(Screen.Paywall)
                } else {
                    navigateTo(Screen.WeeklyPatterns)
                }
            }
        }
    }

    /**
     * Called by any UI element to request recording.
     * Gates 4th entry behind Paywall when free quota is exceeded.
     */
    fun requestRecording(sampleText: String? = null) {
        if (!canRecordEntry.value && sampleText == null) {
            showMessage("Free weekly limit reached. Unlock Premium for unlimited voice entries.")
            navigateTo(Screen.Paywall)
            return
        }
        viewModelScope.launch {
            _recordingRequested.emit(sampleText)
        }
    }

    fun startVoiceCapture(sampleText: String? = null) {
        _isManualEntryMode.value = false
        speechManager.startListening(sampleText)
        navigateTo(Screen.VoiceCapture)
    }

    /** Switch to manual text entry on the capture screen. */
    fun enableManualEntryMode() {
        _isManualEntryMode.value = true
        speechManager.discard()
    }

    fun disableManualEntryMode() {
        _isManualEntryMode.value = false
    }

    /** Submit a manually typed thought for AI categorization. */
    fun submitManualEntry(text: String) {
        if (text.isBlank()) {
            showMessage("Write something before submitting")
            return
        }

        viewModelScope.launch {
            _isUntangling.value = true
            try {
                val untangled = GeminiService.untangleThought(text)

                val sdf = SimpleDateFormat("h:mm a", Locale.US)
                val displayDate = "Today, ${sdf.format(Date()).lowercase(Locale.US)}"

                val quotePreview = if (text.length > 90) {
                    text.take(85).trim() + "…\u201D"
                } else {
                    text
                }

                val newEntry = DraftEntry(
                    title = untangled.title,
                    rawTranscript = text,
                    quotePreview = quotePreview,
                    durationSeconds = 0,
                    recordedLocation = "Manual entry",
                    createdAtTimestamp = System.currentTimeMillis(),
                    displayDate = displayDate,
                    facts = untangled.facts,
                    feelings = untangled.feelings,
                    nextSteps = untangled.nextSteps,
                    hasFacts = untangled.facts.isNotEmpty(),
                    hasFeelings = untangled.feelings.isNotEmpty(),
                    hasNextSteps = untangled.nextSteps.isNotEmpty()
                )

                val newId = repository.insertDraft(newEntry)

                _isUntangling.value = false
                _isManualEntryMode.value = false
                _screenStack.remove(Screen.VoiceCapture)
                navigateTo(Screen.EntryDetail(newId))
            } catch (e: Exception) {
                _isUntangling.value = false
                showMessage("Organized locally: ${e.localizedMessage}")
                navigateBack()
            }
        }
    }

    fun cancelVoiceCapture() {
        _isManualEntryMode.value = false
        speechManager.discard()
        navigateBack()
    }

    fun setLocation(loc: String) {
        _selectedLocation.value = loc
    }

    fun finishAndOrganizeThought() {
        val duration = speechManager.elapsedSeconds.value.coerceAtLeast(1)
        val transcript = speechManager.stopListening()

        if (transcript.isBlank()) {
            cancelVoiceCapture()
            return
        }

        viewModelScope.launch {
            _isUntangling.value = true
            try {
                // Call Gemini to untangle thoughts into Facts, Feelings, and Next Steps
                val untangled = GeminiService.untangleThought(transcript)

                val sdf = SimpleDateFormat("h:mm a", Locale.US)
                val displayDate = "Today, ${sdf.format(Date()).lowercase(Locale.US)}"

                val quotePreview = if (transcript.length > 90) {
                    transcript.take(85).trim() + "…\u201D"
                } else {
                    transcript
                }

                val newEntry = DraftEntry(
                    title = untangled.title,
                    rawTranscript = transcript,
                    quotePreview = quotePreview,
                    durationSeconds = duration,
                    recordedLocation = _selectedLocation.value,
                    createdAtTimestamp = System.currentTimeMillis(),
                    displayDate = displayDate,
                    facts = untangled.facts,
                    feelings = untangled.feelings,
                    nextSteps = untangled.nextSteps,
                    hasFacts = untangled.facts.isNotEmpty(),
                    hasFeelings = untangled.feelings.isNotEmpty(),
                    hasNextSteps = untangled.nextSteps.isNotEmpty()
                )

                val newId = repository.insertDraft(newEntry)

                _isUntangling.value = false
                // Navigate directly to Entry Detail to view the untangled thoughts
                _screenStack.remove(Screen.VoiceCapture)
                navigateTo(Screen.EntryDetail(newId))
            } catch (e: Exception) {
                _isUntangling.value = false
                showMessage("Organized locally: ${e.localizedMessage}")
                navigateBack()
            }
        }
    }

    fun deleteDraft(id: Long) {
        viewModelScope.launch {
            repository.deleteDraftById(id)
            showMessage("Entry deleted")
            navigateBack()
        }
    }

    fun regenerateWeeklySynthesis() {
        if (!isPremium.value) {
            navigateTo(Screen.Paywall)
            return
        }
        viewModelScope.launch {
            _isGeneratingWeeklyPatterns.value = true
            try {
                val drafts = draftsList.value
                val newReport = GeminiService.generateWeeklySynthesis(drafts)
                repository.insertWeeklyPattern(newReport)
                showMessage("Weekly patterns refreshed with Gemini")
            } catch (e: Exception) {
                showMessage("Using existing synthesis: ${e.message}")
            } finally {
                _isGeneratingWeeklyPatterns.value = false
            }
        }
    }

    // --- Search (Gated by Premium) ---

    fun toggleSearch() {
        if (!isPremium.value) {
            navigateTo(Screen.Paywall)
            return
        }
        _isSearchActive.value = !_isSearchActive.value
        if (!_isSearchActive.value) {
            _searchQuery.value = ""
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // --- Export (Gated by Premium) ---

    /** Export all entries as shareable plain text. */
    fun exportEntries(context: Context) {
        if (!isPremium.value) {
            navigateTo(Screen.Paywall)
            return
        }

        val entries = draftsList.value
        if (entries.isEmpty()) {
            showMessage("No entries to export")
            return
        }

        val exportText = buildString {
            appendLine("Echo Drafts \u2014 Exported Entries")
            appendLine("=".repeat(40))
            appendLine()
            entries.forEach { entry ->
                appendLine("\uD83D\uDCCC ${entry.title}")
                appendLine("   ${entry.displayDate} \u00B7 ${entry.durationSeconds}s \u00B7 ${entry.recordedLocation}")
                appendLine()
                if (entry.facts.isNotEmpty()) {
                    appendLine("   Facts:")
                    entry.facts.forEach { appendLine("   \u2022 $it") }
                    appendLine()
                }
                if (entry.feelings.isNotEmpty()) {
                    appendLine("   Feelings:")
                    entry.feelings.forEach { appendLine("   \u2022 $it") }
                    appendLine()
                }
                if (entry.nextSteps.isNotEmpty()) {
                    appendLine("   Next Steps:")
                    entry.nextSteps.forEach { appendLine("   \u2192 $it") }
                    appendLine()
                }
                appendLine("   Raw transcript:")
                appendLine("   ${entry.rawTranscript}")
                appendLine()
                appendLine("-".repeat(40))
                appendLine()
            }
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Echo Drafts \u2014 ${entries.size} Entries Export")
            putExtra(Intent.EXTRA_TEXT, exportText)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Export entries"))
    }

    // --- RevenueCat Actions & Demo Fallbacks ---

    fun purchasePackage(activity: Activity, packageToPurchase: Package) {
        if (Purchases.isConfigured) {
            Purchases.sharedInstance.purchaseWith(
                PurchaseParams.Builder(activity, packageToPurchase).build(),
                onError = { error, userCancelled ->
                    if (!userCancelled) {
                        showMessage("Purchase error: ${error.message}")
                    }
                },
                onSuccess = { _, customerInfo ->
                    val entitled = customerInfo.entitlements[RevenueCatConfig.ENTITLEMENT_ID]?.isActive == true
                    _isPremium.value = entitled
                    if (entitled) {
                        showMessage("Welcome to Echo Drafts Premium!")
                        navigateBack()
                    }
                }
            )
        } else {
            simulatePremiumUnlock()
        }
    }

    fun restorePurchases() {
        if (Purchases.isConfigured) {
            Purchases.sharedInstance.restorePurchasesWith(
                onError = { error ->
                    showMessage("Restore failed: ${error.message}")
                },
                onSuccess = { customerInfo ->
                    val entitled = customerInfo.entitlements[RevenueCatConfig.ENTITLEMENT_ID]?.isActive == true
                    _isPremium.value = entitled
                    if (entitled) {
                        showMessage("Purchases restored successfully!")
                        navigateBack()
                    } else {
                        showMessage("No active premium subscription found")
                    }
                }
            )
        } else {
            simulatePremiumUnlock()
        }
    }

    fun simulatePremiumUnlock() {
        _isPremium.value = !_isPremium.value
        if (_isPremium.value) {
            showMessage("Premium features unlocked (Demo Mode)")
        } else {
            showMessage("Switched back to Free Tier")
        }
    }

    fun showMessage(msg: String) {
        _userMessage.value = msg
    }

    fun clearMessage() {
        _userMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        speechManager.destroy()
    }
}
