package com.mmt.guitarlab.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "tab_projects")
data class TabProjectEntity(
    @PrimaryKey val id: String,
    val title: String,
    val artist: String,
    val updatedAt: Long,
    val jsonContent: String,
)

@Dao
interface TabProjectDao {
    @Query("SELECT * FROM tab_projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<TabProjectEntity>>

    @Query("SELECT * FROM tab_projects WHERE id = :id LIMIT 1")
    suspend fun getProjectById(id: String): TabProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: TabProjectEntity)

    @Delete
    suspend fun deleteProject(project: TabProjectEntity)
}
