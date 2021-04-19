package se.rbkn99.model

data class Shares(
    val companyName: String,
    val amount: Long,
    val price: Long? = null
)
