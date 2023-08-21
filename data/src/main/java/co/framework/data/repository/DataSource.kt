package co.framework.data.repository

import co.framework.data.model.GitResponse
import co.framework.domain.model.GitData
import co.framework.domain.util.RemoteErrorEmitter

interface DataSource {
    suspend fun getGitUserData(remoteErrorEmitter: RemoteErrorEmitter, owner : String) : List<GitResponse>?
}