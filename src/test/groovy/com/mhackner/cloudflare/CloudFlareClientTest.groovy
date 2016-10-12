package com.mhackner.cloudflare

import com.github.tomakehurst.wiremock.junit.WireMockRule

import org.junit.Test
import org.junit.Rule

import static com.github.tomakehurst.wiremock.client.WireMock.*

class CloudFlareClientTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule()

    private static final client = new CloudFlareClient('key', 'email', 'http://localhost:8080/')

    @Test
    void getZone() {
        client.getZone('example.com')
        verify(getRequestedFor(urlEqualTo('/zones?name=example.com')))
    }

    @Test
    void createZone() {
        client.createZone('example.com')
        verify(postRequestedFor(urlEqualTo('/zones'))
                .withRequestBody(equalToJson('{"name": "example.com", "jump_start": false}')))
    }

    @Test
    void createZoneInOrg() {
        client.createZone('example.com', '7c5dae5552338874e5053f2534d2767a')
        verify(postRequestedFor(urlEqualTo('/zones'))
                .withRequestBody(equalToJson('{"name": "example.com", "jump_start": false, "organization": {"id": "7c5dae5552338874e5053f2534d2767a"}}')))
    }

    @Test
    void updatePlanForZone() {
        client.updatePlanForZone('023e105f4ecef8ad9ca31a8372d0c353', 'e592fd9519420ba7405e1307bff33214')
        verify(patchRequestedFor(urlEqualTo('/zones/023e105f4ecef8ad9ca31a8372d0c353'))
                .withRequestBody(equalToJson('{"plan": {"id": "e592fd9519420ba7405e1307bff33214"}}')))
    }

    @Test
    void deleteZone() {
        client.deleteZone('023e105f4ecef8ad9ca31a8372d0c353')
        verify(deleteRequestedFor(urlEqualTo('/zones/023e105f4ecef8ad9ca31a8372d0c353')))
    }

    @Test
    void getRecords() {
        client.getRecords('023e105f4ecef8ad9ca31a8372d0c353')
        verify(getRequestedFor(urlEqualTo('/zones/023e105f4ecef8ad9ca31a8372d0c353/dns_records')))
    }

    @Test
    void createRecord() {
        client.createRecord('023e105f4ecef8ad9ca31a8372d0c353', 'A', 'example.com', '1.2.3.4')
        verify(postRequestedFor(urlEqualTo('/zones/023e105f4ecef8ad9ca31a8372d0c353/dns_records'))
                .withRequestBody(equalToJson('{"type": "A", "name": "example.com", "content": "1.2.3.4"}')))
    }

    @Test
    void deleteRecord() {
        client.deleteRecord('023e105f4ecef8ad9ca31a8372d0c353', '372e67954025e0ba6aaa6d586b9e0b59')
        verify(deleteRequestedFor(urlEqualTo('/zones/023e105f4ecef8ad9ca31a8372d0c353/dns_records/372e67954025e0ba6aaa6d586b9e0b59')))
    }

    @Test
    void updateRecord() {
        client.updateRecord([
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
        ])
        verify(putRequestedFor(urlEqualTo('/zones/023e105f4ecef8ad9ca31a8372d0c353/dns_records/372e67954025e0ba6aaa6d586b9e0b59'))
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
