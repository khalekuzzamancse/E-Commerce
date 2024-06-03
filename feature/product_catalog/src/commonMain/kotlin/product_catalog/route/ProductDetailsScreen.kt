package product_catalog.route

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import common.ui.CustomSnackBar
import common.ui.SnackBarMessage
import common.ui.SnackBarMessageType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import netwok.APIFacade
import netwok.OrderedItem
import netwok.ProductDetailsEntity
import product_catalog.DiscountByPrice
import product_catalog.ProductDetails
import product_catalog.ProductDetailsRoute2
import product_catalog.ProductReview

@Composable
fun ProductDetailsScreen(
    id: String,
) {
    val scope = rememberCoroutineScope()
    var selectedProduct by remember { mutableStateOf<ProductDetails?>(null) }
    var snackBarMessage by remember { mutableStateOf<SnackBarMessage?>(null) }

    LaunchedEffect(Unit) {
        APIFacade().fetchProductDetails(id).getOrNull()?.let {
            selectedProduct = convertEntityToModel(it)
        }

    }
    LaunchedEffect(snackBarMessage){
        if (snackBarMessage!=null){
            delay(3000)
            snackBarMessage=null
        }
    }
    Scaffold(
        snackbarHost = {
            snackBarMessage?.let {
                CustomSnackBar(it){
                    snackBarMessage=null
                }
            }
        },
    ) {
        Box(Modifier.padding(it)) {
            selectedProduct?.let { item ->

                ProductDetailsRoute2(item,
                    //TODO:for temporary we are directly order from details,without adding to cart.refactor it later
                    //TODO: onAddToCart is now used for order
                    onAddToCart = { quantity ->
                        scope.launch {
                            val products = listOf(OrderedItem(item.id, quantity))
                            try {

                                //APIFacade().addToCart(item.id, quantity)
                                val result = APIFacade().orderRequest(products)
                                // snack barHostState.showSnack bar("Added to cart")
                                if (result.isSuccess) {
                                    snackBarMessage = SnackBarMessage(
                                        message = "Purchased successfully",
                                        type = SnackBarMessageType.Success,
                                        details ="Need to pay:${result.getOrThrow().total}"
                                    )
                                    println("ProductListDetailsRoute:$result")
                                    val res = APIFacade().orderConfirm(products)
                                    if (res.isSuccess) {
                                        res.getOrNull()?.let { response ->
                                            snackBarMessage = if (response.isNotEmpty()) {
                                                SnackBarMessage(
                                                    message = "Purchased successfully",
                                                    type = SnackBarMessageType.Success,
                                                    details = result.exceptionOrNull()?.message
                                                )

                                            } else {
                                                SnackBarMessage(
                                                    message = "Failed to purchase",
                                                    type = SnackBarMessageType.Error,
                                                    details = result.exceptionOrNull()?.message
                                                )
                                            }

                                        }

                                    } else if (res.isFailure) {
                                        snackBarMessage = SnackBarMessage(
                                            message = "Failed to purchase",
                                            type = SnackBarMessageType.Error,
                                            details = result.exceptionOrNull()?.message
                                        )
                                    }


                                } else if (result.isFailure) {
                                    snackBarMessage = SnackBarMessage(
                                        message = "Failed to order",
                                        type = SnackBarMessageType.Error,
                                        details = result.exceptionOrNull()?.message
                                    )
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
    val discountByPrice = detailsEntity.discountByPrice
    return ProductDetails(
        id = detailsEntity.productId,
        name = detailsEntity.name,
        imagesLink = detailsEntity.imagesLink,
        description = detailsEntity.description,
        price = detailsEntity.price,
        discountByPrice = if (discountByPrice != null) DiscountByPrice(
            discountByPrice.amount,
            discountByPrice.expirationTimeInMs
        ) else null,
        offeredProduct = detailsEntity.discountByProduct?.let { offerEntity ->
            product_catalog.ProductOffer(
                productName = offerEntity.productName,
                imageLink = offerEntity.imageLink,
                requiredQuantity = offerEntity.requiredQuantity,
                freeQuantity = offerEntity.freeQuantity,
                expirationTimeInMs = offerEntity.expirationTimeInMs
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