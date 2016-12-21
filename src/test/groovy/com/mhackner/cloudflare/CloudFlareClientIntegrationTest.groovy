package com.mhackner.cloudflare

import org.junit.Test

class CloudFlareClientIntegrationTest {

    private final String cloudFlareApiKey = System.env.CLOUDFLARE_KEY
    private final String cloudFlareEmail = System.env.CLOUDFLARE_EMAIL
    private final String domain = System.env.CLOUDFLARE_DOMAIN

    @Test
    void createZone() {
        def client = new CloudFlareClient(cloudFlareApiKey, cloudFlareEmail)
        def zone = client.createZone(domain)
        assert zone.name == domain
        def zoneId = zone.id
        def record = client.createRecord(zoneId, 'CNAME', "www.$domain", domain)
        def recordId = record.id
        def records = client.getRecords(zoneId)
        assert records.size() == 1
        assert records.first().name == "www.$domain"
        assert records.first().type == 'CNAME'
        assert zoneId == record.zone_id
        client.deleteRecord(zoneId, recordId)
        assert client.getRecords(zoneId).empty
        client.deleteZone(zoneId)
        assert client.getZone(domain) == null
    }

    @Test
    void createZoneAsync() {
        def async = new AsyncCloudFlareClient(cloudFlareApiKey, cloudFlareEmail)
        def zone = async.createZone(domain)
        assert zone.get().name == domain
        def zoneId = zone.get().id
        def record = async.createRecord(zoneId, 'CNAME', "www.$domain", domain)
        def recordId = record.get().id
        def records = async.getRecords(zoneId).get()
        assert records.size() == 1
        assert records.first().name == "www.$domain"
        assert records.first().type == 'CNAME'
        assert zoneId == record.get().zone_id
        async.deleteRecord(zoneId, recordId).get()
        assert async.getRecords(zoneId).get().empty
        async.deleteZone(zoneId).get()
        assert async.getZone(domain).get().empty
    }

}
