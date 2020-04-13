package app.ssnc.oasis.handler.firewall.entity

import app.ssnc.oasis.entity.model.enum.Protocol

data class SearchRuleReq (
    val srcAddr: String,
    val dstAddr: String,
    val dstPort: String,
    val protocol: String,
    val startDate: String,
    val expireDate: String,
    val withDiscovery: String = "true"
)
