package com.mhackner.cloudflare.model

data class ResponseInfo(val code: Int,
                        val message: String)

data class Response(val success: Boolean,
                    val errors: List<ResponseInfo>,
                    val messages: List<ResponseInfo>)

data class ResultInfo(val page: Int,
                      val perPage: Int,
                      val totalPages: Int,
                      val count: Int,
                      val totalCount: Int)
