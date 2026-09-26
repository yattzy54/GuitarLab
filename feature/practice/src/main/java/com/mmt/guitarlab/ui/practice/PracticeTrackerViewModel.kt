package com.mmt.guitarlab.ui.practice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mmt.guitarlab.data.db.PracticeSessionDao
import com.mmt.guitarlab.data.db.PracticeSessionEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class PracticeStats(
    val totalMinutes: Int = 0,
    val streakDays: Int = 0,
    val totalSessions: Int = 0,
)

@HiltViewModel
class PracticeTrackerViewModel @Inject constructor(
    private val sessionDao: PracticeSessionDao,
) : ViewModel() {

    val sessions: StateFlow<List<PracticeSessionEntity>> = sessionDao.getAllSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val stats: StateFlow<PracticeStats> = sessions.map { list ->
        calculateStats(list)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PracticeStats())

    fun logSession(durationMinutes: Int, notes: String, category: String) {
        if (durationMinutes <= 0) return
        viewModelScope.launch {
            val entity = PracticeSessionEntity(
                dateMillis = System.currentTimeMillis(),
                durationMinutes = durationMinutes,
                notes = notes,
                category = category,
            )
            sessionDao.insertSession(entity)
        }
    }

    fun deleteSession(session: PracticeSessionEntity) {
        viewModelScope.launch {
            sessionDao.deleteSession(session)
        }
    }

    private fun calculateStats(list: List<PracticeSessionEntity>): PracticeStats {
        if (list.isEmpty()) return PracticeStats()

        val totalMinutes = list.sumOf { it.durationMinutes }
        val totalSessions = list.size

        // Calculate consecutive day streak
        val dayTimestamps = list.map { truncateToDay(it.dateMillis) }.distinct().sortedDescending()
        var streak = 0
        val today = truncateToDay(System.currentTimeMillis())

        // Allow streak to count if today or yesterday was logged
        if (dayTimestamps.isNotEmpty() && (dayTimestamps.first() == today || dayTimestamps.first() == today - TimeUnit.DAYS.toMillis(1))) {
            var checkDay = dayTimestamps.first()
            for (day in dayTimestamps) {
                if (day == checkDay) {
                    streak++
                    checkDay -= TimeUnit.DAYS.toMillis(1)
                } else {
                    break
                }
            }
        }

        return PracticeStats(
            totalMinutes = totalMinutes,
            streakDays = streak,
            totalSessions = totalSessions,
        )
    }

    private fun truncateToDay(millis: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
