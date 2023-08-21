package co.framework.domain.usecase

import co.framework.domain.repository.ApiRepository
import co.framework.domain.util.RemoteErrorEmitter
import javax.inject.Inject

class GetGitUserDataUseCase @Inject constructor(private val apiRepository: ApiRepository){
    suspend fun invoke(remoteErrorEmitter: RemoteErrorEmitter, owner : String) = apiRepository.getGitUserData(remoteErrorEmitter, owner)
}