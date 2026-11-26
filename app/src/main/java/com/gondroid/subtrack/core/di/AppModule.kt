package com.gondroid.subtrack.core.di

import android.content.Context
import com.gondroid.subtrack.core.preferences.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import androidx.room.Room
import com.gondroid.subtrack.data.local.SnapshotDao
import com.gondroid.subtrack.data.local.SubTrackDatabase

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences =
        UserPreferences.getInstance(context)

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SubTrackDatabase =
        Room.databaseBuilder(context, SubTrackDatabase::class.java, SubTrackDatabase.NAME).build()

    @Provides
    fun provideSnapshotDao(db: SubTrackDatabase): SnapshotDao = db.snapshotDao()
}
