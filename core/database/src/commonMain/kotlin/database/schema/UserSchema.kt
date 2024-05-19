import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.util.UUID

// Defines a cart item in the user's shopping cart
class CartItemSchema : RealmObject {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()  // Unique identifier for each cart item
    var productId: String = ""  // Reference to the product
    var quantity: Int = 0  // Quantity of the product

    override fun toString(): String {
        return "CartItemSchema(id='$id', productId='$productId', quantity=$quantity)"
    }

    fun toEntity(): CartItemEntity {
        return CartItemEntity(id, productId, quantity)
    }
}

// User schema including cart items and an optional coupon code
class UserSchema : RealmObject {
    @PrimaryKey
    var email: String = ""  // Email as primary key
    var name: String = ""
    var hashedPassword: String = ""
    var cartItems: RealmList<CartItemSchema> = realmListOf()  // List of cart items
    var coupon: String? = null  // Optional coupon code

    fun toEntity(): UserEntity {
        return UserEntity(
            email = email,
            name = name,
            hashedPassword = hashedPassword,
            cartItems = cartItems.map { it.toEntity() },
            coupon = coupon
        )
    }

    override fun toString(): String {
        return "UserSchema(email='$email', name='$name', cartItems=${cartItems.size}, coupon=$coupon)"
    }
}

// Data transfer object for CartItemSchema
data class CartItemEntity(
    val id: String,
    val productId: String,
    val quantity: Int
)

// Data transfer object for UserSchema
data class UserEntity(
    val email: String,
    val name: String,
    val hashedPassword: String,
    val cartItems: List<CartItemEntity>,
    val coupon: String?=null
)
