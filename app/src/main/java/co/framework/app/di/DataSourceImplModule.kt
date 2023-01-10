package co.framework.app.di

import co.framework.data.remote.ApiService
import co.framework.data.repository.DataSource
import co.framework.data.repository.DataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DataSourceImplModule {
    @Provides
    @Singleton
    fun provideMainDataSource(
        apiService: ApiService
    ): DataSource {
        return DataSourceImpl(apiService)
    }

}