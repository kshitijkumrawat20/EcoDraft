package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class PatternItem(
    val title: String,
    val countOrLabel: String
)

@Entity(tableName = "weekly_pattern_reports")
data class WeeklyPatternReport(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateRangeText: String,
    val synthesisQuote: String,
    val synthesisDescription: String,
    val peakDayTime: String,
    val dailyTensionLevels: List<Float>, // 7 values for Mon-Sun
    val recurringFeelings: List<PatternItem>,
    val recurringFacts: List<PatternItem>,
    val effectiveNextSteps: String,
    val editorialNote: String,
    val isSaved: Boolean = true,
    val generatedTimestamp: Long = System.currentTimeMillis()
)
