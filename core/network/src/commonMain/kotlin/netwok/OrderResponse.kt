package netwok

import kotlinx.serialization.Serializable
@Serializable
data class OrderBillResponse(
    val bills: List<OrderResponse>?,
    val total: Int
)
@Serializable
data class OrderResponse(
    val productId: String,
    val productName: String,
    val unitPrice: Int,
    val quantity: Int,
    val discount: Int,
    val originalPrice: Int,
    val discountedPrice: Int
)

@Serializable
data class OrderedItem(
    val productId: String,
    val quantity: Int
)
@Serializable
data class OrderRequest(
    val userId: String,
    val coupon: String?,
    val items: List<OrderedItem>,
)
@Serializable
data class PurchasedResponse(
    var purchaseId: String? = null,
    var discountId: String? = null,
    var returnExpireDate: String? = null
)
@Serializable
data class ProductReturnRequestResponse(
    val message:String
)
@Serializable
data class ProductReturnRequestEntity(
    val purchaseId: String,
    val returnQuantity: String
)
