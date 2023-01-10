package co.framework.data.mapper

import co.framework.domain.model.TestResponse

object Mapper {
    fun mapperApi(response : List<TestResponse>?) : List<TestResponse>? {
        return response?.toDomain()
    }

    fun List<TestResponse>.toDomain() : List<TestResponse> {
        return this.map {
            TestResponse(
                it.name,
                it.id,
                it.date,
                it.url
            )
        }
    }
}