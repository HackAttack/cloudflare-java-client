package com.mhackner.cloudflare.model

data class Organization(val id: String?,
                        val name: String?,
                        val status: String?,
                        val permissions: List<String>?,
                        val roles: List<String>?)
