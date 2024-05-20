package khalekuzzaman_cse.compose_common_ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ProductionQuantityLimits
import androidx.compose.material3.Text
import khalekuzzaman_cse.compose_common_ui.ui.theme.ComposeCommonUITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeCommonUITheme {
                Text(text = "Hello CMP Template")

            }
        }
    }
}

