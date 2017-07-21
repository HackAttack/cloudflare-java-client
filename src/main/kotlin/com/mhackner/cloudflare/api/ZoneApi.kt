package com.mhackner.cloudflare.api

import com.mhackner.cloudflare.model.*

internal interface ZoneApi {
    fun zoneIdByName(name: String): String
    fun createZone(name: String, jumpStart: Boolean, orgId: String?): Zone
    fun zoneActivationCheck(zoneId: String): Response
    fun listZones(vararg names: String): List<Zone>
    fun zoneDetails(zoneId: String): Zone
    fun zoneSetPaused(zoneId: String, paused: Boolean): Zone
    fun zoneSetVanityNameServers(zoneId: String, nameServers: List<String>): Zone
    fun zoneSetRatePlan(zoneId: String, plan: ZoneRatePlan): Zone
    fun purgeEverything(zoneId: String): PurgeCacheResponse
    fun purgeCache(zoneId: String, files: List<String>?, tags: List<String>?): PurgeCacheResponse
    fun deleteZone(zoneId: String): ZoneId
    fun availableZoneRatePlans(zoneId: String): List<ZoneRatePlan>
    fun zoneAnalyticsDashboard(zoneId: String, options: ZoneAnalyticsOptions): ZoneAnalyticsData
    fun zoneAnalyticsByColocation(zoneId: String, options: ZoneAnalyticsOptions): List<ZoneAnalyticsColocation>
    fun zoneSslSettings(zoneId: String): ZoneSslSetting
}
