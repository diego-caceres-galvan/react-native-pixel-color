package eu.crowtec.rnpixelcolor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import fr.bamlab.rnimageresizer.ImageResizer;

import java.io.IOException;


class RNPixelColorModule extends ReactContextBaseJavaModule {
    private Context context;
    private Bitmap image;

    public RNPixelColorModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.context = reactContext;
    }

    @Override
    public String getName() {
        return "RNPixelColor";
    }

    @ReactMethod
    public void getHex(String path, ReadableMap options, final Callback callback) {

        if (path.startsWith("data:") || path.startsWith("file:")) {
          this.image = ImageResizer.loadBitmapFromBase64(path);
        } else {

            try {
                InputStream istr = this.context.getAssets().open(path);
                this.image = BitmapFactory.decodeStream(istr);

            } catch (IOException e) {
                // handle exception
                callback.invoke("Error parsing bitmap. Error: " + e.getMessage(), null);
                return;
            }
        }

        if (image == null) {
            callback.invoke("Could not create image from given path.", null);
            return;
        }

//        //validate and set rotation of image
//        if (originalRotation != 0) {
//            this.image = ImageResizer.rotateImage(this.image, originalRotation);
//        }

        if (this.image == null) {
            callback.invoke("not have image fo get hex", null);
            return;
        }

        int x = (int) options.getDouble("x");
        int y = (int) options.getDouble("y");

        if (options.hasKey("width") && options.hasKey("height")) {
            int scaledWidth = options.getInt("width");
            int scaledHeight = options.getInt("height");
            
            int originalWidth = this.image.getWidth();
            int originalHeight = this.image.getHeight();

            x = (int)((double)x * ((double)originalWidth / (double)scaledWidth));
            y = (int)((double)y * ((double)originalHeight / (double)scaledHeight));

        }

        if(x > 0 && x < this.image.getWidth() &&
                y > 0 && y < this.image.getHeight()) {
            int color = colorAtPixel(this.image, x, y);

            callback.invoke(null, colorToHexString(color));

        } else {
            callback.invoke(null, "transparent");
        }
    }

    private int colorAtPixel(Bitmap bitmap, int x, int y) {
        return bitmap.getPixel(x, y);
    }

    private String colorToHexString(int color) {
        return String.format("#%06X", (0xFFFFFF & color));
    }


}
