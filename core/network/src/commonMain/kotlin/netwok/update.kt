package netwok

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json

suspend inline fun <reified T> update(url: String, body: Any): Result<T> {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }
    return try {
        val response: T = httpClient.patch(url) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()
        Result.success(response)
    } catch (ex: Exception) {
        Result.failure(ex)
    } finally {
        httpClient.close() // It's a good practice to close the client after the operation is complete
    }
}
