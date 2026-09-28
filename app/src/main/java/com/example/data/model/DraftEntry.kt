package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "draft_entries")
data class DraftEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val rawTranscript: String,
    val quotePreview: String,
    val durationSeconds: Int,
    val recordedLocation: String,
    val createdAtTimestamp: Long,
    val displayDate: String,
    val facts: List<String>,
    val feelings: List<String>,
    val nextSteps: List<String>,
    val hasFacts: Boolean = true,
    val hasFeelings: Boolean = true,
    val hasNextSteps: Boolean = true,
    val isFavorite: Boolean = false,
    val isSynced: Boolean = false
)
