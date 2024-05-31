package netwok

import kotlinx.serialization.Serializable

@Serializable
data class ProductDetailsEntity(
    val productId: String,
    val name: String,
    val imagesLink: List<String>,
    val description: String,
    val price: Int,
    val discountByPrice:DiscountByPriceEntity?,
    val discountByProduct: ProductOfferEntity?,
    val reviews: List<ProductReviewEntity>
)

@Serializable
data class ProductReviewEntity(
    val reviewerName: String,
    val comment: String,
    val imagesLink: List<String>
)

@Serializable
data class ProductOfferEntity(
    val productName: String,
    val imageLink: String,
    val requiredQuantity: String,
    val freeQuantity: String
)
@Serializable
data class DiscountByPriceEntity(
    val amount: Int,
    val expirationTimeInMs: Long
)
