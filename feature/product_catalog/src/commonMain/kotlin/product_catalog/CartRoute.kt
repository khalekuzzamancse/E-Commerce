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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun CartRoute(
    onConfirmOrder: (NewOrder) -> Unit={}
) {
    val sampleCartItems = CartItem(
        productId = "1",
        productName = "Sample Product 1",
        productImageUrl = "https://d61s2hjse0ytn.cloudfront.net/color/410/nokia_130_black.webp",
        unitPrice = 29.99,
        quantity = 2
    )
    val controller = CartController(List(size = 10) {
        sampleCartItems.copy(productName = "Name:$it", productId = it.toString())
    })
    Scaffold(
        floatingActionButton = {
            Button(onClick = controller::showConfirmationDialogue) {
                Text("Order Now")
            }
        }
    ) {
        CartList(
            cartItems = controller.items.collectAsState().value,
            onQuantityChange = controller::onQuantityChanged,
            onRemoveRequest = controller::onItemRemoveRequest
        )
        if (controller.showConfirmationDialog.collectAsState().value){
            _OrderConfirmationDialog(
                totalValue = "${controller.totalPrice.collectAsState().value}",
                discountAmount = "${controller.discount.collectAsState().value}",
                message = controller.messageAboutCouponCode.collectAsState().value,
                couponCode = controller.couponCode.collectAsState().value,
                onCouponCodeChanged = controller::onCouponCodeChanged,
                onDismissRequest = {

                },
                onConfirmOrder = {
                    controller.onOrderConfirm()
                    onConfirmOrder(
                        NewOrder(
                            items = controller.items.value,
                            discount = controller.discount.value
                        )
                    )
                }
            )
        }

    }
}

data class NewOrder(
    val items: List<CartItem>,
    val discount:Int
)

@Composable
private fun _OrderConfirmationDialog(
    totalValue: String,
    discountAmount:String,
    message:String?,
    couponCode: String,
    onCouponCodeChanged:(String)->Unit,
    onDismissRequest: () -> Unit,
    onConfirmOrder: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "Confirm Order")
        },
        text = {
            Column {
                Text(text = "Total Price: $totalValue")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Discount:${discountAmount}")
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = couponCode,
                    onValueChange = onCouponCodeChanged,
                    label = { Text("Coupon Code") },
                    modifier = Modifier.fillMaxWidth()
                )
                if (message!=null){
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = message)

                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirmOrder()
                    onDismissRequest()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}
class CartController(
    items: List<CartItem>
) {
    private val _items = MutableStateFlow(items)
    val items = _items.asStateFlow()
    private val _showConfirmationDialog=MutableStateFlow(false)
    val showConfirmationDialog=_showConfirmationDialog.asStateFlow()
    private val _totalPrice=MutableStateFlow(0)
    private val _discount=MutableStateFlow(0)
    private val _messageAboutCouponCode=MutableStateFlow<String?>(null)
    val messageAboutCouponCode=_messageAboutCouponCode.asStateFlow()
    private val _couponCode=MutableStateFlow("")
    val couponCode=_couponCode.asStateFlow()
    val totalPrice=_totalPrice.asStateFlow()
    val discount=_discount.asStateFlow()
    private var isAlreadyGotDiscount=false



    fun onItemRemoveRequest(id: String) {
        _items.update { items ->
            items.filter { it.productId != id }
        }
    }

    fun onQuantityChanged(id: String, isIncreased: Boolean) {
        _items.update { items ->
            items.map { item ->
                val quantity = if (isIncreased) item.quantity + 1 else item.quantity - 1
                if (item.productId == id)
                    item.copy(quantity = quantity)
                else item
            }
        }
    }

    //dialog section
    fun showConfirmationDialogue(){
        _totalPrice.update { items.value.sumOf { it.totalPrice }.toInt() }
        _showConfirmationDialog.update { true }
        _messageAboutCouponCode.update {
            if (totalPrice.value<5000)
                "You have a Coupon code : ${generateCouponCode()} , use it next time for discount"
            else
                null
        }
    }
init {
    CoroutineScope(Dispatchers.Default).launch {
        couponCode.collect{code->
            if (code.length==4&&!isAlreadyGotDiscount){
                _totalPrice.update {
                    it-calculateDiscount()
                }
                _discount.update { calculateDiscount() }
                isAlreadyGotDiscount=true
            }
        }
    }
}
    private fun calculateDiscount()=200
    fun onOrderConfirm(){
        _showConfirmationDialog.update { false }
    }
    private fun generateCouponCode(): String {
        val random = Random(System.currentTimeMillis())
        val couponCode = (1000..9999).random(random)
        return couponCode.toString()
    }
    fun onCouponCodeChanged(code:String){
        _couponCode.update { code }
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
