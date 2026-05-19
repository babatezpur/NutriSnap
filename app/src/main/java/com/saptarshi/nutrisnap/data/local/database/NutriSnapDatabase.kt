package com.saptarshi.nutrisnap.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.saptarshi.nutrisnap.data.local.dao.MealDao
import com.saptarshi.nutrisnap.data.local.entity.MealEntity

@Database(entities = [MealEntity::class], version = 1, exportSchema = false)
abstract class NutriSnapDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao
}
