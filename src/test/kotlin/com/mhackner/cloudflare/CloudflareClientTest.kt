package com.mhackner.cloudflare

import com.github.kittinunf.fuel.core.FuelError
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.junit.WireMockRule

import org.junit.Rule
import org.junit.Test

class CloudflareClientTest {

    @Rule @JvmField
    val wireMockRule = WireMockRule()

    val client = Cloudflare("key", "email", "http://localhost:8080")

    @Test
    fun getZone() {
        client.getZone("example.com")
        verify(getRequestedFor(urlEqualTo("/zones?name=example.com")))
    }

    @Test
    fun createZone() {
        client.createZone("example.com")
        verify(postRequestedFor(urlEqualTo("/zones"))
                .withRequestBody(equalToJson("""{"name": "example.com", "jump_start": false}""")))
    }

    @Test(expected = FuelError::class)
    fun createZoneError() {
        client.createZone("baddomain.com")
        // TODO Exception inspection
    }

    @Test
    fun createZoneInOrg() {
        client.createZone("example.com", "7c5dae5552338874e5053f2534d2767a")
        verify(postRequestedFor(urlEqualTo("/zones"))
                .withRequestBody(equalToJson("""{"name": "example.com", "jump_start": false, "organization": {"id": "7c5dae5552338874e5053f2534d2767a"}}""")))
    }

}
