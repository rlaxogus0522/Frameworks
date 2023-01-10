package co.framework.app.di


import co.framework.data.repository.ApiRepositoryImpl
import co.framework.data.repository.DataSourceImpl
import co.framework.domain.repository.ApiRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {


    @Provides
    @Singleton
    fun provideMainRepository(
        dataSourceImpl: DataSourceImpl
    ) : ApiRepository {
        return ApiRepositoryImpl(dataSourceImpl)
    }
}