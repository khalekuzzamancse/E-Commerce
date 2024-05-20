package product_catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import database.api.ProductListAPIs
import kotlinx.coroutines.launch
import netwok.APIFacade
import netwok.CartEntity

enum class Route {
    ProductList, ProductDetails, Cart

}

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    Column {
        Row {
            Button(
                onClick = {
                    navController.popBackStack()
                },
            ) {
                Text("Back")
            }
            Button(
                onClick = {
                    navController.navigate(Route.Cart.name)
                },
            ) {
                Text("Cart")
            }
        }
        _NavGraph(navController)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun _NavGraph(
    navController: NavHostController
) {

    val scope= rememberCoroutineScope()
    NavHost(
        navController = navController,
        route = "Main",
        startDestination = Route.ProductList.name
    ) {
        composable(route = Route.ProductList.name) {
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
                onClick = { id ->
                    navController.navigate("ProductDetails/$id")
                }
            )
        }
        composable(
            route = "ProductDetails/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            var product by remember { mutableStateOf<Product?>(null) }
            LaunchedEffect(Unit) {
                if (id != null) {
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
            }
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {},
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    navController.popBackStack()
                                }
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                            }
                        }
                    )
                }
            ) {
                Box(Modifier.padding(it)) {
                    product?.let { item ->
                        ProductListDetailsRoute(item, onAddToCart = {quantity->
                            scope.launch {
                                APIFacade().addToCart(item.id,quantity)
                            }
                        })
                    }
                }
            }

        }
        composable(route = Route.Cart.name) {
            CartRoute()
        }
    }


}