package com.mmt.guitarlab.domain.repository

import com.mmt.guitarlab.domain.model.Tuning
import kotlinx.coroutines.flow.Flow

interface TuningRepository {
    fun getTunings(a4Hz: Float = 440f): Flow<List<Tuning>>
    fun getFavoriteIds(): Flow<Set<String>>
    fun getSelectedTuningId(): Flow<String>
    suspend fun selectTuning(tuningId: String)
    suspend fun toggleFavorite(tuningId: String)
}
