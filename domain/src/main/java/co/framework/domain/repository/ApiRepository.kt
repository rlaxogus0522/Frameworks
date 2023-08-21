package co.framework.domain.repository

import co.framework.domain.model.GitData
import co.framework.domain.util.RemoteErrorEmitter

interface ApiRepository {
    suspend fun getGitUserData(remoteErrorEmitter: RemoteErrorEmitter, owner : String) : List<GitData>?
}