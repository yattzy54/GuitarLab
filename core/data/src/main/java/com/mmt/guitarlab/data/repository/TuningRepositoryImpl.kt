package com.mmt.guitarlab.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mmt.guitarlab.data.db.FavoriteTuningDao
import com.mmt.guitarlab.data.db.FavoriteTuningEntity
import com.mmt.guitarlab.domain.model.Tuning
import com.mmt.guitarlab.domain.repository.TuningRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.tuningDataStore by preferencesDataStore(name = "guitarlab_tuning_prefs")

@Singleton
class TuningRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val favoriteDao: FavoriteTuningDao,
) : TuningRepository {

    private object Keys {
        val selectedTuningId = stringPreferencesKey("selected_tuning_id")
    }

    override fun getFavoriteIds(): Flow<Set<String>> {
        return favoriteDao.getAllFavoriteIds().map { it.toSet() }
    }

    override fun getSelectedTuningId(): Flow<String> {
        return context.tuningDataStore.data.map { prefs ->
            prefs[Keys.selectedTuningId] ?: "standard_e"
        }
    }

    override fun getTunings(a4Hz: Float): Flow<List<Tuning>> {
        return getFavoriteIds().map { favSet ->
            DefaultTunings.getAll(a4Hz).map { tuning ->
                tuning.copy(isFavorite = favSet.contains(tuning.id))
            }
        }
    }

    override suspend fun selectTuning(tuningId: String) {
        context.tuningDataStore.edit { prefs ->
            prefs[Keys.selectedTuningId] = tuningId
        }
    }

    override suspend fun toggleFavorite(tuningId: String) {
        val isFav = favoriteDao.isFavorite(tuningId)
        if (isFav) {
            favoriteDao.deleteFavorite(tuningId)
        } else {
            favoriteDao.insertFavorite(FavoriteTuningEntity(tuningId))
        }
    }
}
