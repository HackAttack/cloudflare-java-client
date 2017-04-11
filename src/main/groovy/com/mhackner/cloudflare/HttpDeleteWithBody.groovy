package com.mhackner.cloudflare

import groovy.transform.PackageScope

import org.apache.http.annotation.NotThreadSafe
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase

@NotThreadSafe
@PackageScope
class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {

    public static final String METHOD_NAME = 'DELETE'

    HttpDeleteWithBody() {
        super()
    }

    @Override
    String getMethod() {
        METHOD_NAME
    }

}
