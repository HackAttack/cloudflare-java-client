package com.mhackner.cloudflare

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.*
import com.mhackner.cloudflare.api.*
import com.mhackner.cloudflare.impl.*

class Cloudflare @JvmOverloads constructor(apiKey: String,
                                           email: String,
                                           serviceKey: String?,
                                           baseUrl: String = "https://api.cloudflare.com/client/v4"
) : ZoneApi by zoneApi {

    internal enum class AuthType {
        EMAIL, SERVICE
    }

    internal enum class Method {
        GET, POST, PUT, DELETE, PATCH
    }

    init {
        require(apiKey.isNotBlank() && email.isNotBlank()) {
            "Invalid credentials: key & email must not be empty"
        }
        FuelManager.instance.apply {
            basePath = baseUrl.trimEnd('/')
            baseHeaders = mapOf(
                    "X-Auth-Key" to apiKey,
                    "X-Auth-Email" to email,
                    "X-Auth-User-Service-Key" to serviceKey.orEmpty(),
//                    "User-Agent" to "HackAttack CloudflareClient",
                    "Content-Type" to "application/json")
        }
    }

    internal companion object {
        val mapper: ObjectMapper = jacksonObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
//                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)

        fun makeRequest(method: Method, uri: String, body: Any? = null,
                        authType: AuthType = AuthType.EMAIL): ByteArray {
            val body = if (body != null) {
                mapper.writeValueAsBytes(body)
            } else {
                null
            }
            val authHeaders = when (authType) {
                AuthType.EMAIL -> FuelManager.instance.baseHeaders!! - "X-Auth-User-Service-Key"
                AuthType.SERVICE -> {
                    require(!FuelManager.instance.baseHeaders!!["X-Auth-User-Service-Key"].isNullOrBlank()) {
                        "Invalid credentials: service key must not be empty"
                    }
                    FuelManager.instance.baseHeaders!! - "X-Auth-Key" - "X-Auth-Email"
                }
            }
            var request = when (method) {
                Method.GET -> uri.httpGet()
                Method.POST -> uri.httpPost()
                Method.PUT -> uri.httpPut()
                Method.DELETE -> uri.httpDelete()
                Method.PATCH -> uri.httpPatch()
            }.header(authHeaders)
            if (body != null) {
                request = request.body(body)
            }
            return request.response().component3().get()
        }
    }
}

private val zoneApi = ZoneApiImpl()
