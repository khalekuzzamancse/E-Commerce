package network

import kotlinx.coroutines.runBlocking
import netwok.APIs
import netwok.CartEntity
import kotlin.test.Test

class APIsTest {
    @Test
    fun testProducts() {
        runBlocking {
            val result = APIs().fetchProducts()
            println(result)
        }
    }

    @Test
    fun testCarts() {
        runBlocking {
            val result = APIs().fetchCarts("admin")
            println(result)
        }
    }

    @Test
    fun testCoupon() {
        runBlocking {
            val result = APIs().fetchCoupon("admin")
            println(result.getOrNull())
        }
    }

    @Test
    fun addToCart() {
        runBlocking {
            val result = APIs().addToCart(
                CartEntity(
                    userId = "admin",
                    productId = "prod456",
                    quantity = 3
                )
            )
            println(result.getOrNull())
        }
    }

    @Test
    fun updateCart() {
        runBlocking {
            val result = APIs().updateCarts(
                listOf(
                    CartEntity(
                        userId = "admin",
                        productId = "b54ace9c-492e-4071-9a9b-2564ae033ccf",
                        quantity = 333
                    ),
                    CartEntity(
                        userId = "admin",
                        productId = "p2",
                        quantity = 5
                    )
                )
            )
            println(result.getOrNull())
        }
    }
}