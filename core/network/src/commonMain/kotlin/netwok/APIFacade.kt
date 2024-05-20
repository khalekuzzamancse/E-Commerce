package netwok

/**
 * - Using the facade design pattern
 */
class APIFacade {
    private val baseUrl = "http://localhost:8080"
    suspend fun fetchProducts() =
        GetRequests().request<List<ProductEntity>>("$baseUrl/api/product")
    suspend fun fetchProduct(id:String):Result<ProductEntity>{
      val res=  GetRequests().request<ProductEntity>("$baseUrl/api/product/$id")
        println(res)
        return res
    }

    /** @param userId as email */
    suspend fun fetchCarts(userId: String) =
        GetRequests().request<List<ProductEntity>>("$baseUrl/api/cart/get/$userId")

    /** @param userId as email */
    suspend fun fetchCoupon(userId: String) =
        GetRequests().request<String>("$baseUrl/api/user/get-coupon/$userId")

    suspend fun addToCart(cartEntity: CartEntity): Result<Unit> {
        return post<Unit>(
            url = "$baseUrl/api/cart/add",
            body = cartEntity
        )
    }

    suspend fun updateCarts(carts: List<CartEntity>): Result<Unit> {
        return update<Unit>(
            url = "$baseUrl/api/cart/update",
            body = carts
        )
    }

    suspend fun orderRequest(orderRequest: OrderRequest): Result<OrderResponse> {
        return post<OrderResponse>(
            url = "$baseUrl/api/cart/order/request",
            body = orderRequest
        )
    }

}