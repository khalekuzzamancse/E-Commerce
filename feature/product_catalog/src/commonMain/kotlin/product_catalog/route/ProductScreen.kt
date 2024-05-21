package product_catalog.route

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import netwok.APIFacade
import product_catalog.Product
import product_catalog.ProductListRoute

@Composable
fun ProductScreen(
    onClick: (String) -> Unit,
) {
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var showP by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        showP=true
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
        showP=false
    }
    Box(Modifier.fillMaxSize()){
        ProductListRoute(
            products = products,
            onClick = onClick
        )
        if (showP){
            CircularProgressIndicator(Modifier.size(64.dp).align(Alignment.Center))
        }
    }


}