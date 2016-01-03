package com.example.NameCard.util;

import android.content.Context;
import android.graphics.*;
import com.example.NameCard.entity.Card;


/**
 * Created by ZTR on 12/31/15.
 */
public class CardTextHelper {

    private String[] mainContent = new String[3];
    private String[] attachedContent = new String[2];

    private final String TANG_KAI_JIAN_FONT = "YeGenYouTangKaiJian.ttf";
    private final String KAI_TI_FONT = "Kaiti.ttc";
    private final float MAIN_INFO_TEXT_STZE = 20f;
    private final float ATTACHED_INFO_TEXT_SIZE = 20f;

    private Card card;

    private Context context;

    public CardTextHelper(Card card, Context context){
        this.card = card;
        this.context = context;
        mainContent[0] = "姓名：" + card.name;
        mainContent[1] = "手机：" + card.telephoneNum;
        mainContent[2] = "职业：" + card.occupation;
        attachedContent[0] = "电子邮箱：" + card.email;
        attachedContent[1] = "地址：" + card.address;
    }

    private Bitmap createBitmapFromCard(
            String[] subContent, int bitMapWidth, int bitMapHeight,
            int startVerticalGap, int horizontalGap, int baseLineGap,
            float textSize, String fontFileName){

        Bitmap bitmap = Bitmap.createBitmap(bitMapWidth, bitMapHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.GRAY);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(textSize);
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/"+fontFileName);
        paint.setTypeface(typeface);
        paint.setARGB(255, 255, 255, 255);

        for(int i = 0; i < subContent.length; i++){
            canvas.drawText(subContent[i], horizontalGap, startVerticalGap, paint);
            startVerticalGap += baseLineGap;
        }

        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return bitmap;
    }

    public Bitmap createMainInfoBitmap(){
        return createBitmapFromCard(mainContent, 256, 128, 30, 10, 36, MAIN_INFO_TEXT_STZE, TANG_KAI_JIAN_FONT);
    }

    public Bitmap createAttachedInfoBitmap(){
        return createBitmapFromCard(attachedContent, 512, 64, 24, 10, 22, ATTACHED_INFO_TEXT_SIZE, KAI_TI_FONT);
    }



}
