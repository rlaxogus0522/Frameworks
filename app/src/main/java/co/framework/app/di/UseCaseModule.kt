package co.framework.app.di

import co.framework.domain.repository.ApiRepository
import co.framework.domain.usecase.GetGitUserDataUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {
    @Provides
    @Singleton
    fun provideGetUseCase(repository : ApiRepository) = GetGitUserDataUseCase(repository)
}