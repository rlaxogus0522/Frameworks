package co.framework.data.remote

import co.framework.domain.model.TestResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("users/{owner}/repos")
    suspend fun getRepos(@Path("owner") owner: String) : Response<List<TestResponse>>
}