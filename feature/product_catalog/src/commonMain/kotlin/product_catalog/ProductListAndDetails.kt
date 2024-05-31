package product_catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import java.util.UUID

/**
 * Product and product details in the same file because they share common component that can be re-use
 *
 */


/*
TODO: Product Details Section
 */
@Composable
internal fun ProductListRoute(
    products: List<Product>,
    onClick: (id: String) -> Unit
) {


    ProductList(products, onClick = onClick)

}








@Composable
fun ProductList(products: List<Product>, onClick: (id: String) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        contentPadding = PaddingValues(4.dp), //Daraz and amazon uses less gap
        verticalArrangement = Arrangement.spacedBy(8.dp), //Daraz and amazon uses less gap
        horizontalArrangement = Arrangement.spacedBy(8.dp)//Daraz and amazon uses less gap
    ) {
        items(
            items = products, key = {
                it.id
            }) { product ->
            ProductItem(
                product = product,
                modifier = Modifier
            ) {
                onClick(product.id)
            }
        }
    }
}






@Composable
fun ProductItem(
    modifier: Modifier=Modifier,
    product: Product, onClick: () -> Unit
) {
    Surface(
        modifier = modifier.padding(2.dp),
        shape = RoundedCornerShape(4.dp),
        shadowElevation = 1.dp, //Daraz and Amazon app uses less elevation
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)//Daraz use white background,even it work in dark mode
                .clickable { onClick() },
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            _ProductImage(
                modifier = Modifier.size(100.dp),
                urls = product.images
            )
            _ProductTitle(
                modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                title = product.name
            )
            _ProductPrice(
                modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                price = "${product.price} Tk"
            )

        }
    }

}

data class Product(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val images: List<String>,
    val price: Int,
    val description: String,
    val type: String,
    val amountAvailable: Int
)


@Composable
private fun _ProductImage(
    modifier: Modifier = Modifier,
    urls: List<String>
) {

    AsyncImage(
        model = ImageRequest.Builder(LocalPlatformContext.current)
            .data(urls.first())
            // .placeholderMemoryCacheKey(screen.placeholder)
            // .apply { extras.setAll(screen.image.extras) }
            .build(),
        contentDescription = null,
        onState = {
            if (it is AsyncImagePainter.State.Success) {

            }
        },
        modifier = modifier,
    )


}

@Composable
private fun _ProductTitle(
    modifier: Modifier = Modifier,
    title: String
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier
    )
}

@Composable
private fun _ProductPrice(
    modifier: Modifier = Modifier,
    price: String
) {
    Text(
        text = price,
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier
    )
}


/**
 * TODO: Re-usable component
 */