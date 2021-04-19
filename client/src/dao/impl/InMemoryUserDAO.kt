package se.rbkn99.dao.impl

import se.rbkn99.dao.UserDAO
import se.rbkn99.model.Shares
import se.rbkn99.model.User
import java.util.concurrent.ConcurrentHashMap

object InMemoryUserDAO : UserDAO {

    private val users: MutableMap<Long, User> = ConcurrentHashMap()

    override fun getUser(id: Long): User {
        return users[id] ?: throw IllegalArgumentException("No users with id '$id'")
    }

    override fun addUser(id: Long, name: String) {
        check(id !in users.keys) { "User with this id already exists" }
        users[id] = User(id, name, 0, listOf())
    }

    override fun increaseBalance(id: Long, amount: Long) {
        check(amount > 0) { "Can't increase by non-positive amount" }
        val oldValue = users[id] ?: error("No user with id '$id'")
        users[id] = oldValue.copy(balance = oldValue.balance + amount)
    }

    override fun decreaseBalance(id: Long, amount: Long) {
        check(amount > 0) { "Can't decrease by non-positive amount" }
        val oldValue = users[id] ?: error("No user with id '$id'")
        check(oldValue.balance >= amount) { "Can't decrease into negative amount" }
        users[id] = oldValue.copy(balance = oldValue.balance - amount)
    }

    override fun addShares(id: Long, companyName: String, amount: Long) {
        check(amount > 0) { "Can't increase by non-positive amount" }
        val oldValue = users[id] ?: error("No user with id '$id'")

        var flag = false
        val newShares = oldValue.shares.map {
            if (it.companyName == companyName) {
                flag = true
                it.copy(amount = it.amount + amount)
            } else {
                it
            }
        }
        users[id] = oldValue.copy(shares = if (flag) newShares else newShares + Shares(companyName, amount))
    }

    override fun removeShares(id: Long, companyName: String, amount: Long) {
        check(amount > 0) { "Can't decrease by non-positive amount" }
        val oldValue = users[id] ?: error("No user with id '$id'")

        val newShares = oldValue.shares.map {
            if (it.companyName == companyName) {
                if (it.amount < amount) {
                    throw IllegalArgumentException("Can't decrease into negative amount")
                } else {
                    it.copy(amount = it.amount - amount)
                }
            } else {
                it
            }
        }.filter { it.amount >= 0 }
        users[id] = oldValue.copy(shares = newShares)
    }

}