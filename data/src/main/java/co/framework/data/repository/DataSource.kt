package co.framework.data.repository

import co.framework.domain.model.TestResponse
import co.framework.domain.util.RemoteErrorEmitter

interface DataSource {
    suspend fun getTest(remoteErrorEmitter: RemoteErrorEmitter, owner : String) : List<TestResponse>?
}