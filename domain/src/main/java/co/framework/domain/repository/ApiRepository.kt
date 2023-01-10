package co.framework.domain.repository

import co.framework.domain.model.TestResponse
import co.framework.domain.util.RemoteErrorEmitter

interface ApiRepository {
    suspend fun getTest(remoteErrorEmitter: RemoteErrorEmitter, owner : String) : List<TestResponse>?
}