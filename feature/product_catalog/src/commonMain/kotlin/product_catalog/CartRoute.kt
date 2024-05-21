package product_catalog


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import netwok.APIFacade
import netwok.OrderedItem

@Composable
internal fun CartRoute(
    onConfirmOrder: (NewOrder) -> Unit = {}
) {
    var controller by remember { mutableStateOf(CartController(emptyList())) }

    var showP by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        showP = true
        val items = APIFacade().fetchCarts().getOrDefault(emptyList()).map { cartItem ->
            val product = cartItem.product
            CartItem(
                productId = product.id,
                productName = product.name,
                productImageUrl = product.images.first(),
                unitPrice = product.price,
                quantity = cartItem.quantity
            )
        }
        controller = CartController(items)
        showP = false

    }

    Box(Modifier.fillMaxSize()){

        Scaffold(
            floatingActionButton = {
                Button(
                    onClick = controller::showConfirmationDialogue,
                    enabled = controller.items.collectAsState().value.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors()
                        .copy(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Order Now")
                }
            },

            floatingActionButtonPosition = FabPosition.EndOverlay,

            ) {
            CartList(
                cartItems = controller.items.collectAsState().value,
                onQuantityChange = controller::onQuantityChanged,
                onRemoveRequest = controller::onItemRemoveRequest
            )
            if (controller.showConfirmationDialog.collectAsState().value) {
                _OrderConfirmationDialog(
                    enabledConfirm = controller.enabledConfirm.collectAsState().value,
                    totalValue = "${controller.totalPrice.collectAsState().value}",
                    discountAmount = "${controller.discount.collectAsState().value}",
                    couponCode = controller.couponCode.collectAsState().value,
                    onCouponCodeChanged = controller::onCouponCodeChanged,
                    onCancel = {
                        controller.onDismissDialog()
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
                ) {
                    val errorMessage = controller.errorMessage.collectAsState().value
                    if (errorMessage != null) {
                        Snackbar {
                            Text(errorMessage)
                        }
                    }

                }
            }

        }
        if (showP){
            CircularProgressIndicator(Modifier.size(64.dp).align(Alignment.Center))
        }
    }

}

data class NewOrder(
    val items: List<CartItem>,
    val discount: Int
)

@Composable
private fun _OrderConfirmationDialog(
    enabledConfirm: Boolean,
    totalValue: String,
    discountAmount: String,
    couponCode: String,
    onCouponCodeChanged: (String) -> Unit,
    onCancel: () -> Unit,
    onConfirmOrder: () -> Unit,
    snackBar: @Composable () -> Unit = {},

    ) {

    AlertDialog(
        onDismissRequest = onCancel,
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
                snackBar()

            }


        },
        confirmButton = {
            Button(
                enabled = enabledConfirm,
                onClick = {
                    onConfirmOrder()

                },
                colors = ButtonDefaults.buttonColors()
                    .copy(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors()
                    .copy(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Cancel")
            }
        }
    )
}

class CartController(
    items: List<CartItem>,
) {
    private val _items = MutableStateFlow(items)
    val items = _items.asStateFlow()
    private val _showConfirmationDialog = MutableStateFlow(false)
    val showConfirmationDialog = _showConfirmationDialog.asStateFlow()
    private val _totalPrice = MutableStateFlow(0)
    private val _discount = MutableStateFlow(0)
    private val _couponCode = MutableStateFlow("")
    val couponCode = _couponCode.asStateFlow()
    val totalPrice = _totalPrice.asStateFlow()
    val discount = _discount.asStateFlow()
    private var previousCoupon: String? = null
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()
    private val _enableConfirm = MutableStateFlow(true)
    val enabledConfirm = _enableConfirm.asStateFlow()

    //Has Bug,so use the index right now to delete
//    fun onItemRemoveRequest(id: String) {
//        _items.update { items ->
//            items.filter { it.productId != id }
//        }
//    }
    fun onItemRemoveRequest(i: Int) {
        try {

            _items.update { items ->
                items.filterIndexed { index, _ -> index != i }
            }
        } catch (e: Exception) {

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
    fun showConfirmationDialogue() {
        //When no item in the cart,so do not show the order dialog
        if (items.value.isEmpty())
            return
        CoroutineScope(Dispatchers.Default).launch {
            previousCoupon = APIFacade().fetchCoupon().getOrNull()
            _totalPrice.update { items.value.sumOf { it.totalPrice }.toInt() }
            _showConfirmationDialog.update { true }
            if (!previousCoupon.isNullOrEmpty())
                updateErrorMessage("You have a previous coupon:$previousCoupon")
        }

    }

    fun onOrderConfirm() {
        CoroutineScope(Dispatchers.Default).launch {
            _enableConfirm.update { false }
            val isCouponValid =
                (previousCoupon.isNullOrEmpty() && previousCoupon == couponCode.value)
            if (isCouponValid) {
                updateErrorMessage("Invalid Coupon code")
                delay(3_000)
            }


            val response = APIFacade().orderRequest(
                coupon = couponCode.value,
                items = items.value.map {
                    OrderedItem(it.productId, it.quantity)
                }
            ).getOrNull()
            if (response != null) {
                _totalPrice.update { response.totalPrice - response.discount }
                _discount.update { response.discount }
                if (discount.value <= 0)
                    updateErrorMessage("You have No discount")
                else
                    updateErrorMessage("You have got discount:${_discount.value}")
                delay(2000)
                _showConfirmationDialog.update { true }
                if (response.coupon != null)
                    updateErrorMessage(
                        "Congratulations,You have new Coupon:${response.coupon},Use it next time.",
                        5000
                    )

            }
            delay(7_000)//after 3 sec hide dialogue,to avoid multiple click
            _showConfirmationDialog.update { false }
            //remove the item from cart
            val isRemoved = APIFacade().clearCart()
            println("IsItemRemovecart:$isRemoved")
            //fetch the new cart
            //but right now let remove the local cart
            _items.update { emptyList() }

        }


    }

    private fun updateErrorMessage(msg: String?, duration: Long = 3000) {
        CoroutineScope(Dispatchers.Default).launch {
            _errorMessage.update { msg }
            delay(duration)
            _errorMessage.update { null }
        }


    }


    fun onCouponCodeChanged(code: String) {
        _couponCode.update { code }
    }

    fun onDismissDialog() {
        _showConfirmationDialog.update { false }
        updateErrorMessage(null)

    }


}

@Composable
private fun CartList(
    cartItems: List<CartItem>,
    onQuantityChange: (id: String, increased: Boolean) -> Unit,
//    onRemoveRequest: (id: String) -> Unit,
    onRemoveRequest: (Int) -> Unit,
) {

    LazyVerticalGrid(
        columns = GridCells.Adaptive(300.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(cartItems) { index, cartItem ->
            CartItemView(
                cartItem = cartItem,
                onQuantityChange = onQuantityChange,
                onRemoveRequest = { onRemoveRequest(index) }
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
    Surface(
        modifier = Modifier.padding(2.dp),
        shape = RoundedCornerShape(4.dp),
        shadowElevation = 1.dp, //Daraz and Amazon app uses less elevation
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
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
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove from cart",
                        tint = MaterialTheme.colorScheme.error
                    )
                }

            }
        }
    }
}

data class CartItem(
    val productId: String,
    val productName: String,
    val productImageUrl: String,
    val unitPrice: Int,
    var quantity: Int
) {
    val totalPrice: Int
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
                    contentDescription = "Decrease quantity",
                    tint = MaterialTheme.colorScheme.secondary
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
                    contentDescription = "Increase quantity",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
