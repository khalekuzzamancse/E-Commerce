package netwok

import kotlinx.serialization.Serializable

@Serializable
data class CartItem(
    val quantity: Int,
    val product: ProductEntity
)