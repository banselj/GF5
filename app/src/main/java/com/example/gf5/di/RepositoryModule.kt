package com.example.gf5.di

import com.example.gf5.repositories.RideRepository
import com.example.gf5.repositories.RideRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRideRepository(
        rideRepositoryImpl: RideRepositoryImpl
    ): RideRepository
}

