package product_catalog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest

@Composable
fun ImageLoader(
    modifier: Modifier = Modifier,
    url: String,
    onSuccess:()->Unit={}
) {

    AsyncImage(
        model = ImageRequest.Builder(LocalPlatformContext.current)
            .data(url)
            // .placeholderMemoryCacheKey(screen.placeholder)
            // .apply { extras.setAll(screen.image.extras) }
            .build(),
        contentDescription = null,
        onState = {
            if( it is AsyncImagePainter.State.Success) {
                onSuccess()
            }
        },
        modifier = modifier,
    )

}
