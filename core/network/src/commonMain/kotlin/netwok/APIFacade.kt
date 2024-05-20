package netwok

/**
 * - Using the facade design pattern
 */
class APIFacade {
    private val baseUrl = "http://localhost:8080"
    private val userId="admin"
    suspend fun fetchProducts() =
        GetRequests().request<List<ProductEntity>>("$baseUrl/api/product")
    suspend fun fetchProduct(id:String):Result<ProductEntity>{
      val res=  GetRequests().request<ProductEntity>("$baseUrl/api/product/$id")
        println(res)
        return res
    }

    /** @param userId as email */
    suspend fun fetchCarts():Result<List<CartItem>>{
        val res=GetRequests().request<List<CartItem>>("$baseUrl/api/cart/get/$userId")
        println(res)
        return res
    }

    /** @param userId as email */
    suspend fun fetchCoupon() =
        GetRequests().request<String>("$baseUrl/api/user/get-coupon/$userId")

    suspend fun addToCart(productId:String,quantity:Int): Result<Unit> {
        return post<Unit>(
            url = "$baseUrl/api/cart/add",
            body = CartEntity(userId, productId, quantity)
        )
    }
    suspend fun clearCart(): Result<Unit> {
        return delete<Unit>(
            url = "$baseUrl/api/cart/delete/$userId",
        )
    }
    suspend fun updateCarts(carts: List<CartEntity>): Result<Unit> {
        return update<Unit>(
            url = "$baseUrl/api/cart/update",
            body = carts
        )
    }

    suspend fun orderRequest(items:List<OrderedItem>, coupon:String?): Result<OrderResponse> {
        return post<OrderResponse>(
            url = "$baseUrl/api/cart/order/request",
            body = OrderRequest(userId = userId,coupon=coupon, items = items)
        )
    }

}