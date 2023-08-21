package co.framework.data.mapper

import co.framework.data.model.GitResponse
import co.framework.domain.model.GitData

object Mapper {
    fun mapperApi(response : List<GitResponse>?) : List<GitData>? {
        return response?.toDomain()
    }

    fun List<GitResponse>.toDomain() : List<GitData> {
        return this.map {
            GitData(
                it.name,
                it.id,
                it.date,
                it.url
            )
        }
    }
}