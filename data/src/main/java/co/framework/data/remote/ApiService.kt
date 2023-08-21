package co.framework.data.remote

import co.framework.data.model.GitResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("users/{owner}/repos")
    suspend fun getRepos(@Path("owner") owner: String) : Response<List<GitResponse>>

    @GET("users/{owner}/repos")
    fun getRepo(@Path("owner") owner: String) : Observable<GitResponse>
}