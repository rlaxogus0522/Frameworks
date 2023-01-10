package co.framework.data.repository

import co.framework.data.remote.ApiService
import co.framework.data.util.BaseRepository
import co.framework.domain.model.TestResponse
import co.framework.domain.util.RemoteErrorEmitter
import javax.inject.Inject

class DataSourceImpl @Inject constructor(private val apiService: ApiService) : BaseRepository(), DataSource {
    override suspend fun getTest(remoteErrorEmitter: RemoteErrorEmitter, owner: String): List<TestResponse>? {
        return safeCallApi(remoteErrorEmitter) {apiService.getRepos(owner).body()}
    }

}