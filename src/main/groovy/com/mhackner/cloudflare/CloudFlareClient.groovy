package com.mhackner.cloudflare

import groovyx.net.http.ContentType
import groovyx.net.http.RESTClient

class CloudFlareClient {

    private final RESTClient http

    CloudFlareClient(String apiKey, String email, String url = 'https://api.cloudflare.com/client/v4/') {
        http = new RESTClient(url, ContentType.JSON)
        http.headers = ['X-Auth-Key': apiKey, 'X-Auth-Email': email, 'User-Agent': 'HackAttack CloudFlareClient']
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

    Map deleteZone(String zoneId) {
        http.delete(path: "zones/$zoneId").data.result
    }

    List<Map> getRecords(String zoneId, Map params = [:]) {
        def data = http.get(path: "zones/$zoneId/dns_records", query: params + [per_page: 1000]).data
        if (data.result_info.total_pages > 1) {
            throw new RuntimeException('Data took up multiple pages and paging is not supported yet')
        }
        data.result
    }

    Map createRecord(String zoneId, String type, String name, String content, Integer priority = null) {
        def params = [type: type, name: name, content: content]
        if (priority != null) {
            params.priority = priority
        }
        http.post(path: "zones/$zoneId/dns_records", body: params).data.result
    }

    Map deleteRecord(String zoneId, String recordId) {
        http.delete(path: "zones/$zoneId/dns_records/$recordId").data.result
    }

    List<Map> deleteRecords(String zoneId, Map params = [:]) {
        getRecords(zoneId, params).collect { deleteRecord(zoneId, it.id) }
    }

    Map updateRecord(Map record) {
        http.put(path: "zones/$record.zone_id/dns_records/$record.id", body: record).data.result
    }

}
