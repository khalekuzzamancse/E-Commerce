package product_catalog.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import netwok.APIFacade
import product_catalog.Product
import product_catalog.ProductListRoute

@Composable
fun ProductScreen(
    onClick: (String) -> Unit,
) {
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    LaunchedEffect(Unit) {
        products = APIFacade().fetchProducts().getOrDefault(emptyList()).map {
            Product(
                id = it.id,
                name = it.name,
                images = it.images,
                price = it.price,
                description = it.description,
                type = it.type,
                amountAvailable = it.amountAvailable
            )
        }
    }
    ProductListRoute(
        products = products,
        onClick = onClick
    )

}