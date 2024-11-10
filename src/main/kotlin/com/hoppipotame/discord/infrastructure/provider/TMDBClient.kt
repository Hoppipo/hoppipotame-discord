package com.hoppipotame.discord.infrastructure.provider

import com.hoppipotame.discord.domain.model.Movie
import com.typesafe.config.Config
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
private data class TMDBSearchResult(
    val page: Int,
    val results: List<TMDBResultItem>
)

@Serializable
private data class TMDBResultItem(
    val adult: Boolean,
    val id: Int,
    val title: String,
    @SerialName("original_title")
    val originalTitle: String,
    @SerialName("release_date")
    val releaseDate: String,
    val overview: String,
    @SerialName("poster_path")
    val posterPath: String?,
)

class TMDBClient(config: Config, private val httpClient: HttpClient) {

    private val url = config.getString("api.url")
    private val jwtToken = config.getString("api.jwt")
    private val coverUrl = config.getString("api.image")

    fun search(query: String, language: String?): List<Movie> {
        return runBlocking {
            httpClient.request("$url/search/movie") {
                parameter("query", query)
                language?.let { isoCode -> parameter("language", isoCode) }
                bearerAuth(jwtToken)
            }
                .body<TMDBSearchResult>()
                .results
        }
            .map { item ->
                Movie(
                    id = item.id.toString(),
                    title = if (language != null) item.title else item.originalTitle,
                    releaseDate = item.releaseDate,
                    coverUrl = coverUrl + item.posterPath,
                    adult = item.adult
                )
            }
    }
}