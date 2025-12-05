package com.idat.di.modules

import com.google.firebase.auth.FirebaseAuth
import com.idat.data.repository.ProductoRepositoryImpl
import com.idat.domain.repository.ProductoRepository
import com.idat.data.local.dao.ProductoDao
import com.idat.data.local.dao.CarritoDao
import com.idat.data.local.dao.FavoritoDao
import com.idat.data.remote.service.ProductoApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideProductoRepository(
        productoDao: ProductoDao,
        carritoDao: CarritoDao,
        favoritoDao: FavoritoDao,
        apiService: ProductoApiService,
        auth: FirebaseAuth
    ): ProductoRepository {
        return ProductoRepositoryImpl(productoDao, carritoDao, favoritoDao, apiService, auth)
    }
}
