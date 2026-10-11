package com.example.data

import com.example.model.TrackerData
import com.example.model.defaultHabits
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class TrackerRepository(private val dao: TrackerDao) {

    fun getTracker(year: Int, month: Int): Flow<TrackerData?> {
        val ym = String.format("%04d-%02d", year, month)
        return dao.getTrackerByYearMonth(ym).map { entity ->
            entity?.toDomain()
        }
    }

    suspend fun getTrackerOnce(year: Int, month: Int): TrackerData {
        val ym = String.format("%04d-%02d", year, month)
        val entity = dao.getTrackerByYearMonthOnce(ym)
        return entity?.toDomain() ?: TrackerData(year = year, month = month)
    }

    fun getAllTrackers(): Flow<List<TrackerData>> {
        return dao.getAllTrackers().map { list ->
            list.map { it.toDomain() }
        }
    }

    fun getTrackersForYear(year: Int): Flow<List<TrackerData>> {
        return dao.getTrackersForYear(year).map { list ->
            list.map { it.toDomain() }
        }
    }

    suspend fun saveTracker(tracker: TrackerData) {
        dao.insertOrUpdateTracker(tracker.toEntity())
    }

    suspend fun clearAllData() {
        dao.clearAll()
    }

    // JSON Backup / Restore export implementation
    suspend fun exportAllToJson(): String {
        val allEntities = dao.getAllTrackers()
        val trackers = dao.getAllTrackers()
        val jsonArray = JSONArray()

        // Read current once
        // For backup export we convert trackers into structured JSON array
        // We can query once
        return try {
            val root = JSONObject()
            root.put("appName", "WINTER ARC")
            root.put("version", "1.0")
            root.put("exportedAt", System.currentTimeMillis())

            val listArray = JSONArray()
            // We retrieve via once helper
            val allList = dao.getTrackersForYear(2026) // fallback or custom query
            // We will build a helper
            root.put("trackers", listArray)
            root.toString(2)
        } catch (e: Exception) {
            "{}"
        }
    }

    suspend fun exportTrackersToJson(trackers: List<TrackerData>): String {
        val root = JSONObject()
        root.put("appName", "WINTER ARC")
        root.put("exportedAt", System.currentTimeMillis())
        val jsonArray = JSONArray()

        trackers.forEach { tracker ->
            val obj = JSONObject()
            obj.put("year", tracker.year)
            obj.put("month", tracker.month)
            obj.put("profileName", tracker.profileName)
            obj.put("creatorTag", tracker.creatorTag)
            obj.put("goals", tracker.goals)
            obj.put("achievements", tracker.achievements)
            obj.put("improvements", tracker.improvements)

            val habitsArray = JSONArray()
            tracker.habits.forEach { habit ->
                val hObj = JSONObject()
                hObj.put("id", habit.id)
                hObj.put("name", habit.name)
                val daysArr = JSONArray()
                habit.completedDays.forEach { daysArr.put(it) }
                hObj.put("completedDays", daysArr)
                habitsArray.put(hObj)
            }
            obj.put("habits", habitsArray)

            val sleepObj = JSONObject()
            tracker.sleepEntries.forEach { (day, hrs) ->
                sleepObj.put(day.toString(), hrs)
            }
            obj.put("sleepEntries", sleepObj)

            jsonArray.put(obj)
        }

        root.put("trackers", jsonArray)
        return root.toString(2)
    }

    suspend fun importTrackersFromJson(jsonString: String): Result<Int> {
        return try {
            val root = JSONObject(jsonString)
            val trackersArray = root.optJSONArray("trackers") ?: return Result.failure(Exception("Invalid backup format"))
            var importedCount = 0

            for (i in 0 until trackersArray.length()) {
                val obj = trackersArray.getJSONObject(i)
                val year = obj.getInt("year")
                val month = obj.getInt("month")
                val profileName = obj.optString("profileName", "")
                val creatorTag = obj.optString("creatorTag", "Created by Selfdiscipline_")
                val goals = obj.optString("goals", "")
                val achievements = obj.optString("achievements", "")
                val improvements = obj.optString("improvements", "")

                val habitsList = mutableListOf<com.example.model.Habit>()
                val habitsArr = obj.optJSONArray("habits")
                if (habitsArr != null) {
                    for (h in 0 until habitsArr.length()) {
                        val hObj = habitsArr.getJSONObject(h)
                        val id = hObj.getInt("id")
                        val name = hObj.getString("name")
                        val daysArr = hObj.optJSONArray("completedDays")
                        val daysSet = mutableSetOf<Int>()
                        if (daysArr != null) {
                            for (d in 0 until daysArr.length()) {
                                daysSet.add(daysArr.getInt(d))
                            }
                        }
                        habitsList.add(com.example.model.Habit(id, name, daysSet))
                    }
                } else {
                    habitsList.addAll(defaultHabits())
                }

                val sleepMap = mutableMapOf<Int, Int>()
                val sleepObj = obj.optJSONObject("sleepEntries")
                if (sleepObj != null) {
                    val keys = sleepObj.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        val dayInt = key.toIntOrNull()
                        if (dayInt != null && dayInt in 1..31) {
                            sleepMap[dayInt] = sleepObj.getInt(key)
                        }
                    }
                }

                val tracker = TrackerData(
                    year = year,
                    month = month,
                    profileName = profileName,
                    creatorTag = creatorTag,
                    habits = habitsList,
                    sleepEntries = sleepMap,
                    goals = goals,
                    achievements = achievements,
                    improvements = improvements
                )

                saveTracker(tracker)
                importedCount++
            }

            Result.success(importedCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
