package se.rbkn99.dao.impl

import se.rbkn99.dao.SharesDAO
import se.rbkn99.model.Shares
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

class InMemorySharesDAO(val policy: PricePolicy) : SharesDAO {

    private val shares: MutableMap<String, Shares> = ConcurrentHashMap()

    private fun mapPrice(shares: Shares): Shares {
        return if (policy == PricePolicy.Manual) {
            shares
        } else {
            shares.copy(
                price = shares.price + Random(LocalDateTime.now().hour).nextLong(
                    -shares.price / 2, shares.price
                )
            )
        }
    }

    override fun getShares(): List<Shares> {
        return shares.values.toList().map(this::mapPrice)
    }

    override fun getShares(companyName: String): Shares {
        return mapPrice(
            shares[companyName] ?: throw IllegalArgumentException("No shares registered for '$companyName'")
        )
    }

    override fun addShares(companyName: String, price: Long) {
        check(price > 0) { "Shares can't cost non-positive amount" }
        check(companyName !in shares.keys) { "Company '$companyName' already registered" }
        shares[companyName] = Shares(
            companyName,
            0,
            price
        )
    }

    override fun increaseShares(companyName: String, amount: Long): Long {
        check(amount > 0) { "Can't increase by non-positive amount" }
        val oldValue = shares[companyName]
            ?: throw IllegalArgumentException("Company '$companyName' is not registered")
        shares[companyName] = oldValue.copy(amount = oldValue.amount + amount)
        return oldValue.price * amount
    }

    override fun decreaseShares(companyName: String, amount: Long): Long {
        check(amount > 0) { "Can't decreasy by non-positive amount" }
        val oldValue = shares[companyName]
            ?: throw IllegalArgumentException("Company '$companyName' is not registered")
        check(oldValue.amount >= amount) { "Can't decrease into negative amount" }
        shares[companyName] = oldValue.copy(amount = oldValue.amount - amount)
        return oldValue.price * amount
    }

    override fun changePrice(companyName: String, newPrice: Long) {
        check(policy == PricePolicy.Manual) { "Can't change price manually" }
        check(newPrice > 0) { "Shares can't const non-positive amount" }
        val oldValue = shares[companyName]
            ?: throw IllegalArgumentException("Company '$companyName' is not registered")
        shares[companyName] = oldValue.copy(price = newPrice)
    }

}