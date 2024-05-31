package product_catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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


@Composable
fun ProductDetailsRoute2(
    product: ProductDetails,
    onAddToCart: (Int) -> Unit,
) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        ProductDetails2(product, onAddToCart)
        product.reviews?.let {
            ReviewSectionPreview(product.reviews)
        }

    }


}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
private fun ProductDetails2(
    product: ProductDetails,
    onAddToCart: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val windowWidth = calculateWindowSizeClass().widthSizeClass
        val weight = 0.35f
        when (windowWidth) {
            WindowWidthSizeClass.Compact -> {
                Column {
                    _ProductImage(
                        modifier = Modifier.sizeIn(maxWidth = 100.dp),
                        urls = product.imagesLink
                    )
                    _ProductDetailsSection(
                        name = product.name,
                        originalPrice = product.originalPrice,
                        discount = product.discount,
                        priceOnDiscount = product.priceOnDiscount,
                        description = product.description,
                        onAddToCart = onAddToCart
                    )
                }
            }

            else -> {
                Row {
                    //Since product so should use hd quality image so that user can ....
                    _ProductImage(
                        modifier = Modifier.weight(weight).sizeIn(
                            maxHeight = 250.dp,
                            maxWidth = 250.dp
                        ),
                        urls = product.imagesLink
                    )
                    _ProductDetailsSection(
                        modifier = Modifier.weight(1f - weight),
                        name = product.name,
                        originalPrice = product.originalPrice,
                        discount = product.discount,
                        priceOnDiscount = product.priceOnDiscount,
                        description = product.description,
                        onAddToCart = onAddToCart
                    )
                }
            }
        }


    }
}

data class ProductDetails(
    val id: String,
    val name: String,
    val imagesLink: List<String>,
    val description: String,
    val originalPrice: String,
    val discount: String,
    val priceOnDiscount: String,
    val offeredProduct: ProductOffer?,
    val reviews: List<ProductReview>?
)

data class ProductReview(
    val reviewerName: String,
    val comment: String,
    val imagesLink: List<String>
)

data class ProductOffer(
    val productName: String,
    val imageLink: String,
    val requiredQuantity: String,
    val freeQuantity: String
)


@Composable
private fun _ProductDetailsSection(
    modifier: Modifier = Modifier,
    name: String,
    originalPrice: String,
    discount: String,
    priceOnDiscount: String,
    description: String,
    onAddToCart: (Int) -> Unit,
) {
    Column(modifier) {
        _ProductTitle(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp),
            title = name
        )
        _ProductPrice(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp),
            label = "Original Price",
            price = "$originalPrice Tk"
        )
        _ProductPrice(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp),
            label = "Discount",
            price = "$discount Tk"
        )

        _ProductPrice(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp),
            label = "Price onDiscount",
            price = "$priceOnDiscount Tk"
        )
        _ProductDescription(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp),
            description = description
        )
        _CartControlSection(
            availableItems = 5,
            onAddToCart = onAddToCart
        )
    }
}

@Composable
private fun _ProductDescription(
    modifier: Modifier = Modifier,
    description: String
) {
    Text(
        text = description,
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier
    )
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
                    contentDescription = "Decrease quantity",
                    tint = MaterialTheme.colorScheme.secondary
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
                    contentDescription = "Increase quantity",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
        Button(
            onClick = { onAddToCart(quantity) },
            modifier = Modifier.sizeIn(maxWidth = 150.dp),
            colors = ButtonDefaults.buttonColors()
                .copy(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
//            Text(text = "Add to Cart", fontSize = 18.sp)
            //TODO:for temporary we are directly order from details,without adding to cart.refactor it later
            Text(text = "Order now", fontSize = 18.sp)

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
        reviews.forEach { review ->
            ReviewItem(review)
        }
    }
}


@Composable
fun ReviewSectionPreview(reviews: List<ProductReview>) {
    ReviewSection(reviews.map {
        Review(
            it.reviewerName,it.comment,it.imagesLink
        )
    })

}


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
    label: String,
    price: String
) {
    Row {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            modifier = modifier
        )
        Text(": ")
        Text(
            text = price,
            style = MaterialTheme.typography.titleMedium,
            modifier = modifier
        )
    }

}


/**
 * TODO: Re-usable component
 */