package com.idat.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.idat.data.local.database.AppDatabase
import com.idat.data.local.dao.ProductoDao
import com.idat.data.local.dao.CarritoDao
import com.idat.data.local.dao.FavoritoDao
import com.idat.data.local.preferences.UserPreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext appContext: Context
    ): AppDatabase =
        Room.databaseBuilder(appContext, AppDatabase::class.java, "tienda_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideProductoDao(db: AppDatabase): ProductoDao = db.productoDao()

    @Provides
    fun provideCarritoDao(db: AppDatabase): CarritoDao = db.carritoDao()

    @Provides
    fun provideFavoritoDao(db: AppDatabase): FavoritoDao = db.favoritoDao()

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideUserPreferencesManager(@ApplicationContext context: Context): UserPreferencesManager {
        return UserPreferencesManager(context)
    }
}
