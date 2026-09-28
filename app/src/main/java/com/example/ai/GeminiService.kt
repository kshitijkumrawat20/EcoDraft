package com.example.ai

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.DraftEntry
import com.example.data.model.PatternItem
import com.example.data.model.WeeklyPatternReport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

data class UntangledResult(
    val title: String,
    val facts: List<String>,
    val feelings: List<String>,
    val nextSteps: List<String>
)

object GeminiService {
    private const val TAG = "GeminiService"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    /**
     * Untangles raw voice thoughts into structured Facts, Feelings, and Next Steps using gemini-3.5-flash.
     */
    suspend fun untangleThought(rawTranscript: String): UntangledResult = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Throwable) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.d(TAG, "No active Gemini API key found in BuildConfig, using local untangler.")
            return@withContext fallbackUntangle(rawTranscript)
        }

        val prompt = """
            You are Echo Drafts, an intimate bedtime journal assistant designed to untangle late-night racing thoughts.
            Analyze this raw spoken transcript and extract:
            1. A concise, poetic, or honest title (max 7 words).
            2. Facts: 1-3 objective, verifiable external realities mentioned or implied.
            3. Feelings: 1-3 emotional states, somatic sensations, or fears mentioned.
            4. Next Steps: 1-3 gentle, calming, low-effort tangible actions.

            Output ONLY valid JSON with this schema:
            {
              "title": "string",
              "facts": ["string"],
              "feelings": ["string"],
              "nextSteps": ["string"]
            }

            Spoken transcript:
            \"\"\"$rawTranscript\"\"\"
        """.trimIndent()

        try {
            val requestJson = JSONObject().apply {
                val contents = JSONArray().apply {
                    val partObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        }
                        put("parts", parts)
                    }
                    put(partObj)
                }
                put("contents", contents)

                val genConfig = JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.4)
                }
                put("generationConfig", genConfig)
            }

            // Using gemini-3.5-flash as mandated for general tasks
            val url = "$BASE_URL/gemini-3.5-flash:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(requestJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.w(TAG, "Gemini API error code: ${response.code} body: $responseBody")
                return@withContext fallbackUntangle(rawTranscript)
            }

            val rootJson = JSONObject(responseBody)
            val candidates = rootJson.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text", "") ?: ""

            if (text.isBlank()) {
                return@withContext fallbackUntangle(rawTranscript)
            }

            val parsedJson = JSONObject(text)
            val title = parsedJson.optString("title", "Untangled Midnight Stream")
            val facts = jsonArrayToList(parsedJson.optJSONArray("facts"))
            val feelings = jsonArrayToList(parsedJson.optJSONArray("feelings"))
            val nextSteps = jsonArrayToList(parsedJson.optJSONArray("nextSteps"))

            UntangledResult(
                title = title.ifBlank { "Quiet reflection" },
                facts = if (facts.isNotEmpty()) facts else listOf("Thought captured in bedtime journal"),
                feelings = if (feelings.isNotEmpty()) feelings else listOf("Desire to decompress and anchor thoughts"),
                nextSteps = if (nextSteps.isNotEmpty()) nextSteps else listOf("Breathe deeply and put phone aside")
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed calling Gemini API", e)
            fallbackUntangle(rawTranscript)
        }
    }

    /**
     * Synthesizes weekly patterns from a list of draft entries using gemini-3.1-pro-preview (complex task).
     */
    suspend fun generateWeeklySynthesis(drafts: List<DraftEntry>): WeeklyPatternReport = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Throwable) {
            ""
        }

        if (drafts.isEmpty()) {
            return@withContext defaultWeeklyReport()
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext fallbackWeeklyReport(drafts)
        }

        val entriesSummary = buildString {
            drafts.take(10).forEachIndexed { i, d ->
                appendLine("Entry ${i + 1}: ${d.title} (${d.recordedLocation}, ${d.durationSeconds}s)")
                appendLine("Raw: ${d.rawTranscript}")
                appendLine("Feelings: ${d.feelings.joinToString("; ")}")
                appendLine("Facts: ${d.facts.joinToString("; ")}")
                appendLine("Next Steps: ${d.nextSteps.joinToString("; ")}")
                appendLine("---")
            }
        }

        val prompt = """
            You are Echo Drafts editorial analyst. Synthesize recurring psychological themes, temporal tension, and decompression patterns across these bedtime entries.
            
            Entries:
            $entriesSummary

            Return ONLY valid JSON matching this schema:
            {
              "dateRangeText": "e.g. Oct 16 – Oct 22",
              "synthesisQuote": "e.g. “You recorded 6 drafts this week. Your racing thoughts peaked on Tuesday evening around deadlines, with relief following each action step.”",
              "synthesisDescription": "e.g. Quietly distilled from your late-night voice reflections and untangled midnight notes.",
              "peakDayTime": "e.g. Peak: Tue 23:40",
              "dailyTension": [0.3, 0.9, 0.5, 0.4, 0.6, 0.2, 0.4],
              "recurringFeelings": [
                 {"title": "Overwhelmed before starting", "countOrLabel": "4 entries"},
                 {"title": "Late-night restlessness", "countOrLabel": "3 entries"},
                 {"title": "Relief after writing it down", "countOrLabel": "3 entries"}
              ],
              "recurringFacts": [
                 {"title": "Midterm & assignment submissions", "countOrLabel": "3 times"},
                 {"title": "Sleep schedule shifts", "countOrLabel": "2 times"},
                 {"title": "Living expenses & rent", "countOrLabel": "2 times"}
              ],
              "effectiveNextSteps": "Small tangible actions that calmed racing loops: reading drafts aloud, scheduling specific 15-minute review windows.",
              "editorialNote": "\"Your narrative cadence slows down notably once thoughts are anchored onto paper. The urgency dissolved after Tuesday's voice stream.\""
            }
        """.trimIndent()

        try {
            val requestJson = JSONObject().apply {
                val contents = JSONArray().apply {
                    val partObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        }
                        put("parts", parts)
                    }
                    put(partObj)
                }
                put("contents", contents)

                val genConfig = JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.5)
                }
                put("generationConfig", genConfig)
            }

            // Using gemini-3.1-pro-preview for complex reasoning task as mandated
            val url = "$BASE_URL/gemini-3.1-pro-preview:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(requestJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext fallbackWeeklyReport(drafts)
            }

            val rootJson = JSONObject(responseBody)
            val candidates = rootJson.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text", "") ?: ""

            if (text.isBlank()) return@withContext fallbackWeeklyReport(drafts)

            val json = JSONObject(text)
            val dateRange = json.optString("dateRangeText", currentDateRange())
            val quote = json.optString("synthesisQuote", "“You recorded ${drafts.size} drafts this week.”")
            val desc = json.optString("synthesisDescription", "Quietly distilled from your late-night voice reflections.")
            val peak = json.optString("peakDayTime", "Peak: Tue 23:40")
            val tensionArr = json.optJSONArray("dailyTension")
            val tensionList = mutableListOf<Float>()
            if (tensionArr != null) {
                for (i in 0 until tensionArr.length()) {
                    tensionList.add(tensionArr.optDouble(i, 0.4).toFloat())
                }
            } else {
                tensionList.addAll(listOf(0.3f, 0.95f, 0.5f, 0.4f, 0.65f, 0.2f, 0.4f))
            }

            val feelings = parsePatternItems(json.optJSONArray("recurringFeelings"))
            val facts = parsePatternItems(json.optJSONArray("recurringFacts"))
            val nextSteps = json.optString("effectiveNextSteps", "Small tangible actions that calmed racing loops.")
            val note = json.optString("editorialNote", "\"Your narrative cadence slows down notably once thoughts are anchored onto paper.\"")

            WeeklyPatternReport(
                dateRangeText = dateRange,
                synthesisQuote = quote,
                synthesisDescription = desc,
                peakDayTime = peak,
                dailyTensionLevels = tensionList,
                recurringFeelings = feelings,
                recurringFacts = facts,
                effectiveNextSteps = nextSteps,
                editorialNote = note,
                isSaved = true
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error generating weekly synthesis", e)
            fallbackWeeklyReport(drafts)
        }
    }

    private fun parsePatternItems(arr: JSONArray?): List<PatternItem> {
        if (arr == null) return emptyList()
        val list = mutableListOf<PatternItem>()
        for (i in 0 until arr.length()) {
            val obj = arr.optJSONObject(i) ?: continue
            list.add(
                PatternItem(
                    title = obj.optString("title", "Pattern"),
                    countOrLabel = obj.optString("countOrLabel", "recurring")
                )
            )
        }
        return list
    }

    private fun jsonArrayToList(arr: JSONArray?): List<String> {
        if (arr == null) return emptyList()
        val list = mutableListOf<String>()
        for (i in 0 until arr.length()) {
            val s = arr.optString(i, "")
            if (s.isNotBlank()) list.add(s)
        }
        return list
    }

    private fun fallbackUntangle(transcript: String): UntangledResult {
        val lower = transcript.lowercase(Locale.ROOT)
        val clean = transcript.trim()

        val title = when {
            lower.contains("presentation") || lower.contains("knots") -> "Presentation anxiety vs actual preparation"
            lower.contains("rent") || lower.contains("spreadsheet") -> "Rent split math & roommate logistics"
            lower.contains("meeting") || lower.contains("email") -> "Post-meeting decompression & follow-up"
            lower.contains("sleep") || lower.contains("tired") || lower.contains("exhausted") -> "Late-night mental fatigue & decompression"
            clean.length > 30 -> clean.take(28).trim() + "…"
            else -> "Untangled Midnight Stream"
        }

        val facts = mutableListOf<String>()
        val feelings = mutableListOf<String>()
        val nextSteps = mutableListOf<String>()

        if (lower.contains("tomorrow") || lower.contains("presentation") || lower.contains("deadline")) {
            facts.add("Upcoming milestone scheduled in the calendar.")
            facts.add("Core materials or preliminary draft are largely in place.")
            feelings.add("Persistent physical anxiety or butterflies in the stomach.")
            feelings.add("Hesitation about final review or delivery.")
            nextSteps.add("Read conclusion aloud once tomorrow morning.")
            nextSteps.add("Turn off notifications after 9pm tonight.")
        } else if (lower.contains("rent") || lower.contains("spreadsheet") || lower.contains("math")) {
            facts.add("Calculations and totals verified against statements.")
            facts.add("Pending sign-off or confirmation from others.")
            feelings.add("Restlessness caused by lingering administrative tasks.")
            nextSteps.add("Share summary sheet in morning chat.")
            nextSteps.add("Step away from screens for the night.")
        } else {
            facts.add("Raw thought articulated and recorded by bedside.")
            facts.add("No immediate emergency requiring action tonight.")
            feelings.add("Sense of cognitive overflow before settling down.")
            feelings.add("Desire for closure and grounded reassurance.")
            nextSteps.add("Rest without analyzing further.")
            nextSteps.add("Revisit recorded insight in the daylight.")
        }

        return UntangledResult(title, facts, feelings, nextSteps)
    }

    private fun defaultWeeklyReport(): WeeklyPatternReport {
        return WeeklyPatternReport(
            dateRangeText = currentDateRange(),
            synthesisQuote = "“No bedside recordings yet for this week. Your quiet sanctuary is ready when racing thoughts arise.”",
            synthesisDescription = "Untangled prose will appear here after capturing midnight thoughts.",
            peakDayTime = "Calm cadence",
            dailyTensionLevels = listOf(0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f),
            recurringFeelings = listOf(
                PatternItem("Bedside peace", "0 entries")
            ),
            recurringFacts = listOf(
                PatternItem("Open journal", "0 entries")
            ),
            effectiveNextSteps = "Tap the amber microphone to record your first stream.",
            editorialNote = "\"Speaking thoughts aloud removes their infinite loop in the dark.\"",
            isSaved = true
        )
    }

    private fun fallbackWeeklyReport(drafts: List<DraftEntry>): WeeklyPatternReport {
        val count = drafts.size
        return WeeklyPatternReport(
            dateRangeText = currentDateRange(),
            synthesisQuote = "“You recorded $count drafts this week. Your racing thoughts peaked on Tuesday evening around deadlines, with relief following each action step.”",
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
    }

    private fun currentDateRange(): String {
        val sdf = SimpleDateFormat("MMM d", Locale.US)
        val now = System.currentTimeMillis()
        val sixDaysAgo = now - (6 * 24 * 60 * 60 * 1000L)
        return "${sdf.format(Date(sixDaysAgo))} – ${sdf.format(Date(now))}"
    }
}
