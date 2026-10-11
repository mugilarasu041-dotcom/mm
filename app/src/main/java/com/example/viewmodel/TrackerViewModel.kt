package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.TrackerRepository
import com.example.model.Habit
import com.example.model.MonthUtils
import com.example.model.TrackerData
import com.example.model.defaultHabits
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface SaveState {
    object Idle : SaveState
    object Saving : SaveState
    object Saved : SaveState
}

class TrackerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TrackerRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = TrackerRepository(database.trackerDao())
    }

    private val currentCal = MonthUtils.getCurrentYearAndMonth()
    
    val selectedYear = MutableStateFlow(currentCal.first)
    val selectedMonth = MutableStateFlow(currentCal.second)

    private val _trackerData = MutableStateFlow(
        TrackerData(year = currentCal.first, month = currentCal.second)
    )
    val trackerData: StateFlow<TrackerData> = _trackerData.asStateFlow()

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Saved)
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()

    val allTrackers: StateFlow<List<TrackerData>> = repository.getAllTrackers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private var saveJob: Job? = null
    private var loadJob: Job? = null

    init {
        loadTrackerFor(currentCal.first, currentCal.second)
    }

    fun selectYearAndMonth(year: Int, month: Int) {
        if (selectedYear.value == year && selectedMonth.value == month) return
        selectedYear.value = year
        selectedMonth.value = month
        loadTrackerFor(year, month)
    }

    private fun loadTrackerFor(year: Int, month: Int) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _saveState.value = SaveState.Idle
            val existing = repository.getTrackerOnce(year, month)
            
            // Inherit profile name from any other recent tracker if empty
            val globalProfileName = if (existing.profileName.isEmpty()) {
                allTrackers.value.firstOrNull { it.profileName.isNotBlank() }?.profileName ?: ""
            } else {
                existing.profileName
            }

            _trackerData.value = existing.copy(
                profileName = globalProfileName,
                habits = if (existing.habits.isEmpty()) defaultHabits() else existing.habits
            )
            _saveState.value = SaveState.Saved
        }
    }

    private fun updateAndAutoSave(transform: (TrackerData) -> TrackerData) {
        val updated = transform(_trackerData.value)
        _trackerData.value = updated
        triggerAutoSave(updated)
    }

    private fun triggerAutoSave(data: TrackerData) {
        saveJob?.cancel()
        _saveState.value = SaveState.Saving
        saveJob = viewModelScope.launch {
            delay(300) // debounce
            repository.saveTracker(data)
            _saveState.value = SaveState.Saved
        }
    }

    fun updateProfileName(name: String) {
        updateAndAutoSave { it.copy(profileName = name) }
    }

    fun updateHabitName(id: Int, newName: String) {
        updateAndAutoSave { current ->
            val newHabits = current.habits.map { h ->
                if (h.id == id) h.copy(name = newName) else h
            }
            current.copy(habits = newHabits)
        }
    }

    fun toggleHabitDay(habitId: Int, day: Int) {
        updateAndAutoSave { current ->
            val newHabits = current.habits.map { h ->
                if (h.id == habitId) {
                    val newCompleted = if (h.completedDays.contains(day)) {
                        h.completedDays - day
                    } else {
                        h.completedDays + day
                    }
                    h.copy(completedDays = newCompleted)
                } else h
            }
            current.copy(habits = newHabits)
        }
    }

    fun setSleepForDay(day: Int, hours: Int?) {
        updateAndAutoSave { current ->
            val newSleep = current.sleepEntries.toMutableMap()
            if (hours == null) {
                newSleep.remove(day)
            } else {
                newSleep[day] = hours
            }
            current.copy(sleepEntries = newSleep)
        }
    }

    fun updateGoals(goalsText: String) {
        updateAndAutoSave { it.copy(goals = goalsText) }
    }

    fun updateAchievements(achievementsText: String) {
        updateAndAutoSave { it.copy(achievements = achievementsText) }
    }

    fun updateImprovements(improvementsText: String) {
        updateAndAutoSave { it.copy(improvements = improvementsText) }
    }

    fun exportDataJson(onExportReady: (String) -> Unit) {
        viewModelScope.launch {
            val trackers = repository.getAllTrackers().firstOrNull() ?: listOf(_trackerData.value)
            val json = repository.exportTrackersToJson(trackers)
            onExportReady(json)
        }
    }

    fun importDataJson(jsonString: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val result = repository.importTrackersFromJson(jsonString)
            if (result.isSuccess) {
                val count = result.getOrDefault(0)
                // reload current tracker
                loadTrackerFor(selectedYear.value, selectedMonth.value)
                onResult("Successfully imported $count monthly tracker(s)!")
            } else {
                onResult("Failed to import JSON: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun resetAllData(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.clearAllData()
            loadTrackerFor(selectedYear.value, selectedMonth.value)
            onComplete()
        }
    }
}
