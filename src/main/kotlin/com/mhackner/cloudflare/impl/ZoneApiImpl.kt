package com.mhackner.cloudflare.impl

import com.mhackner.cloudflare.Cloudflare.Companion.makeRequest
import com.mhackner.cloudflare.api.ZoneApi
import com.mhackner.cloudflare.model.NewZone
import com.mhackner.cloudflare.model.Organization
import com.mhackner.cloudflare.model.Zone

internal class ZoneApiImpl : ZoneApi {

    override fun zoneIdByName(name: String): String {
        return listZones(name).single { it.name == name }.id
    }

    override fun createZone(name: String, jumpStart: Boolean, orgId: String?): Zone {
        val params = mutableMapOf()
        makeRequest()
    }

}
