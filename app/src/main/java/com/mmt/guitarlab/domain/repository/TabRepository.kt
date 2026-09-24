package com.mmt.guitarlab.domain.repository

import com.mmt.guitarlab.data.db.TabProjectEntity
import com.mmt.guitarlab.domain.model.TabScore
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.io.InputStream

interface TabRepository {
    suspend fun parseTab(inputStream: InputStream, filename: String?): Result<TabScore>
    suspend fun parseAsciiText(text: String, title: String): Result<TabScore>
    suspend fun exportMidi(score: TabScore, outputFile: File): Result<File>

    fun getAllProjects(): Flow<List<TabProjectEntity>>
    suspend fun saveProject(score: TabScore): Result<Unit>
    suspend fun loadProject(id: String): Result<TabScore>
    suspend fun deleteProject(project: TabProjectEntity): Result<Unit>
}
