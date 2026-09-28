package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.DraftEntry
import com.example.data.model.PatternItem
import com.example.data.model.WeeklyPatternReport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [DraftEntry::class, WeeklyPatternReport::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun draftDao(): DraftDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "echo_drafts_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.draftDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: DraftDao) {
            val now = System.currentTimeMillis()
            val dayMillis = 24 * 60 * 60 * 1000L

            val initialEntries = listOf(
                DraftEntry(
                    id = 1,
                    title = "Midterm anxiety vs actual completion state",
                    rawTranscript = "I keep thinking about the paper due Thursday, even though I wrapped the whole draft up yesterday afternoon. It's like my head hasn't caught up with what my hands already finished. I still have that knot right in the sternum. Maybe I just need to read the ending out loud tomorrow after breakfast and shut off my phone by nine tonight so I don't obsess over it.",
                    quotePreview = "“I keep worrying about the midterm deadline even though the draft is basically done…”",
                    durationSeconds = 42,
                    recordedLocation = "Recorded in Bedroom",
                    createdAtTimestamp = now - 35 * 60 * 1000L,
                    displayDate = "Today, 11:42 pm",
                    facts = listOf(
                        "First draft of the paper was finished yesterday at 4pm.",
                        "Deadline is Thursday at 11:59 pm; 48 hours remaining."
                    ),
                    feelings = listOf(
                        "Persistent racing sensation in the chest despite being on schedule.",
                        "Fear of submitting work that hasn't been read three times."
                    ),
                    nextSteps = listOf(
                        "Read conclusion aloud once tomorrow morning.",
                        "Turn off notifications after 9pm tonight."
                    ),
                    hasFacts = true,
                    hasFeelings = true,
                    hasNextSteps = true
                ),
                DraftEntry(
                    id = 2,
                    title = "Lab meeting cognitive overload",
                    rawTranscript = "Felt overwhelmed after the lab meeting today. Need to send an email to Dr. Chen before Friday morning. The feedback was dense, but Dr. Chen made helpful suggestions about the dataset parsing. I need to clear my thoughts before writing back.",
                    quotePreview = "“Felt overwhelmed after the lab meeting. Need to send an email to Dr. Chen before Friday morning.”",
                    durationSeconds = 78,
                    recordedLocation = "Transit thought",
                    createdAtTimestamp = now - dayMillis + (2 * 60 * 60 * 1000L),
                    displayDate = "Yesterday, 9:15 pm",
                    facts = listOf(
                        "Dr. Chen requested revisions on the statistical analysis pipeline.",
                        "Follow-up response deadline is Friday morning."
                    ),
                    feelings = listOf(
                        "Cognitive overwhelm from conflicting supervisor critiques.",
                        "Impatience with the length of data processing scripts."
                    ),
                    nextSteps = listOf(
                        "Draft concise questions in note form before writing email.",
                        "Schedule a 20-minute focused window at 10am."
                    ),
                    hasFacts = false,
                    hasFeelings = true,
                    hasNextSteps = true
                ),
                DraftEntry(
                    id = 3,
                    title = "Rent split calculation insomnia",
                    rawTranscript = "Can't sleep because of rent split math. The spreadsheet numbers are accurate, just need approval from roommates. I've rechecked the utility averages three times and they match the billing statements to the cent.",
                    quotePreview = "“Can't sleep because of rent split math. The spreadsheet numbers are accurate, just need approval.”",
                    durationSeconds = 55,
                    recordedLocation = "Desk notebook",
                    createdAtTimestamp = now - 3 * dayMillis,
                    displayDate = "Oct 21, 1:20 am",
                    facts = listOf(
                        "Spreadsheet incorporates proportional room square footage and utilities.",
                        "Calculations match bills and bank statements."
                    ),
                    feelings = listOf(
                        "Late-night restlessness triggered by petty logistical tasks.",
                        "Mild apprehension about roommate friction."
                    ),
                    nextSteps = listOf(
                        "Send link to the shared tab with short friendly note.",
                        "Put phone outside the bedroom after sending."
                    ),
                    hasFacts = true,
                    hasFeelings = false,
                    hasNextSteps = true
                ),
                DraftEntry(
                    id = 4,
                    title = "Full brain sensory saturation",
                    rawTranscript = "Just exhausted today. Nothing went wrong, brain is just full. No unresolved crises, just an overload of sensory inputs and screen time throughout the week. Need quiet time.",
                    quotePreview = "“Just exhausted today. Nothing went wrong, brain is just full.”",
                    durationSeconds = 24,
                    recordedLocation = "Bedside record",
                    createdAtTimestamp = now - 5 * dayMillis,
                    displayDate = "Oct 19, 10:04 pm",
                    facts = listOf(
                        "All sprint commitments completed and delivered.",
                        "No urgent notifications pending."
                    ),
                    feelings = listOf(
                        "Profound mental exhaustion from continuous context-switching.",
                        "Relief from putting the day to rest."
                    ),
                    nextSteps = listOf(
                        "Sleep without early morning alarms.",
                        "Read physical book for 10 minutes by amber lamp."
                    ),
                    hasFacts = false,
                    hasFeelings = true,
                    hasNextSteps = false
                )
            )

            dao.insertDrafts(initialEntries)

            val initialPattern = WeeklyPatternReport(
                id = 1,
                dateRangeText = "Oct 16 – Oct 22",
                synthesisQuote = "“You recorded 6 drafts this week. Your racing thoughts peaked on Tuesday evening around deadlines, with relief following each action step.”",
                synthesisDescription = "Quietly distilled from your late-night voice reflections and untangled midnight notes.",
                peakDayTime = "Peak: Tue 23:40",
                dailyTensionLevels = listOf(0.3f, 0.95f, 0.5f, 0.4f, 0.65f, 0.2f, 0.4f),
                recurringFeelings = listOf(
                    PatternItem("Overwhelmed before starting", "4 entries"),
                    PatternItem("Late-night restlessness", "3 entries"),
                    PatternItem("Relief after writing it down", "3 entries")
                ),
                recurringFacts = listOf(
                    PatternItem("Midterm & assignment submissions", "3 times"),
                    PatternItem("Sleep schedule shifts", "2 times"),
                    PatternItem("Living expenses & rent", "2 times")
                ),
                effectiveNextSteps = "Small tangible actions that calmed racing loops: reading drafts aloud, scheduling specific 15-minute review windows.",
                editorialNote = "\"Your narrative cadence slows down notably once thoughts are anchored onto paper. The urgency dissolved after Tuesday's 3-minute voice stream.\"",
                isSaved = true
            )

            dao.insertWeeklyPattern(initialPattern)
        }
    }
}
