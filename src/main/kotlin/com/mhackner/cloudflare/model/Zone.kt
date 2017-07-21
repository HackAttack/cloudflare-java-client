package com.mhackner.cloudflare.model

import com.fasterxml.jackson.annotation.JsonUnwrapped

import java.util.Date

data class Owner(val id: String,
                 val email: String,
                 val ownerType: String)

data class Zone(val id: String,
                val name: String,
                val developmentMode: Int,
                val originalNameServers: List<String>,
                val originalRegistrar: String,
                val originalDnshost: String,
                val createdOn: Date,
                val modifiedOn: Date,
                val nameServers: List<String>,
                val owner: Owner,
                val permissions: List<String>,
                val plan: ZoneRatePlan,
                val planPending: ZoneRatePlan?,
                val status: String,
                val paused: Boolean,
                val type: String,
                val host: Host,
                val vanityNameServers: List<String>,
                val betas: List<String>,
                val deactivationReason: String,
                val meta: ZoneMeta) {
    data class Host(val name: String,
                    val website: String)
}

data class ZoneMeta(val pageRuleQuota: Int,
                    val wildcardProxiable: Boolean,
                    val phishingDetected: Boolean)

data class ZoneRatePlan(val id: String,
                        val name: String?,
                        val price: Int?,
                        val currency: String?,
                        val duration: Int?,
                        val frequency: String?,
                        val components: List<ZoneRatePlanComponents>)

data class ZoneRatePlanComponents(val name: String,
                                  val default: Int,
                                  val unitPrice: Int)

data class ZoneId(val id: String)

data class ZoneResponse(@JsonUnwrapped val response: Response,
                        val result: Zone)

data class ZonesResponse(@JsonUnwrapped val response: Response,
                         val result: List<Zone>)

data class ZoneIdResponse(@JsonUnwrapped val response: Response,
                          val result: ZoneId)

data class AvailableZoneRatePlansResponse(@JsonUnwrapped val response: Response,
                                          val result: List<ZoneRatePlan>)

data class ZoneRatePlanResponse(@JsonUnwrapped val response: Response,
                                val result: ZoneRatePlan)

data class ZoneSetting(val id: String,
                       val editable: Boolean,
                       val modifiedOn: Date?,
                       val value: Any,
                       val timeRemaining: Int?)

data class ZoneSettingResponse(@JsonUnwrapped val response: Response,
                               val result: List<ZoneSetting>)

data class ZoneSslSetting(val id: String,
                          val editable: Boolean,
                          val modifiedOn: Date?,
                          val value: String,
                          val certificateStatus: String)

data class ZoneSslSettingResponse(@JsonUnwrapped val response: Response,
                                  val result: ZoneSslSetting)

data class ZoneAnalyticsData(val totals: ZoneAnalytics,
                             val timeseries: List<ZoneAnalytics>)

data class ZoneAnalyticsDataResponse(@JsonUnwrapped val response: Response,
                                     val result: ZoneAnalyticsData)

data class ZoneAnalyticsColocation(val coloId: String,
                                   val timeseries: List<ZoneAnalytics>)

data class ZoneAnalyticsColocationResponse(@JsonUnwrapped val response: Response,
                                           val result: List<ZoneAnalyticsColocation>)

data class ZoneAnalytics(val since: Date,
                         val until: Date,
                         val requests: Requests,
                         val bandwidth: Bandwidth,
                         val threats: Threats,
                         val pageviews: Pageviews,
                         val uniques: Uniques) {
    data class Ssl(val encrypted: Int,
                   val unencrypted: Int)

    data class Requests(val all: Int,
                        val cached: Int,
                        val uncached: Int,
                        val contentType: Map<String, Int>,
                        val country: Map<String, Int>,
                        val ssl: Ssl,
                        val httpStatus: Map<String, Int>)

    data class Bandwidth(val all: Int,
                         val cached: Int,
                         val uncached: Int,
                         val contentType: Map<String, Int>,
                         val country: Map<String, Int>,
                         val ssl: Ssl)

    data class Threats(val all: Int,
                       val country: Map<String, Int>,
                       val type: Map<String, Int>)

    data class Pageviews(val all: Int,
                         val searchEngines: Map<String, Int>)

    data class Uniques(val all: Int)
}

data class ZoneAnalyticsOptions(val since: Date,
                                val until: Date,
                                val continuous: Boolean)

data class PurgeCacheResponse(@JsonUnwrapped val response: Response)

data class NewZone(val name: String,
                   val jumpStart: Boolean,
                   val organization: Organization?)

data class ZoneOptions(val paused: Boolean?,
                       val vanityNameServers: List<String>?,
                       val plan: ZoneRatePlan?)
