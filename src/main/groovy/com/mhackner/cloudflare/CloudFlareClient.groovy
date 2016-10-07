package com.mhackner.cloudflare

import groovyx.net.http.ContentType
import groovyx.net.http.RESTClient

class CloudFlareClient {

    private final RESTClient http

    CloudFlareClient(String apiKey, String email) {
        http = new RESTClient('https://api.cloudflare.com/client/v4/', ContentType.JSON)
        http.headers = ['X-Auth-Key': apiKey, 'X-Auth-Email': email]
    }

    Map getZone(String domain) {
        def result = http.get(path: 'zones', query: [name: domain]).data.result
        assert result.size() <= 1
        result ? result.first() : null
    }

    Map createZone(String domain, String orgId = null) {
        def params = [name: domain, jump_start: false]
        if (orgId) {
            params.organization = [id: orgId]
        }
        http.post(path: 'zones', body: params).data.result
    }

    Map updatePlanForZone(String zoneId, String planId) {
        http.patch(path: "zones/$zoneId", body: [plan: [id: planId]]).data.result
    }

    void deleteZone(String zoneId) {
        http.delete(path: "zones/$zoneId")
    }

    List<Map> getRecords(String zoneId, Map params = [:]) {
        http.get(path: "zones/$zoneId/dns_records", query: params).data.result
    }

    Map createRecord(String zoneId, String type, String name, String content, Integer priority = null) {
        def params = [type: type, name: name, content: content]
        if (priority) {
            params.priority = priority
        }
        http.post(path: "zones/$zoneId/dns_records", body: params).data.result
    }

    void deleteRecord(String zoneId, String recordId) {
        http.delete(path: "zones/$zoneId/dns_records/$recordId")
    }

    void deleteRecords(String zoneId, Map params = [:]) {
        getRecords(zoneId, params).each { deleteRecord(zoneId, it.id) }
    }

    void updateRecord(Map record) {
        http.put(path: "zones/$record.zone_id/dns_records/$record.id", body: record)
    }

}
