package product_catalog.route

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
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
import product_catalog.Product
import product_catalog.ProductListDetailsRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    id: String,
) {
    val scope = rememberCoroutineScope()
    var product by remember { mutableStateOf<Product?>(null) }
    LaunchedEffect(Unit) {
        APIFacade().fetchProduct(id).getOrNull()?.let {
            product = Product(
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
    product?.let { item ->
        ProductListDetailsRoute(item, onAddToCart = { quantity ->
            scope.launch {
                APIFacade().addToCart(item.id, quantity)
            }
        })
    }


}