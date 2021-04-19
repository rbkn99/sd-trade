package se.rbkn99.dao

import se.rbkn99.model.Shares

interface SharesDAO {

    suspend fun buy(userId: Long, companyName: String, amount: Long)

    suspend fun sell(userId: Long, companyName: String, amount: Long)

    suspend fun getTotalWorth(shares: List<Shares>): List<Shares>

}