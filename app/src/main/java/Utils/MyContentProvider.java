package Utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wentao on 2015/12/29.
 *
 * Provide contacts list, select and cut image
 */
public class MyContentProvider {

    public static List<NameCard> getSystemContactsList(Context context){
        List<NameCard> importCardList = new ArrayList<>();
        NameCard nameCard;

        Cursor PhoneCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);

        while (PhoneCursor.moveToNext()) {
            nameCard = new NameCard(
                    PhoneCursor.getString(PhoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)),
                    PhoneCursor.getString(PhoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)),
                    null,
                    null,
                    null);
            importCardList.add(nameCard);
        }
        PhoneCursor.close();

        return importCardList;
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static File generatePhotoFile(Context context) {
        File file;
        String prefix = "IMG-AVATAR" ;
        try {
            file = File.createTempFile(prefix, ".jpg", context.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            Log.i("PATH", file.getAbsolutePath());
            return file;
        }
        catch (IOException e) {
            // Error while creating file
            e.printStackTrace();
            return null;
        }
    }

    public static File generateTempFile(Context context){
        return new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "IMG-TEMP.jpg");
    }

    public static Bitmap getBitmap(File file)
    {
        Bitmap bitmap = null;
        try {
            if(file.exists()) {
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return bitmap;
    }

    public static File getFile(Context context, String photoPath){
        return new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), photoPath);
    }

    public static Bitmap generateQRCode(String qrCodeString){
        Bitmap bmp = null;    //二维码图片
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(qrCodeString, BarcodeFormat.QR_CODE, 512, 512); //参数分别表示为: 条码文本内容，条码格式，宽，高
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            //绘制每个像素
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

        } catch (WriterException e) {
            e.printStackTrace();
        }

        return bmp;
    }
}
