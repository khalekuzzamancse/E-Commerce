package network

import kotlinx.coroutines.runBlocking
import netwok.APIFacade
import netwok.CartEntity
import netwok.OrderRequest
import netwok.OrderedItem
import kotlin.test.Test

class APIFacadeTest {
    @Test
    fun testProducts() {
        runBlocking {
            val result = APIFacade().fetchProducts()
            println(result)
        }
    }

    @Test
    fun testCarts() {
        runBlocking {
            val result = APIFacade().fetchCarts("admin")
            println(result)
        }
    }

    @Test
    fun testCoupon() {
        runBlocking {
            val result = APIFacade().fetchCoupon("admin")
            println(result.getOrNull())
        }
    }

    @Test
    fun addToCart() {
        runBlocking {
            val result = APIFacade().addToCart(
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
            val result = APIFacade().updateCarts(
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
    @Test
    fun orderRequestTest() {

        runBlocking {
            val orderedItems = listOf(
                OrderedItem(productId = "1", quantity = 2),
                OrderedItem(productId = "2", quantity = 1),
                OrderedItem(productId = "3", quantity = 3)
            )

            // Creating an OrderRequest instance using the list of ordered items
            val orderRequest = OrderRequest(
                userId = "admin",
                coupon = "6272",  // This can be null if no coupon is applied
                items = orderedItems
            )
            val result = APIFacade().orderRequest(orderRequest)
            println(result.getOrNull())
        }
    }
}