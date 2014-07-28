package com.atsexp.fly.preview;

import java.io.File;
import java.util.Collections;

import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import android.widget.ImageView;

public enum IconPreview {
	INSTANCE;

	private final ConcurrentMap<String, Bitmap> cache;
	private final ExecutorService pool;
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new ConcurrentHashMap<ImageView, String>());
	private static PackageManager pm;
	private static int mSize = 64;
	private Bitmap placeholder;

	IconPreview() {
		cache = new ConcurrentHashMap<String, Bitmap>();
		pool = Executors.newFixedThreadPool(5);
	}

	public void setPlaceholder(Bitmap bmp) {
		placeholder = bmp;
	}

	public Bitmap getBitmapFromCache(String url) {
		if (cache.containsKey(url)) {
			return cache.get(url);
		}

		return null;
	}

	public void queueJob(final File uri, final ImageView imageView) {
		/* Create handler in UI thread. */
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String tag = imageViews.get(imageView);
				if (tag != null && tag.equals(uri.getAbsolutePath())) {
					if (msg.obj != null) {
						imageView.setImageBitmap((Bitmap) msg.obj);
					} else {
						imageView.setImageBitmap(placeholder);
						// Fail
					}
				}
			}
		};

		pool.submit(new Runnable() {
			public void run() {
				final Bitmap bmp = getPreview(uri);
				Message message = Message.obtain();
				message.obj = bmp;

				handler.sendMessage(message);
			}
		});
	}

	public void loadBitmap(final File file, final ImageView imageView) {
		imageViews.put(imageView, file.getAbsolutePath());
		Bitmap bitmap = getBitmapFromCache(file.getAbsolutePath());

		// check in UI thread, so no concurrency issues
		if (bitmap != null) {
			// Item loaded from cache
			imageView.setImageBitmap(bitmap);
		} else {
			imageView.setImageBitmap(placeholder);
			queueJob(file, imageView);
		}
	}

	public void loadApk(final File file1, final ImageView imageView,
			final Context context) {
		pm = context.getPackageManager();
		imageViews.put(imageView, file1.getAbsolutePath());
		Bitmap bitmap = getBitmapFromCache(file1.getAbsolutePath());

		// check in UI thread, so no concurrency issues
		if (bitmap != null) {
			// Item loaded from cache
			imageView.setImageBitmap(bitmap);
		} else {
			imageView.setImageBitmap(placeholder);
			queueJob(file1, imageView);
		}
	}

	private Bitmap getPreview(File file) {
		final boolean isImage = MimeTypes.isPicture(file);
		final boolean isVideo = MimeTypes.isVideo(file);
		final boolean isApk = file.getName().endsWith(".apk");
		Bitmap mBitmap = null;
		String path = file.getAbsolutePath();

		if (isImage) {
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;

			BitmapFactory.decodeFile(path, o);
			o.inJustDecodeBounds = false;

			if (o.outWidth != -1 && o.outHeight != -1) {
				final int originalSize = (o.outHeight > o.outWidth) ? o.outWidth
						: o.outHeight;
				o.inSampleSize = originalSize / mSize;
			}

			mBitmap = BitmapFactory.decodeFile(path, o);

			cache.put(path, mBitmap);
			return mBitmap;
		} else if (isVideo) {
			mBitmap = ThumbnailUtils.createVideoThumbnail(path,
					MediaStore.Video.Thumbnails.MICRO_KIND);

			cache.put(path, mBitmap);
			return mBitmap;
		} else if (isApk) {
			final PackageInfo packageInfo = pm.getPackageArchiveInfo(path,
					PackageManager.GET_ACTIVITIES);

			if (packageInfo != null) {
				final ApplicationInfo appInfo = packageInfo.applicationInfo;

				if (appInfo != null) {
					appInfo.sourceDir = path;
					appInfo.publicSourceDir = path;
					final Drawable icon = appInfo.loadIcon(pm);

					if (icon != null) {
						mBitmap = ((BitmapDrawable) icon).getBitmap();
					}
				}
			}

			cache.put(path, mBitmap);
			return mBitmap;
		}
		return null;
	}
}