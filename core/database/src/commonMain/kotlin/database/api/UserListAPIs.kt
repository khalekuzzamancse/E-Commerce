package database.api

import CartItemEntity
import CartItemSchema
import UserEntity
import UserSchema
import database.DB
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class UserListAPIs {
    private val realm: Realm = DB.db

    suspend fun addUser(userEntity: UserEntity) {
        try {
            withContext(Dispatchers.IO) {
                realm.write {
                    val user = UserSchema().apply {
                        email = userEntity.email
                        name = userEntity.name
                        hashedPassword = userEntity.hashedPassword
                        cartItems.addAll(userEntity.cartItems.map {
                            CartItemSchema().apply {
                                id = it.id
                                productId = it.productId
                                quantity = it.quantity
                            }
                        })
                    }
                    copyToRealm(user)
                }
            }
        }
        catch (e:Exception){

        }
    }

    suspend fun getUserByEmail(email: String): UserEntity? {
        return withContext(Dispatchers.IO) {
            realm.query<UserSchema>("email == $0", email).first().find()?.toEntity()
        }
    }

    suspend fun getAllUsers(): List<UserEntity> {
        return withContext(Dispatchers.IO) {
            realm.query<UserSchema>().find().map { it.toEntity() }
        }
    }

    suspend fun addItemToUserCart(email: String, cartItemEntity: CartItemEntity) {
        withContext(Dispatchers.IO) {
            realm.write {
                val user = realm.query<UserSchema>("email == $0", email).first().find()
                user?.cartItems?.add(CartItemSchema().apply {
                    id = cartItemEntity.id
                    productId = cartItemEntity.productId
                    quantity = cartItemEntity.quantity
                })
            }
        }
    }

    suspend fun removeItemFromUserCart(email: String, cartItemId: String) {
        withContext(Dispatchers.IO) {
            realm.write {
                val user = realm.query<UserSchema>("email == $0", email).first().find()
                user?.cartItems?.removeIf { it.id == cartItemId }
            }
        }
    }

    suspend fun updateCartItemQuantity(email: String, cartItemId: String, quantity: Int) {
        withContext(Dispatchers.IO) {
            realm.write {
                val cartItem = realm.query<CartItemSchema>("id == $0", cartItemId).first().find()
                if (cartItem != null && cartItem.quantity != quantity) {
                    cartItem.quantity = quantity
                }
            }
        }
    }

    suspend fun updateUserCart(email: String, newCartItems: List<CartItemEntity>) {
        withContext(Dispatchers.IO) {
            realm.write {
                val user = realm.query<UserSchema>("email == $0", email).first().find()
                if (user != null) {
                    // Update existing user's cart items
                    user.cartItems.clear()
                    user.cartItems.addAll(newCartItems.map { item ->
                        CartItemSchema().apply {
                            id = item.id
                            productId = item.productId
                            quantity = item.quantity
                        }
                    })
                } else {
                    // If user does not exist, create a new one

                }
            }
        }
    }




    private fun updateCart(email: String, cartItems: List<CartItemEntity>) {
        val user = realm.query<UserSchema>("email == $0", email).first().find()
        user?.let {
            it.cartItems.clear()
            it.cartItems.addAll(cartItems.map { cartItem ->
                CartItemSchema().apply {
                    id = cartItem.id
                    productId = cartItem.productId
                    quantity = cartItem.quantity
                }
            })
        }
    }




    private fun UserSchema.toEntity(): UserEntity {
        return UserEntity(
            email = this.email,
            name = this.name,
            hashedPassword = this.hashedPassword,
            cartItems = this.cartItems.map { it.toEntity() },
            coupon=this.coupon
        )
    }

    private fun CartItemSchema.toEntity(): CartItemEntity {
        return CartItemEntity(
            id = this.id,
            productId = this.productId,
            quantity = this.quantity
        )
    }
}
