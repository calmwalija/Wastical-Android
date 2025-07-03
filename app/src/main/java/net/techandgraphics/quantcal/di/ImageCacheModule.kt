package net.techandgraphics.quantcal.di

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ImageCacheModule {

  @Provides
  @Singleton
  fun providesImageLoader(@ApplicationContext context: Context): ImageLoader {
    return ImageLoader.Builder(context)
      .diskCache {
        DiskCache.Builder()
          .directory(context.cacheDir.resolve("image_cache"))
          .maxSizePercent(0.02)
          .build()
      }
      .crossfade(true)
      .build()
  }
}
