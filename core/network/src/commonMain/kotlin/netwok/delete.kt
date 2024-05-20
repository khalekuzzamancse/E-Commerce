package netwok

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json

suspend inline fun <reified T> delete(url: String): Result<T> {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }
    return try {
        val response: T = httpClient.delete(url) {
            contentType(ContentType.Application.Json)
        }.body()
        Result.success(response)
    } catch (ex: Exception) {
        Result.failure(ex)
    } finally {
        httpClient.close() // Always close the client to free up resources
    }
}
