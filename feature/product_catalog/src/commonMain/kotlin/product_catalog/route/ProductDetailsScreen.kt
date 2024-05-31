package product_catalog.route

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import netwok.APIFacade
import netwok.OrderedItem
import netwok.ProductDetailsEntity
import product_catalog.ProductDetails
import product_catalog.ProductDetailsRoute2
import product_catalog.ProductReview

data class ProductOffer(
    val name:String,
    val requiredQuantity:Int,
    val freeQuantity:Int,
)
@Composable
fun ProductDetailsScreen(
    id: String,
) {
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    var selectedProduct by remember { mutableStateOf<ProductDetails?>(null) }
    LaunchedEffect(Unit) {
        APIFacade().fetchProductDetails(id).getOrNull()?.let {
            selectedProduct = convertEntityToModel(it)
        }

    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
    ) {
        Box(Modifier.padding(it)) {
            selectedProduct?.let { item ->

                ProductDetailsRoute2(item,
                    //TODO:for temporary we are directly order from details,without adding to cart.refactor it later
                    //TODO: onAddToCart is now used for order
                    onAddToCart = { quantity ->
                        scope.launch {
                            val products = listOf(OrderedItem(item.id.toString(), quantity))
                            try {

                                //APIFacade().addToCart(item.id, quantity)
                                val result = APIFacade().orderRequest(products)
                                // snack barHostState.showSnack bar("Added to cart")
                                if (result.isSuccess) {
                                    snackBarHostState.showSnackbar("Need to pay:${result.getOrThrow().totalPrice}")
                                    println("ProductListDetailsRoute:$result")
                                    val res = APIFacade().orderConfirm(products)
                                    if (res.isSuccess) {
                                        res.getOrNull()?.let {response->
                                            if (response.isNotEmpty()){
                                                snackBarHostState.showSnackbar("Purchased successfully")
                                                println("ProductListDetailsRoute:$res")
                                            }
                                            else{
                                                snackBarHostState.showSnackbar("Failed to purchase")
                                            }

                                        }

                                    } else if (res.isFailure) {
                                        println("ProductListDetailsRoute:$res")
                                        snackBarHostState.showSnackbar("Failed to purchase")
                                    }


                                } else if (result.isFailure) {
                                    snackBarHostState.showSnackbar("Failed to order")
                                    println("ProductListDetailsRoute:$result")
                                }
                            } catch (_: Exception) {
                            }

                        }
                    })
            }
        }
    }


}

// Converter function from Entity to Model
private fun convertEntityToModel(detailsEntity: ProductDetailsEntity): ProductDetails {
    return ProductDetails(
        id = detailsEntity.productId,
        name = detailsEntity.name,
        imagesLink = detailsEntity.imagesLink,
        description = detailsEntity.description,
        originalPrice = detailsEntity.originalPrice,
        discount = detailsEntity.priceDiscount,
        priceOnDiscount = detailsEntity.priceOnDiscount,
        offeredProduct = detailsEntity.offeredProduct?.let { offerEntity ->
            product_catalog.ProductOffer(
                productName = offerEntity.productName,
                imageLink = offerEntity.imageLink,
                requiredQuantity = offerEntity.requiredQuantity,
                freeQuantity = offerEntity.freeQuantity
            )
        },
        reviews = detailsEntity.reviews.map { reviewEntity ->
            ProductReview(
                reviewerName = reviewEntity.reviewerName,
                comment = reviewEntity.comment,
                imagesLink = reviewEntity.imagesLink
            )
        }
    )
}