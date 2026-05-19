package com.saptarshi.nutrisnap.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val foodName: String,
    val quantity: String,
    val userNotes: String?,
    val mealType: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val fibre: Float,
    val aiSummary: String,
    val photoPath: String,
    val date: String,
    val timestamp: Long
)
