package netwok

/**
 * - Using the facade design pattern
 */
class APIFacade {
    private val baseUrl = "http://localhost:8080"
    private val userId = "admin"
    suspend fun fetchProducts(): Result<List<ProductEntity>>{
       val res= GetRequests().request<List<ProductEntity>>("$baseUrl/api/product")
      //  println(res)
        return res
    }

    suspend fun fetchProduct(id: String): Result<ProductEntity> {
      //  val res = GetRequests().request<ProductEntity>("$baseUrl/api/product/$id")
        val res = GetRequests().request<ProductEntity>("$baseUrl/api/product/$id")
     //   println(res)
        return res
    }
    suspend fun fetchProductDetails(id: String): Result<ProductDetailsEntity> {
        //  val res = GetRequests().request<ProductEntity>("$baseUrl/api/product/$id")
        val res = GetRequests().request<ProductDetailsEntity>("$baseUrl/api/product/details/$id")
        //   println(res)
        return res
    }
    suspend fun fetchProductOffer(id: String): Result<ProductOfferEntity> {
        //  val res = GetRequests().request<ProductEntity>("$baseUrl/api/product/$id")
        val res = GetRequests().request<ProductOfferEntity>("$baseUrl/product/offer/$id")
        //   println(res)
        return res
    }
    /** @param userId as email */
    suspend fun fetchCarts(): Result<List<CartItem>> {
        val res = GetRequests().request<List<CartItem>>("$baseUrl/api/cart/get/$userId")
       // println(res)
        return res
    }
    suspend fun fetchPurchasedProducts():Result<List<PurchasedProductEntity>>{
        val res=GetRequests().request<List<PurchasedProductEntity>>("$baseUrl/api/purchase/$userId")
        return res
    }

    /** @param userId as email */
    suspend fun fetchCoupon() =
        GetRequests().request<String>("$baseUrl/api/user/get-coupon/$userId")

    suspend fun addToCart(productId: String, quantity: Int): Result<Unit> {
        return post<Unit>(
            url = "$baseUrl/api/cart/add",
            body = CartEntity(userId, productId, quantity)
        )
    }
    suspend fun requestForReturn(purchaseId: String, returnQuantity: Int): Result<ProductReturnRequestResponse> {
        return post<ProductReturnRequestResponse>(
            url = "$baseUrl/api/product/return/request",
            body = ProductReturnRequestEntity(purchaseId, returnQuantity.toString())
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

    suspend fun orderRequest(items: List<OrderedItem>, coupon: String?=null): Result<OrderResponse> {
        return post<OrderResponse>(
            url = "$baseUrl/api/purchase/request",
            body = OrderRequest(userId = userId, coupon = coupon, items = items)
        )
    }
    suspend fun orderConfirm(items: List<OrderedItem>, coupon: String?=null): Result<List<PurchasedResponse>> {
        return post<List<PurchasedResponse>>(
            url = "$baseUrl/api/purchase/confirm",
            body = OrderRequest(userId = userId, coupon = coupon, items = items)
        )
    }


}