package com.androidforge.streakup.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.androidforge.streakup.data.local.entity.HabitEntity
import com.androidforge.streakup.data.local.entity.HabitWithCompletions
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity): Long

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Query("DELETE FROM habits WHERE id = :habitId")
    suspend fun deleteHabit(habitId: Long)

    @Transaction
    @Query("SELECT * FROM habits WHERE id = :habitId")
    fun getHabitWithCompletions(habitId: Long): Flow<HabitWithCompletions?>

    @Transaction
    @Query("SELECT * FROM habits WHERE is_active = 1 ORDER BY created_at DESC")
    fun getAllHabitsWithCompletions(): Flow<List<HabitWithCompletions>>
}