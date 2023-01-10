package co.framework.domain.usecase

import co.framework.domain.repository.ApiRepository
import co.framework.domain.util.RemoteErrorEmitter
import javax.inject.Inject

class GetUseCase @Inject constructor(private val apiRepository: ApiRepository){
    suspend fun exTest(remoteErrorEmitter: RemoteErrorEmitter, owner : String) = apiRepository.getTest(remoteErrorEmitter, owner)
}