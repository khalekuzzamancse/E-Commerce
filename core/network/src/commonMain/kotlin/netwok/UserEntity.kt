package netwok

import kotlinx.serialization.Serializable

/**
 * @param email used as id
 */
@Serializable
data class UserEntity(
    val name: String,
    val email: String,
    val password: String,
    val coupon: String? = null,
)