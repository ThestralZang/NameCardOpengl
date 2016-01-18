package Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by PUPPY on 2016/1/4 0004.
 */
public class ImageLoader {

    LruCache<String, Bitmap> mMemoryCache;

    public ImageLoader() {
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };
    }

    public void addBitmapToMemoryCache(String filePath, Bitmap bitmap) {
        if (getBitmapFromMemCache(filePath) == null) {
            mMemoryCache.put(filePath, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String filePath) {
        return mMemoryCache.get(filePath);
    }

    public void showImageByAsyncTask(Context context, ImageView imageView, String filePath) {
        Bitmap bitmap = getBitmapFromMemCache(filePath);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            new LoadImageFromFileAsyncTask(context, imageView, filePath).execute(filePath);
        }
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private Bitmap decodeSampledBitmapFromFile(File file, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
    }

    private class LoadImageFromFileAsyncTask extends AsyncTask<String, Void, Bitmap> {

        private final WeakReference<ImageView> imageViewReference;
        private Context mContext;
        private String mFilePath;

        public LoadImageFromFileAsyncTask(Context context, ImageView imageView, String filePath) {
            imageViewReference = new WeakReference<>(imageView);
            mContext = context;
            mFilePath = filePath;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String filePath = params[0];
            File file = MyContentProvider.getFile(mContext, params[0]);
            Bitmap bitmap = decodeSampledBitmapFromFile(file, 100, 100);
            if (bitmap != null) {
                addBitmapToMemoryCache(filePath, bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null && imageView.getTag() == (mFilePath)) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }
}
