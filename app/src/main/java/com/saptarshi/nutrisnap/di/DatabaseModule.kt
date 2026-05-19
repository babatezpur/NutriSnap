package com.saptarshi.nutrisnap.di

import android.content.Context
import androidx.room.Room
import com.saptarshi.nutrisnap.data.local.dao.MealDao
import com.saptarshi.nutrisnap.data.local.database.NutriSnapDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NutriSnapDatabase {
        return Room.databaseBuilder(
            context,
            NutriSnapDatabase::class.java,
            "nutri_snap_database"
        ).build()
    }

    @Provides
    fun provideMealDao(database: NutriSnapDatabase): MealDao {
        return database.mealDao()
    }
}
