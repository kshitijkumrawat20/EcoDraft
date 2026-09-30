package com.example.data.repository

import com.example.data.db.DraftDao
import com.example.data.model.DraftEntry
import com.example.data.model.WeeklyPatternReport
import kotlinx.coroutines.flow.Flow

class DraftRepository(
    private val draftDao: DraftDao
) {
    val allDrafts: Flow<List<DraftEntry>> = draftDao.getAllDrafts()
    val latestPattern: Flow<WeeklyPatternReport?> = draftDao.getLatestWeeklyPattern()
    val draftCount: Flow<Int> = draftDao.getDraftCount()

    fun getDraftById(id: Long): Flow<DraftEntry?> = draftDao.getDraftByIdFlow(id)

    suspend fun insertDraft(draft: DraftEntry): Long {
        return draftDao.insertDraft(draft)
    }

    suspend fun updateDraft(draft: DraftEntry) {
        draftDao.updateDraft(draft)
    }

    suspend fun deleteDraftById(id: Long) {
        draftDao.deleteDraftById(id)
    }

    fun getWeeklyEntryCount(sinceTimestamp: Long): Flow<Int> =
        draftDao.getWeeklyEntryCount(sinceTimestamp)

    fun searchDrafts(query: String): Flow<List<DraftEntry>> =
        draftDao.searchDrafts(query)

    suspend fun insertWeeklyPattern(pattern: WeeklyPatternReport): Long {
        return draftDao.insertWeeklyPattern(pattern)
    }

    suspend fun clearAllData() {
        draftDao.deleteAllDrafts()
        draftDao.deleteAllWeeklyPatterns()
    }
}
