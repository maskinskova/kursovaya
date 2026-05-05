package com.delivery

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis
import kotlin.test.Test
import kotlin.test.assertTrue

class ReactiveTest {

    @Test
    fun `server processes multiple slow requests concurrently`() = testApplication {
        application {
            routing {
                get("/slow") {
                    delay(2000)
                    call.respondText("OK")
                }
            }
        }

        val requests = 10
        val maxDelayMs = 2500L

        val totalTime = measureTimeMillis {
            coroutineScope {
                repeat(requests) {
                    launch {
                        val response: HttpResponse = client.get("/slow")
                        assert(response.status == HttpStatusCode.OK)
                    }
                }
            }
        }

        assertTrue(
            totalTime < maxDelayMs,
            "Ожидалось реактивное выполнение (< $maxDelayMs мс), но заняло $totalTime мс"
        )
    }
}