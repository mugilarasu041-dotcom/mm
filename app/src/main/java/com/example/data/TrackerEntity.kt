package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.model.Habit
import com.example.model.TrackerData
import com.example.model.defaultHabits
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@Entity(tableName = "monthly_trackers")
@TypeConverters(TrackerConverters::class)
data class TrackerEntity(
    @PrimaryKey val yearMonth: String, // e.g. "2026-10"
    val year: Int,
    val month: Int,
    val profileName: String,
    val creatorTag: String,
    val habitsJson: String,
    val sleepJson: String,
    val goals: String,
    val achievements: String,
    val improvements: String,
    val updatedAt: Long
)

class TrackerConverters {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    private val habitListType = Types.newParameterizedType(List::class.java, Habit::class.java)
    private val habitAdapter = moshi.adapter<List<Habit>>(habitListType)

    private val sleepMapType = Types.newParameterizedType(Map::class.java, String::class.java, Int::class.java)
    private val sleepAdapter = moshi.adapter<Map<String, Int>>(sleepMapType)

    @TypeConverter
    fun fromHabitList(habits: List<Habit>?): String {
        return habitAdapter.toJson(habits ?: emptyList())
    }

    @TypeConverter
    fun toHabitList(json: String?): List<Habit> {
        if (json.isNullOrEmpty()) return defaultHabits()
        return try {
            habitAdapter.fromJson(json) ?: defaultHabits()
        } catch (e: Exception) {
            defaultHabits()
        }
    }

    @TypeConverter
    fun fromSleepMap(map: Map<Int, Int>?): String {
        val stringKeyMap = map?.mapKeys { it.key.toString() } ?: emptyMap()
        return sleepAdapter.toJson(stringKeyMap)
    }

    @TypeConverter
    fun toSleepMap(json: String?): Map<Int, Int> {
        if (json.isNullOrEmpty()) return emptyMap()
        return try {
            val stringMap = sleepAdapter.fromJson(json) ?: emptyMap()
            stringMap.mapKeys { it.key.toIntOrNull() ?: 0 }.filterKeys { it in 1..31 }
        } catch (e: Exception) {
            emptyMap()
        }
    }
}

fun TrackerData.toEntity(): TrackerEntity {
    val converters = TrackerConverters()
    val ym = String.format("%04d-%02d", year, month)
    return TrackerEntity(
        yearMonth = ym,
        year = year,
        month = month,
        profileName = profileName,
        creatorTag = creatorTag,
        habitsJson = converters.fromHabitList(habits),
        sleepJson = converters.fromSleepMap(sleepEntries),
        goals = goals,
        achievements = achievements,
        improvements = improvements,
        updatedAt = System.currentTimeMillis()
    )
}

fun TrackerEntity.toDomain(): TrackerData {
    val converters = TrackerConverters()
    return TrackerData(
        year = year,
        month = month,
        profileName = profileName,
        creatorTag = creatorTag.ifEmpty { "Created by Selfdiscipline_" },
        habits = converters.toHabitList(habitsJson),
        sleepEntries = converters.toSleepMap(sleepJson),
        goals = goals,
        achievements = achievements,
        improvements = improvements,
        updatedAt = updatedAt
    )
}
