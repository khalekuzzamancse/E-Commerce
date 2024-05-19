package product_catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest

/**
 * Product and product details in the same file because they share common component that can be re-use
 *
 */


/*
TODO: Product Details Section
 */

@Composable
fun ProductListDetailsPreview() {
    val product = Product(
        name = "Nokia 8000",
        images = listOf("https://welectronics.com/images/stories/virtuemart/product/Nokia8000gold6.jpg"),
        price = 8999, // Assuming the price is in BDT
        specifications = """
        - Display: 6.51-inch HD+ display
        - Processor: Unison T606
        - RAM: 3GB
        - Storage: 64GB, expandable via microSD
        - Camera: 13MP + 2MP dual rear cameras, 8MP front camera
        - Battery: 4500mAh
        - OS: Android 11 (Go Edition)
    """.trimIndent(),
        description = "The Nokia 8000 is a sleek and budget-friendly smartphone designed for everyday use. It offers a large HD+ display, a dual-camera setup, and a long-lasting battery, making it an ideal choice for those looking for a reliable device at an affordable price.",
        type = "Electronics",
        amountAvailable = 5
    )

    Column (Modifier.verticalScroll(rememberScrollState())){
        ProductDetails(product)
        ReviewSectionPreview()
    }


}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
private fun ProductDetails(product: Product) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val windowWidth= calculateWindowSizeClass().widthSizeClass
        val weight=0.35f
        when(windowWidth){
            WindowWidthSizeClass.Compact->{
                Column {
                    _ProductImage(
                        modifier = Modifier.sizeIn(maxWidth = 100.dp),
                        urls = product.images
                    )
                    _ProductDetailsSection(
                        name = product.name,
                        price = product.price.toString(),
                        description = product.description
                    )
                }
            }
            else->{
                Row {
                    //Since product so should use hd quality image so that user can ....
                    _ProductImage(
                        modifier = Modifier.weight(weight).sizeIn(
                            maxHeight = 250.dp,
                            maxWidth = 250.dp
                        ),
                        urls = product.images
                    )
                    _ProductDetailsSection(
                        modifier = Modifier.weight(1f-weight),
                        name = product.name,
                        price = product.price.toString(),
                        description = product.description
                    )
                }
            }
        }



    }
}

@Composable
private fun _ProductDetailsSection(
    modifier: Modifier=Modifier,
    name: String,
    price: String,
    description: String,
) {
    Column (modifier){
        _ProductTitle(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp),
            title = name
        )
        _ProductPrice(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp),
            price = "$price Tk"
        )
        _ProductDescription(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp),
            description = description
        )
        _CartControlSection(
            availableItems = 5,
            onAddToCart = {}
        )
    }
}

@Composable
private fun _ProductDescription(
    modifier: Modifier=Modifier,
    description: String
) {
    Text(
        text = description,
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier
    )
}






@Composable
fun ProductList(products: List<Product>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(products) { product ->
            ProductItem(product)
        }
    }
}

@Composable
private fun _CartControlSection(
    availableItems: Int,
    onAddToCart: (Int) -> Unit
) {
    var quantity by remember { mutableStateOf(1) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "Quantity:", fontSize = 18.sp)
            IconButton(
                onClick = {
                    if (quantity > 1) {
                        quantity -= 1
                    }
                },
                enabled = quantity > 1
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease quantity"
                )
            }
            Text(text = "$quantity", fontSize = 18.sp)
            IconButton(
                onClick = {
                    if (quantity < availableItems) {
                        quantity += 1
                    }
                },
                enabled = quantity < availableItems
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase quantity"
                )
            }
        }
        Button(
            onClick = { onAddToCart(quantity) },
            modifier = Modifier.sizeIn(maxWidth = 150.dp)
        ) {
            Text(text = "Add to Cart", fontSize = 18.sp)
        }
    }
}

/*
TODO: Review section
 */
data class Review(
    val userName: String,
    val comment: String,
    val imageLinks: List<String>? = null
)

@Composable
fun ReviewItem(review: Review) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp)
    ) {
        Text(text = review.userName, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = review.comment, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))
        review.imageLinks?.let { images ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                images.forEach { imageUrl ->
                    _ProductImage(
                        modifier = Modifier.size(100.dp),
                        urls = listOf(imageUrl)
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewSection(reviews: List<Review>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      reviews.forEach{ review ->
            ReviewItem(review)
        }
    }
}


@Composable
fun ReviewSectionPreview() {
    val sampleReviews = listOf(
        Review(
            userName = "John Doe",
            comment = "Great product! Highly recommended.",
            imageLinks = listOf("https://via.placeholder.com/150")
        ),
        Review(
            userName = "Jane Smith",
            comment = "Not bad, but could be better.",
            imageLinks = listOf("https://via.placeholder.com/150", "https://via.placeholder.com/150")
        ),
        Review(
            userName = "Alice Johnson",
            comment = "I didn't like it at all.",
            imageLinks = null
        )
    )
    ReviewSection(sampleReviews)

}





@Composable
fun ProductListPreview() {
    val product=Product(
        name = "Nokia 8000",
        images = listOf("https://welectronics.com/images/stories/virtuemart/product/Nokia8000gold6.jpg"),
        price =5205,
        specifications = "Sample specifications",
        description = "Sample description",
        type = "Electronics",
        amountAvailable = 5
    )

    ProductList(List(10){product})

}


@Composable
fun ProductItem(product: Product) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { /* Handle click */ },
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
data class Product(
    val name: String,
    val images: List<String>,
    val price: Int,
    val specifications: String,
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