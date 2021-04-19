package se.rbkn99.model

data class User(
    val id: Long,
    val name: String,
    val balance: Long,
    val shares: List<Shares>
)