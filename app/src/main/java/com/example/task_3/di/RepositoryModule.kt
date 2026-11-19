package com.example.task_3.di

import com.example.task_3.data.AssetSportsRepository
import com.example.task_3.domain.SportsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindSportsRepository(repository: AssetSportsRepository): SportsRepository
}
