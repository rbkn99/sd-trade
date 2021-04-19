package se.rbkn99

import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Assert.*
import org.junit.Test

class SharesBaseTest {
    @Test
    fun testRoot() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("[ ]", response.content)
            }
        }
    }

    @Test
    fun testRegister() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/admin/register?company-name=xx&price=100&amount=200").apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }
            handleRequest(HttpMethod.Get, "/?company-name=xx").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotEquals("[ ]", response.content)
            }
        }
    }

    @Test
    fun testPriceChange() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/admin/register?company-name=xx&price=100&amount=200").apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }
            handleRequest(HttpMethod.Get, "/admin/change?company-name=xx&new-price=101").apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }
            handleRequest(HttpMethod.Get, "/?company-name=xx").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertTrue(response.content!!.contains("101"))
            }
        }
    }

    @Test
    fun testBuy() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/admin/register?company-name=xx&price=100&amount=200").apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }
            handleRequest(HttpMethod.Get, "/buy?company-name=xx&amount=200").apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }
            handleRequest(HttpMethod.Get, "/buy?company-name=xx&amount=1").apply {
                assertNotEquals(HttpStatusCode.OK, response.status())
            }
        }
    }
}
