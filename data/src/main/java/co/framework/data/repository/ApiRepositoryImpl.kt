package co.framework.data.repository

import co.framework.data.mapper.Mapper
import co.framework.domain.model.GitData
import co.framework.domain.repository.ApiRepository
import co.framework.domain.util.RemoteErrorEmitter
import javax.inject.Inject

class ApiRepositoryImpl @Inject constructor( private val dataSource: DataSource) : ApiRepository {
    override suspend fun getGitUserData(remoteErrorEmitter: RemoteErrorEmitter, owner: String): List<GitData>? {
        return Mapper.mapperApi(dataSource.getGitUserData(remoteErrorEmitter,owner))
    }

}