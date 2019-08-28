package com.lib.jsdk.glide.load.engine.prefill;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.VisibleForTesting;

import com.lib.jsdk.glide.load.DecodeFormat;
import com.lib.jsdk.glide.load.engine.bitmap_recycle.BitmapPool;
import com.lib.jsdk.glide.load.engine.cache.MemoryCache;
import com.lib.jsdk.glide.util.Util;
import java.util.HashMap;
import java.util.Map;

/**
 * A class for pre-filling {@link Bitmap Bitmaps} in a
 * {@link BitmapPool}.
 */
public final class BitmapPreFiller {

  private final MemoryCache memoryCache;
  private final BitmapPool bitmapPool;
  private final DecodeFormat defaultFormat;
  private final Handler handler = new Handler(Looper.getMainLooper());

  private BitmapPreFillRunner current;

  public BitmapPreFiller(MemoryCache memoryCache, BitmapPool bitmapPool,
      DecodeFormat defaultFormat) {
    this.memoryCache = memoryCache;
    this.bitmapPool = bitmapPool;
    this.defaultFormat = defaultFormat;
  }

  @SuppressWarnings("deprecation")
  public void preFill(com.lib.jsdk.glide.load.engine.prefill.PreFillType.Builder... bitmapAttributeBuilders) {
    if (current != null) {
      current.cancel();
    }

    com.lib.jsdk.glide.load.engine.prefill.PreFillType[] bitmapAttributes = new com.lib.jsdk.glide.load.engine.prefill.PreFillType[bitmapAttributeBuilders.length];
    for (int i = 0; i < bitmapAttributeBuilders.length; i++) {
      com.lib.jsdk.glide.load.engine.prefill.PreFillType.Builder builder = bitmapAttributeBuilders[i];
      if (builder.getConfig() == null) {
        builder.setConfig(
            defaultFormat == DecodeFormat.PREFER_ARGB_8888
            ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
      }
      bitmapAttributes[i] = builder.build();
    }

    com.lib.jsdk.glide.load.engine.prefill.PreFillQueue allocationOrder = generateAllocationOrder(bitmapAttributes);
    current = new BitmapPreFillRunner(bitmapPool, memoryCache, allocationOrder);
    handler.post(current);
  }

  @VisibleForTesting
  com.lib.jsdk.glide.load.engine.prefill.PreFillQueue generateAllocationOrder(com.lib.jsdk.glide.load.engine.prefill.PreFillType... preFillSizes) {
    final long maxSize =
        memoryCache.getMaxSize() - memoryCache.getCurrentSize() + bitmapPool.getMaxSize();

    int totalWeight = 0;
    for (com.lib.jsdk.glide.load.engine.prefill.PreFillType size : preFillSizes) {
      totalWeight += size.getWeight();
    }

    final float bytesPerWeight = maxSize / (float) totalWeight;

    Map<com.lib.jsdk.glide.load.engine.prefill.PreFillType, Integer> attributeToCount = new HashMap<>();
    for (com.lib.jsdk.glide.load.engine.prefill.PreFillType size : preFillSizes) {
      int bytesForSize = Math.round(bytesPerWeight * size.getWeight());
      int bytesPerBitmap = getSizeInBytes(size);
      int bitmapsForSize = bytesForSize / bytesPerBitmap;
      attributeToCount.put(size, bitmapsForSize);
    }

    return new com.lib.jsdk.glide.load.engine.prefill.PreFillQueue(attributeToCount);
  }

  private static int getSizeInBytes(PreFillType size) {
    return Util.getBitmapByteSize(size.getWidth(), size.getHeight(), size.getConfig());
  }
}

