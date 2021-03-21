package co.harismiftahulhudha.alodoktertest.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import co.harismiftahulhudha.alodoktertest.BuildConfig
import co.harismiftahulhudha.alodoktertest.BuildConfig.DATABASE_NAME
import co.harismiftahulhudha.alodoktertest.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application, callback: AppDatabase.Callback) =
        if (BuildConfig.DEBUG) {
            /**
             * saat debug menambahkan fungsi setJournalMode
             * agar database bisa di import untuk di cek datanya
             */
            Room.databaseBuilder(app, AppDatabase::class.java, DATABASE_NAME)
                .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
                .fallbackToDestructiveMigration()
                .addCallback(callback)
                .build()
        } else {
            Room.databaseBuilder(app, AppDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .addCallback(callback)
                .build()
        }


    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context) = context


    @Provides
    @Singleton
    fun provideUserDao(db: AppDatabase) = db.userDao()

    @Provides
    @Singleton
    fun provideContentDao(db: AppDatabase) = db.contentDao()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope