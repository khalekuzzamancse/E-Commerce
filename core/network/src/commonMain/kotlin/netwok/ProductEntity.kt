package netwok

import kotlinx.serialization.Serializable

@Serializable
data class ProductEntity(
    val id: String,
    val name: String,
    val images: List<String>,
    val price: Int,
    val description: String,
    val type: String,
    val amountAvailable: Int
)
