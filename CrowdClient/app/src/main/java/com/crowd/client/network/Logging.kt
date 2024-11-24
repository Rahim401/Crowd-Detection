package com.crowd.client.network

import com.crowd.client.utils.Colors
import retrofit2.Response


fun Response<Map<String, Any?>>.logApi(
    endpoint: String = "/unknown", title: String = "Unknown Api",
    expCodes: List<Int> = listOf(), others: Map<String, Any> = mapOf()
) {
    var startPad = ""
    if(title.isNotEmpty()) {
        val expCodeStr = expCodes.joinToString(", ")
        println("${Colors.Blue}#### $title($expCodeStr) ####${Colors.Reset}")
        startPad = "  "
    }
    println(
        "${startPad}*->Request, ToEndpoint: $endpoint, " +
                others.map { "${it.key}: ${it.value}" }
                    .joinToString(", ")
    )
    handle(
        onResponse = { code, message, data ->
            val processedData = (data as? Map<*, *>)?.toMutableMap()?.also {
                if("photo" in it)
                    it["photo"] = it["photo"].hashCode()
                it.remove("message")
                it.remove("error")
            } ?: data
            println("$startPad|->Response($code), Message: $message")
            println("$startPad|->Json: $processedData")
            if(expCodes.isNotEmpty()) {
                val testResMsg = if(code in expCodes) "${Colors.Green}Passed the test (✓)${Colors.Reset}"
                else {
                    if(expCodes.size == 1) "${Colors.Red}Failed($code != ${expCodes[0]}) (\uD835\uDC99)${Colors.Reset}"
                    else "${Colors.Red}Failed($code !in $expCodes) (\uD835\uDC99)${Colors.Reset}"
                }
                println("---> $testResMsg")
            }
        },
        onErrorHandling = { code, error ->
            println("---> ${Colors.Red}Error($code): $error${Colors.Reset}")
        }
    )
}

fun Response<Map<String, Any?>>.logApi(
    endpoint: String = "/unknown", title: String = "Unknown Api",
    expCode: Int, others: Map<String, Any> = mapOf()
) = logApi(endpoint, title, listOf(expCode), others)