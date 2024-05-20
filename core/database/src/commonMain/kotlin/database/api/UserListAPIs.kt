package database.api

import CartItemEntity
import CartItemSchema
import UserEntity
import UserSchema
import database.DB
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.toRealmList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class UserListAPIs {
    private val realm: Realm = DB.db

    suspend fun addUser(userEntity: UserEntity) {
        try {
            withContext(Dispatchers.IO) {
                realm.writeBlocking  {
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
    suspend fun addToCart(email: String):UserEntity?{
      return realm.query<UserSchema>("email == $0", email).first().find()?.also { userSchema ->
          realm.writeBlocking {
             findLatest(userSchema)?.let {userSchema ->
                 val items: List<CartItemSchema> =userSchema.cartItems+CartItemSchema().apply {
                     this.quantity=5
                     this.productId="updated"
                 }
                 userSchema.cartItems=items.toRealmList()
             }
          }
      }?.toEntity()
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
