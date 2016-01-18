package NameCard.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;

import NameCard.entity.Card;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import name.mawentao.contactscard.R;


/**
 * Created by ZTR on 12/31/15.
 */
public class CardTextHelper {

    private String[] mainContent = new String[3];
    private String[] attachedContent = new String[2];

    private final String TANG_KAI_JIAN_FONT = "YeGenYouTangKaiJian.ttf";
    private final String KAI_TI_FONT = "Kaiti.ttc";
    private final float MAIN_INFO_TEXT_STZE = 16f;
    private final float ATTACHED_INFO_TEXT_SIZE = 20f;

    private Card card;

    private Context context;

    public CardTextHelper(Card card, Context context){
        this.card = card;
        this.context = context;
        mainContent[0] = "姓名:" + card.name;
        mainContent[1] = "电话:" + card.telephoneNum;
        mainContent[2] = "职务:" + card.occupation;
        attachedContent[0] = "Email:" + card.email;
        attachedContent[1] = "地址:" + card.address;
    }

    private Bitmap createBitmapFromCard(
            String[] subContent, int bitMapWidth, int bitMapHeight,
            int startVerticalGap, int horizontalGap, int baseLineGap,
            float textSize, String fontFileName){


        Resources res = context.getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.wood_plank);

        Bitmap resultBitmap = Bitmap.createBitmap(bitMapWidth, bitMapHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(resultBitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(textSize);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setARGB(255,255,255,255);
        canvas.drawBitmap(bitmap, 0, 0, paint);

        paint.setARGB(255, 114, 59, 28);
        for(int i = 0; i < subContent.length; i++){
            canvas.drawText(subContent[i], horizontalGap, startVerticalGap, paint);
            startVerticalGap += baseLineGap;
        }

        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return resultBitmap;

    }

    public Bitmap createMainInfoBitmap(){
        return createBitmapFromCard(mainContent, 256, 128, 30, 10, 36, MAIN_INFO_TEXT_STZE, TANG_KAI_JIAN_FONT);
    }

    public Bitmap createAttachedInfoBitmap(){
        return createBitmapFromCard(attachedContent, 512, 64, 24, 10, 22, ATTACHED_INFO_TEXT_SIZE, KAI_TI_FONT);
    }



}
