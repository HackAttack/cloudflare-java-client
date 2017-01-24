package com.mhackner.cloudflare

import com.google.common.util.concurrent.RateLimiter

import org.apache.http.HttpHost
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.HttpClient
import org.apache.http.client.ResponseHandler
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.conn.ClientConnectionManager
import org.apache.http.params.HttpParams
import org.apache.http.protocol.HttpContext

class RateLimitingHttpClient implements HttpClient {

    private final HttpClient delegate
    private final RateLimiter rateLimiter

    RateLimitingHttpClient(HttpClient delegate, double qps) {
        this.delegate = delegate
        rateLimiter = RateLimiter.create(qps)
    }

    @Override
    HttpParams getParams() {
        delegate.params
    }

    @Override
    ClientConnectionManager getConnectionManager() {
        delegate.connectionManager
    }

    @Override
    HttpResponse execute(HttpUriRequest request) throws IOException, ClientProtocolException {
        rateLimiter.acquire()
        delegate.execute(request)

    }

    @Override
    HttpResponse execute(HttpUriRequest request, HttpContext context) throws IOException, ClientProtocolException {
        rateLimiter.acquire()
        delegate.execute(request, context)
    }

    @Override
    HttpResponse execute(HttpHost target, HttpRequest request) throws IOException, ClientProtocolException {
        rateLimiter.acquire()
        delegate.execute(target, request)
    }

    @Override
    HttpResponse execute(HttpHost target, HttpRequest request, HttpContext context)
            throws IOException, ClientProtocolException {
        rateLimiter.acquire()
        delegate.execute(target, request, context)
    }

    @Override
    <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler)
            throws IOException, ClientProtocolException {
        rateLimiter.acquire()
        delegate.execute(request, responseHandler)
    }

    @Override
    <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context)
            throws IOException, ClientProtocolException {
        rateLimiter.acquire()
        delegate.execute(request, responseHandler, context)
    }

    @Override
    <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler)
            throws IOException, ClientProtocolException {
        rateLimiter.acquire()
        delegate.execute(target, request, responseHandler)
    }

    @Override
    <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context)
            throws IOException, ClientProtocolException {
        rateLimiter.acquire()
        delegate.execute(target, request, responseHandler, context)
    }

}
