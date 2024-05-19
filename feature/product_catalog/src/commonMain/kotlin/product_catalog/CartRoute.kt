package product_catalog


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Composable
fun CartListPreview() {
    val sampleCartItems = CartItem(
        productId = "1",
        productName = "Sample Product 1",
        productImageUrl = "https://via.placeholder.com/150",
        unitPrice = 29.99,
        quantity = 2
    )
    val controller = CartController(List(size = 10) {
        sampleCartItems.copy(productName = "Name:$it", productId = it.toString())
    })
    Scaffold(
        floatingActionButton = {
            Button(onClick = {}) {
                Text("Order Now")
            }
        }
    ) {
        CartList(
            cartItems = controller.items.collectAsState().value,
            onQuantityChange = controller::onQuantityChanged,
            onRemoveRequest = controller::onItemRemoveRequest
        )
    }
}

class CartController(
    items: List<CartItem>
) {
    private val _items = MutableStateFlow(items)
    val items = _items.asStateFlow()
    fun onItemRemoveRequest(id: String) {
        _items.update { items ->
            items.filter { it.productId != id }
        }
    }

    fun onQuantityChanged(id: String, isIncresed: Boolean) {
        _items.update { items ->
            items.map { item ->
                val quantity = if (isIncresed) item.quantity + 1 else item.quantity - 1
                if (item.productId == id)
                    item.copy(quantity = quantity)
                else item
            }
        }
    }


}

@Composable
private fun CartList(
    cartItems: List<CartItem>,
    onQuantityChange: (id: String, increased: Boolean) -> Unit,
    onRemoveRequest: (id: String) -> Unit,
) {

    LazyVerticalGrid(
        columns = GridCells.Adaptive(300.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(cartItems) { cartItem ->
            CartItemView(
                cartItem = cartItem,
                onQuantityChange = onQuantityChange,
                onRemoveRequest = { onRemoveRequest(cartItem.productId) }
            )
        }
    }

}

@Composable
fun CartItemView(
    cartItem: CartItem,
    onQuantityChange: (String, increased: Boolean) -> Unit,
    onRemoveRequest: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            _ProductImage(
                modifier = Modifier.size(80.dp),
                url = cartItem.productImageUrl
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(Modifier) {
                Text(
                    text = cartItem.productName,
                    style = MaterialTheme.typography.titleMedium,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Price: \$${cartItem.unitPrice}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Total: \$${cartItem.totalPrice}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            _CartControlSection(
                availableItems = 5,
                addedItemAmount = cartItem.quantity,
                onItemAmountChanged = { newQuantity ->
                    onQuantityChange(cartItem.productId, newQuantity)
                }
            )
            IconButton(
                onClick = onRemoveRequest
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Remove from cart")
            }

        }
    }

}

data class CartItem(
    val productId: String,
    val productName: String,
    val productImageUrl: String,
    val unitPrice: Double,
    var quantity: Int
) {
    val totalPrice: Double
        get() = unitPrice * quantity
}

@Composable
private fun _ProductImage(
    modifier: Modifier = Modifier,
    url: String
) {

    AsyncImage(
        model = ImageRequest.Builder(LocalPlatformContext.current)
            .data(url)
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
private fun _CartControlSection(
    modifier: Modifier = Modifier,
    availableItems: Int,
    addedItemAmount: Int,
    onItemAmountChanged: (increased: Boolean) -> Unit
) {
//    var quantity by remember { mutableStateOf(1) }
    val quantity = addedItemAmount

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = {
                    onItemAmountChanged(false)
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
                        onItemAmountChanged(true)
                },
                enabled = quantity < availableItems
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase quantity"
                )
            }
        }
    }
}
