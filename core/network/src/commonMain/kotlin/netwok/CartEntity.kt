package netwok

import kotlinx.serialization.Serializable

/**
 * - A cart be unique identified by userId+ProductId
 */
@Serializable
data class CartEntity(
    val userId: String,
    val productId: String,
    val quantity: Int
){
    val id=userId+productId
}