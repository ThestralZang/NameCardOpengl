package Utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * Created by PUPPY on 2015/12/30 0030.
 */
public class TextHelper {

    private static HanyuPinyinOutputFormat format;
    public static String replaceChinese(String s){
        String result="";
        char[] x=s.toCharArray();
        for(int i=0;i<x.length;i++){
            if(isChinese(x[i])){
                format=new HanyuPinyinOutputFormat();
                format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
                format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
                format.setVCharType(HanyuPinyinVCharType.WITH_V);
                try {
                    String[] temp= PinyinHelper.toHanyuPinyinStringArray(x[i], format);
                    result=result+temp[0];
                } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                    badHanyuPinyinOutputFormatCombination.printStackTrace();
                }
            }else{
                result=result+x[i];
            }
        }
        return result;
    }

    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }
}
