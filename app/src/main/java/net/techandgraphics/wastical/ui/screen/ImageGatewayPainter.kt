package net.techandgraphics.wastical.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import net.techandgraphics.wastical.R

@Composable fun imageGatewayPainter(imageUrl: String, imageLoader: ImageLoader): AsyncImagePainter {
  return rememberAsyncImagePainter(
    model = ImageRequest.Builder(LocalContext.current)
      .data(imageUrl)
      .diskCacheKey(imageUrl)
      .networkCachePolicy(CachePolicy.ENABLED)
      .crossfade(true)
      .build(),
    imageLoader = imageLoader,
    placeholder = painterResource(R.drawable.im_placeholder),
    error = painterResource(R.drawable.im_placeholder),
  )
}
