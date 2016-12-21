package com.mhackner.cloudflare

import groovyx.net.http.HttpResponseException

import com.github.tomakehurst.wiremock.junit.WireMockRule

import org.junit.Test
import org.junit.Rule

import java.util.concurrent.ExecutionException

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static org.junit.Assert.fail

class CloudFlareClientTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule()

    private static final client = new CloudFlareClient('key', 'email', 'http://localhost:8080/')
    private static final asyncClient = new AsyncCloudFlareClient('key', 'email', 'http://localhost:8080/')

    @Test
    void getZone() {
        client.getZone('example.com')
        asyncClient.getZone('example.com').get()
        verify(2, getRequestedFor(urlEqualTo('/zones?name=example.com')))
    }

    @Test
    void createZone() {
        client.createZone('example.com')
        asyncClient.createZone('example.com').get()
        verify(2, postRequestedFor(urlEqualTo('/zones'))
                .withRequestBody(equalToJson('{"name": "example.com", "jump_start": false}')))
    }

    @Test
    void createZoneError() {
        try {
            client.createZone('baddomain.com')
            fail('Expected an HttpResponseException to be thrown')
        } catch (HttpResponseException ex) {
            assert ex.statusCode == 400
            assert ex.response.data.errors == [[code: 1097, message: 'This zone is banned and cannot be added to CloudFlare at this time, please contact CloudFlare Support.']]
        }
    }

    @Test
    void createZoneErrorAsync() {
        try {
            asyncClient.createZone('baddomain.com').get()
            fail('Expected an ExecutionException to be thrown')
        } catch (ExecutionException ex) {
            def cause = ex.cause as HttpResponseException
            assert cause.statusCode == 400
            assert cause.response.data.errors == [[code: 1097, message: 'This zone is banned and cannot be added to CloudFlare at this time, please contact CloudFlare Support.']]
        }
    }

    @Test
    void createZoneInOrg() {
        client.createZone('example.com', '7c5dae5552338874e5053f2534d2767a')
        asyncClient.createZone('example.com', '7c5dae5552338874e5053f2534d2767a').get()
        verify(2, postRequestedFor(urlEqualTo('/zones'))
                .withRequestBody(equalToJson('{"name": "example.com", "jump_start": false, "organization": {"id": "7c5dae5552338874e5053f2534d2767a"}}')))
    }

    @Test
    void updatePlanForZone() {
        client.updatePlanForZone('023e105f4ecef8ad9ca31a8372d0c353', 'e592fd9519420ba7405e1307bff33214')
        asyncClient.updatePlanForZone('023e105f4ecef8ad9ca31a8372d0c353', 'e592fd9519420ba7405e1307bff33214').get()
        verify(2, patchRequestedFor(urlEqualTo('/zones/023e105f4ecef8ad9ca31a8372d0c353'))
                .withRequestBody(equalToJson('{"plan": {"id": "e592fd9519420ba7405e1307bff33214"}}')))
    }

    @Test
    void deleteZone() {
        client.deleteZone('023e105f4ecef8ad9ca31a8372d0c353')
        asyncClient.deleteZone('023e105f4ecef8ad9ca31a8372d0c353').get()
        verify(2, deleteRequestedFor(urlEqualTo('/zones/023e105f4ecef8ad9ca31a8372d0c353')))
    }

    @Test
    void getRecords() {
        client.getRecords('023e105f4ecef8ad9ca31a8372d0c353')
        asyncClient.getRecords('023e105f4ecef8ad9ca31a8372d0c353').get()
        verify(2, getRequestedFor(urlEqualTo('/zones/023e105f4ecef8ad9ca31a8372d0c353/dns_records?per_page=1000')))
    }

    @Test
    void createRecord() {
        client.createRecord('023e105f4ecef8ad9ca31a8372d0c353', 'A', 'example.com', '1.2.3.4')
        asyncClient.createRecord('023e105f4ecef8ad9ca31a8372d0c353', 'A', 'example.com', '1.2.3.4').get()
        verify(2, postRequestedFor(urlEqualTo('/zones/023e105f4ecef8ad9ca31a8372d0c353/dns_records'))
                .withRequestBody(equalToJson('{"type": "A", "name": "example.com", "content": "1.2.3.4"}')))
    }

    @Test
    void deleteRecord() {
        client.deleteRecord('023e105f4ecef8ad9ca31a8372d0c353', '372e67954025e0ba6aaa6d586b9e0b59')
        asyncClient.deleteRecord('023e105f4ecef8ad9ca31a8372d0c353', '372e67954025e0ba6aaa6d586b9e0b59').get()
        verify(2, deleteRequestedFor(urlEqualTo('/zones/023e105f4ecef8ad9ca31a8372d0c353/dns_records/372e67954025e0ba6aaa6d586b9e0b59')))
    }

    @Test
    void updateRecord() {
        def record = [
                id: '372e67954025e0ba6aaa6d586b9e0b59',
                type: 'A',
                name: 'example.com',
                content: '1.2.3.4',
                proxiable: true,
                proxied: false,
                ttl: 120,
                locked: false,
                zone_id: '023e105f4ecef8ad9ca31a8372d0c353',
                zone_name: 'example.com',
                created_on: '2014-01-01T05:20:00.12345Z',
                modified_on: '2014-01-01T05:20:00.12345Z',
                data: [:]
        ]
        client.updateRecord(record)
        asyncClient.updateRecord(record)
        verify(2, putRequestedFor(urlEqualTo('/zones/023e105f4ecef8ad9ca31a8372d0c353/dns_records/372e67954025e0ba6aaa6d586b9e0b59'))
                .withRequestBody(equalToJson('''
                        {
                            "id": "372e67954025e0ba6aaa6d586b9e0b59",
                            "type": "A",
                            "name": "example.com",
                            "content": "1.2.3.4",
                            "proxiable": true,
                            "proxied": false,
                            "ttl": 120,
                            "locked": false,
                            "zone_id": "023e105f4ecef8ad9ca31a8372d0c353",
                            "zone_name": "example.com",
                            "created_on": "2014-01-01T05:20:00.12345Z",
                            "modified_on": "2014-01-01T05:20:00.12345Z",
                            "data": {}
                        }''')))
    }

}
