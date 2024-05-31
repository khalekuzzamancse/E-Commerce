package product_catalog.route

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import netwok.APIFacade
import netwok.OrderedItem
import product_catalog.Product
import product_catalog.ProductListDetailsRoute

@Composable
fun ProductDetailsScreen(
    id: String,
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var product by remember { mutableStateOf<Product?>(null) }
    LaunchedEffect(Unit) {
        APIFacade().fetchProduct(id).getOrNull()?.let {
            product = Product(
                id = it.id,
                name = it.name,
                images = it.images,
                price = it.price,
                description = it.description,
                type = " it.type",
                amountAvailable = it.amountAvailable
            )
        }
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) {
        Box(Modifier.padding(it)) {
            product?.let { item ->
                ProductListDetailsRoute(item,
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
                                    snackbarHostState.showSnackbar("Need to pay:${result.getOrThrow().totalPrice}")
                                    println("ProductListDetailsRoute:$result")
                                    val res = APIFacade().orderConfirm(products)
                                    if (res.isSuccess) {
                                        res.getOrNull()?.let {response->
                                            if (response.isNotEmpty()){
                                                snackbarHostState.showSnackbar("Purchased successfully")
                                                println("ProductListDetailsRoute:$res")
                                            }
                                            else{
                                                snackbarHostState.showSnackbar("Failed to purchase")
                                            }

                                        }

                                    } else if (res.isFailure) {
                                        println("ProductListDetailsRoute:$res")
                                        snackbarHostState.showSnackbar("Failed to purchase")
                                    }


                                } else if (result.isFailure) {
                                    snackbarHostState.showSnackbar("Failed to order")
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