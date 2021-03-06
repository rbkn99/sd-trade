package se.rbkn99

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*
import se.rbkn99.dao.SharesDAO
import se.rbkn99.dao.impl.InMemorySharesDAO
import se.rbkn99.dao.impl.PricePolicy

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    val dao: SharesDAO = InMemorySharesDAO(PricePolicy.Manual)

    routing {
        get("/") {
            try {
                val companyName = call.getParameter<String>("company-name")
                if (companyName == null) {
                    call.respond(dao.getShares())
                } else {
                    call.respond(dao.getShares(companyName))
                }
            } catch (e: Exception) {
                call.respondText("Error: ${e.message}", status = HttpStatusCode.BadRequest)
            }
        }

        get("/admin/register") {
            try {
                val companyName = call.requireParameter<String>("company-name")
                val price = call.requireParameter<Long>("price")
                val amount = call.getParameter<Long>("amount")

                dao.addShares(companyName, price)
                if (amount != null) {
                    dao.increaseShares(companyName, amount)
                }
                call.respond("SUCCESS")
            } catch (e: Exception) {
                call.respondText("Error: ${e.message}", status = HttpStatusCode.BadRequest)
            }
        }

        get("/sell") {
            try {
                val companyName = call.requireParameter<String>("company-name")
                val amount = call.requireParameter<Long>("amount")
                call.respond(dao.increaseShares(companyName, amount))
            } catch (e: Exception) {
                call.respondText("Error: ${e.message}", status = HttpStatusCode.BadRequest)
            }
        }

        get("/buy") {
            try {
                val companyName = call.requireParameter<String>("company-name")
                val amount = call.requireParameter<Long>("amount")
                call.respond(dao.decreaseShares(companyName, amount))
            } catch (e: Exception) {
                call.respondText("Error: ${e.message}", status = HttpStatusCode.BadRequest)
            }
        }

        get("/admin/change") {
            try {
                val companyName = call.requireParameter<String>("company-name")
                val newPrice = call.requireParameter<Long>("new-price")

                dao.changePrice(companyName, newPrice)
                call.respond("SUCCESS")
            } catch (e: Exception) {
                call.respondText("Error: ${e.message}", status = HttpStatusCode.BadRequest)
            }
        }
    }
}

