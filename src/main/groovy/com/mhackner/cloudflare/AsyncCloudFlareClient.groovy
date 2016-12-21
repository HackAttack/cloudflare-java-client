package com.mhackner.cloudflare

import groovyx.net.http.AsyncHTTPBuilder
import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseException
import groovyx.net.http.Method

import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class AsyncCloudFlareClient {

    private final AsyncHTTPBuilder http

    AsyncCloudFlareClient(String apiKey, String email, String url = 'https://api.cloudflare.com/client/v4/') {
        http = new AsyncHTTPBuilder(uri: url, contentType: ContentType.JSON)
        http.headers = ['X-Auth-Key': apiKey, 'X-Auth-Email': email, 'User-Agent': 'HackAttack AsyncCloudFlareClient']

        // Mimic the response body parsing of RESTClient
        http.handler.failure = { resp, data ->
            resp.data = http.handler.success(resp, data)
            throw new HttpResponseException(resp)
        }
    }

    Future<List<Map>> getZone(String domain) {
        new ResultExtractor(http.get(path: 'zones', query: [name: domain]))
    }

    Future<Map> createZone(String domain, String orgId = null) {
        def params = [name: domain, jump_start: false]
        if (orgId) {
            params.organization = [id: orgId]
        }
        new ResultExtractor(http.post(path: 'zones', body: params, requestContentType: ContentType.JSON))
    }

    Future<Map> updatePlanForZone(String zoneId, String planId) {
        new ResultExtractor(http.request(Method.PATCH) { req ->
            uri.path = "zones/$zoneId"
            body = [plan: [id: planId]]
        })
    }

    Future<Map> deleteZone(String zoneId) {
        http.request(Method.DELETE) { req ->
            uri.path = "zones/$zoneId"
        }
    }

    Future<List<Map>> getRecords(String zoneId, Map params = [:]) {
        new ResultExtractor(http.get(path: "zones/$zoneId/dns_records", query: params + [per_page: 1000]))
    }

    Future<Map> createRecord(String zoneId, String type, String name, String content, Integer priority = null) {
        def params = [type: type, name: name, content: content]
        if (priority != null) {
            params.priority = priority
        }
        new ResultExtractor(http.post(path: "zones/$zoneId/dns_records", body: params, requestContentType: ContentType.JSON))
    }

    Future<Map> deleteRecord(String zoneId, String recordId) {
        http.request(Method.DELETE) { req ->
            uri.path = "zones/$zoneId/dns_records/$recordId"
        }
    }

    Future<Map> updateRecord(Map record) {
        http.request(Method.PUT) { req ->
            uri.path = "zones/$record.zone_id/dns_records/$record.id"
            body = record
        }
    }

    private static class ResultExtractor {
        @Delegate
        Future delegate

        ResultExtractor(Future delegate) {
            this.delegate = delegate
        }

        @Override
        Object get() throws InterruptedException, ExecutionException {
            return delegate.get().result
        }

        @Override
        Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return delegate.get(timeout, unit).result
        }
    }

}
