package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DraftEntry
import com.example.data.model.WeeklyPatternReport
import kotlinx.coroutines.flow.Flow

@Dao
interface DraftDao {
    @Query("SELECT * FROM draft_entries ORDER BY createdAtTimestamp DESC")
    fun getAllDrafts(): Flow<List<DraftEntry>>

    @Query("SELECT * FROM draft_entries WHERE id = :id LIMIT 1")
    suspend fun getDraftById(id: Long): DraftEntry?

    @Query("SELECT * FROM draft_entries WHERE id = :id LIMIT 1")
    fun getDraftByIdFlow(id: Long): Flow<DraftEntry?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDraft(draft: DraftEntry): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrafts(drafts: List<DraftEntry>)

    @Update
    suspend fun updateDraft(draft: DraftEntry)

    @Delete
    suspend fun deleteDraft(draft: DraftEntry)

    @Query("DELETE FROM draft_entries WHERE id = :id")
    suspend fun deleteDraftById(id: Long)

    @Query("SELECT COUNT(*) FROM draft_entries")
    fun getDraftCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM draft_entries WHERE createdAtTimestamp >= :sinceTimestamp")
    fun getWeeklyEntryCount(sinceTimestamp: Long): Flow<Int>

    @Query("SELECT * FROM draft_entries WHERE title LIKE '%' || :query || '%' OR rawTranscript LIKE '%' || :query || '%' ORDER BY createdAtTimestamp DESC")
    fun searchDrafts(query: String): Flow<List<DraftEntry>>

    // Weekly patterns
    @Query("SELECT * FROM weekly_pattern_reports ORDER BY generatedTimestamp DESC LIMIT 1")
    fun getLatestWeeklyPattern(): Flow<WeeklyPatternReport?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeeklyPattern(pattern: WeeklyPatternReport): Long

    @Query("DELETE FROM draft_entries WHERE id <= 4")
    suspend fun clearSeedDrafts()

    @Query("DELETE FROM weekly_pattern_reports WHERE id <= 1")
    suspend fun clearSeedPatterns()
}
