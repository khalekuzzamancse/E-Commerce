package netwok

import kotlinx.serialization.Serializable

@Serializable
data class ProductEntity(
    val id: String,
    val name: String,
    val images: List<String>,
    val price: Int,
    val description: String,
    val amountAvailable: Int
)
@Serializable
data class PurchasedProductEntity(
    val purchaseId: String,
    val productName: String,
    val productImageUrl: String,
    val quantity: Int,
    val returnExpireDate: String,
)
