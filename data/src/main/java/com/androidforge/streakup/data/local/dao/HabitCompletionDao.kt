package com.androidforge.streakup.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.androidforge.streakup.data.local.entity.HabitCompletionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitCompletionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: HabitCompletionEntity): Long

    @Update
    suspend fun updateCompletion(completion: HabitCompletionEntity)

    @Query("SELECT * FROM habit_completions WHERE habit_id = :habitId AND date = :date")
    suspend fun getCompletionForDate(habitId: Long, date: Long): HabitCompletionEntity?

    @Query("DELETE FROM habit_completions WHERE habit_id = :habitId")
    suspend fun deleteCompletionsForHabit(habitId: Long)
}