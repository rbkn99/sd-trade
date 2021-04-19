package se.rbkn99.dao.impl

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import se.rbkn99.dao.SharesDAO
import se.rbkn99.dao.UserDAO
import se.rbkn99.model.Shares

class HttpSharesDAO(
    private val baseURL: String,
    private val userDAO: UserDAO
) : SharesDAO {

    private val client: HttpClient = HttpClient(Apache) {
        install(HttpTimeout) {
            requestTimeoutMillis = 5000
        }
        expectSuccess = false
    }

    override suspend fun buy(userId: Long, companyName: String, amount: Long) {
        val shares = client.get<Shares>("$baseURL?company-name=$companyName")
        try {
            userDAO.decreaseBalance(userId, shares.price!! * amount)
            client.get<HttpResponse>("$baseURL/buy?company-name=$companyName&amount=$amount")
            userDAO.addShares(userId, companyName, amount)
        } catch (e: IllegalStateException) {
        }
    }

    override suspend fun sell(userId: Long, companyName: String, amount: Long) {
        val shares = client.get<Shares>("$baseURL?company-name=$companyName")
        try {
            userDAO.removeShares(userId, companyName, amount)
            client.get<HttpResponse>("$baseURL/sell?company-name=$companyName&amount=$amount")
            userDAO.increaseBalance(userId, shares.price!! * amount)
        } catch (e: IllegalStateException) {
        }
    }

    override suspend fun getTotalWorth(shares: List<Shares>): List<Shares> {
        return shares.map {
            client.get<Shares>("$baseURL?company-name=${it.companyName}")
        }
    }

}