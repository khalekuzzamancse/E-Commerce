package product_return


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
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
import java.time.LocalDate

@Composable
internal fun ProductReturnRoute(
) {
    var controller by remember { mutableStateOf(CartController(emptyList())) }

    var showP by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        showP = true
        val items = APIFacade().fetchPurchasedProducts().getOrDefault(emptyList()).map { entity ->
            PurchasedProduct(
                purchaseId = entity.purchaseId,
                productName = entity.productName,
                productImageUrl = entity.productImageUrl,
                quantity = entity.quantity,
                returnExpireDate = entity.returnExpireDate
            )
        }
        // val items = getDummyPurchaseProduct()
        controller = CartController(items)
        showP = false

    }

    Box(Modifier.fillMaxSize()) {

        Scaffold(
            floatingActionButton = {
//                Button(
//                    onClick = controller::showConfirmationDialogue,
//                    enabled = controller.items.collectAsState().value.isNotEmpty(),
//                    colors = ButtonDefaults.buttonColors()
//                        .copy(containerColor = MaterialTheme.colorScheme.secondary)
//                ) {
//                    Text("Order Now")
//                }
            },

            floatingActionButtonPosition = FabPosition.EndOverlay,

            ) {
            CartList(
                cartItems = controller.items.collectAsState().value,
                onReturnRequest = controller::onReturnRequest
            )
            if (controller.showConfirmationDialog.collectAsState().value) {
                _ReturnConfirmationDialog(
                    enabledConfirm = controller.enabledConfirm.collectAsState().value,
                    returnAmount = controller.returnAmount.collectAsState().value,
                    onReturnAmountChanged = controller::onReturnAmountChanged,
                    onCancel = {
                        controller.onDismissDialog()
                    },
                    onConfirm = {
                        controller.onConfirm()
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
        if (showP) {
            CircularProgressIndicator(Modifier.size(64.dp).align(Alignment.Center))
        }
    }

}

@Composable
private fun _ReturnConfirmationDialog(
    enabledConfirm: Boolean,
    returnAmount: String,
    onReturnAmountChanged: (String) -> Unit,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    snackBar: @Composable () -> Unit = {},

    ) {

    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(text = "Return Product")
        },
        text = {
            Column {
                TextField(
                    value = returnAmount,
                    onValueChange = onReturnAmountChanged,
                    label = { Text("Return amount") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
                snackBar()

            }


        },
        confirmButton = {
            Button(
                enabled = enabledConfirm,
                onClick = {
                    onConfirm()

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
    items: List<PurchasedProduct>,
) {
    private val _items = MutableStateFlow(items)
    val items = _items.asStateFlow()
    private val _showConfirmationDialog = MutableStateFlow(false)
    val showConfirmationDialog = _showConfirmationDialog.asStateFlow()
    private val _returnAmount = MutableStateFlow("")
    val returnAmount = _returnAmount.asStateFlow()

    private var returningItem: PurchasedProduct? = null


    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()
    private val _enableConfirm = MutableStateFlow(true)
    val enabledConfirm = _enableConfirm.asStateFlow()
    init {
        CoroutineScope(Dispatchers.Default).launch {
            _returnAmount.collect{amount->
                if (amount.isEmpty())
                    _enableConfirm.update { false }
            }
        }
    }

    fun onReturnAmountChanged(amount: String) {
        _returnAmount.update { amount }
        try {
            val purchasedQuantity = returningItem!!.quantity
            val typedAmount = amount.toInt()
            if (typedAmount <= 0)
                _enableConfirm.update { false }
            else if (typedAmount > purchasedQuantity)
                _enableConfirm.update { false }
            else
                _enableConfirm.update { true }
        } catch (e: Exception) {
            _enableConfirm.update { false }
        }
    }

    fun onReturnRequest(purchaseId: String) {
        returningItem = items.value.find { it.purchaseId == purchaseId }

        showConfirmationDialogue()
    }

    //dialog section
    fun showConfirmationDialogue() {
        _showConfirmationDialog.update { true }

    }

    fun onConfirm() {

    }

    private fun updateErrorMessage(msg: String?, duration: Long = 3000) {
        CoroutineScope(Dispatchers.Default).launch {
            _errorMessage.update { msg }
            delay(duration)
            _errorMessage.update { null }
        }


    }


    fun onDismissDialog() {
        _showConfirmationDialog.update { false }
        updateErrorMessage(null)

    }


}

@Composable
private fun CartList(
    cartItems: List<PurchasedProduct>,
    onReturnRequest: (purchaseId: String) -> Unit,
) {

    LazyVerticalGrid(
        columns = GridCells.Adaptive(300.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(cartItems) { _, item ->
            CartItemView(
                item = item,
                onReturnRequest = { onReturnRequest(item.purchaseId) }
            )
        }
    }

}

@Composable
fun CartItemView(
    item: PurchasedProduct,
    onReturnRequest: () -> Unit,
) {
    Surface(
        modifier = Modifier.padding(2.dp),
        shape = RoundedCornerShape(4.dp),
        shadowElevation = 1.dp, //Daraz and Amazon app uses less elevation
    ) {

        Row(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            _ProductImage(
                modifier = Modifier.size(80.dp),
                url = item.productImageUrl
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(Modifier) {
                Text(
                    text = item.productName,
                    style = MaterialTheme.typography.titleMedium,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Quantity:${item.quantity}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Expire:${item.returnExpireDate}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                modifier = Modifier,
                onClick = onReturnRequest,
            ) {
                Text(text = "Return", color = MaterialTheme.colorScheme.secondary)
            }


        }
    }
}

fun getDummyPurchaseProduct() = listOf(
    PurchasedProduct(
        purchaseId = "P001",
        productName = "Laptop",
        productImageUrl = "https://d61s2hjse0ytn.cloudfront.net/card_image/None/blackcard.webp",
        quantity = 1,
        returnExpireDate = LocalDate.now().toString()
    ),
    PurchasedProduct(
        purchaseId = "P002",
        productName = "Smartphone",
        productImageUrl = "https://mobilebazar.com.bd/assets/img/Apple_iPhone_SE_(2022).webp",
        quantity = 2,
        returnExpireDate = LocalDate.now().toString()
    ),

    )

data class PurchasedProduct(
    val purchaseId: String,
    val productName: String,
    val productImageUrl: String,
    val quantity: Int,
    val returnExpireDate: String,
)


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
