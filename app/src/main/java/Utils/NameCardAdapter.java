package Utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SectionIndexer;

import java.util.ArrayList;
import java.util.List;

import name.mawentao.contactscard.MainActivity;
import name.mawentao.contactscard.R;

/**
 * Created by Wentao on 2015/12/27.
 * <p/>
 * Use as Card List Fragment list View adapter
 */
public class NameCardAdapter extends ArrayAdapter<NameCard> implements SectionIndexer {

    String[] sections = {"#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    List<Integer> SectionMap;
    private List<NameCard> mObjects;
    private Context mContext;
    private ImageLoader mImageLoader;

    public NameCardAdapter(Context context, int resource, List<NameCard> objects) {
        super(context, resource, objects);
        mObjects = objects;
        mContext = context;
        mImageLoader=new ImageLoader();
        updateSectionMap();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NameCard nameCard = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.card_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.cardImage = (ImageView) convertView.findViewById(R.id.card_image);
            viewHolder.nameText = (CheckedTextView) convertView.findViewById(R.id.card_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String photoPath = nameCard.getPhotoPath();
        viewHolder.cardImage.setTag(photoPath);
        if (photoPath != null && MyContentProvider.isExternalStorageReadable()) {
            mImageLoader.showImageByAsyncTask(mContext, viewHolder.cardImage, photoPath);
        } else {
            viewHolder.cardImage.setImageResource(R.drawable.ic_person);
        }
        viewHolder.nameText.setText(nameCard.getName());
        if (MainActivity.isActionMode) {
            if (viewHolder.nameText.getCheckMarkDrawable() == null)
                viewHolder.nameText.setCheckMarkDrawable(getIndicator());
            ListView mlv = (ListView) parent;
            if (mlv.isItemChecked(position)) {
                viewHolder.nameText.setChecked(true);
            } else {
                viewHolder.nameText.setChecked(false);
            }
        } else {
            if (viewHolder.nameText.getCheckMarkDrawable() != null)
                viewHolder.nameText.setCheckMarkDrawable(null);
        }
        return convertView;
    }

    //需要为每个ConvertView中的CheckedTextView提供独立的CheckMark，否则会互相影响
    //使用ResourceId不可取，因为ResouceId随着Context变化而变化
    private Drawable getIndicator() {
        int[] attrs = {android.R.attr.listChoiceIndicatorMultiple};
        TypedArray ta = mContext.getTheme().obtainStyledAttributes(attrs);
        Drawable indicator = ta.getDrawable(0);
        ta.recycle();
        return indicator;
    }

    public void updateSectionMap() {
        SectionMap = new ArrayList<>();
        char FirstLetter;
        for (NameCard aObject : mObjects) {
            FirstLetter = TextHelper.replaceChinese(aObject.getName()).toLowerCase().charAt(0);
            if (FirstLetter >= 97 && FirstLetter <= 122) {
                SectionMap.add(FirstLetter - 96);
            } else {
                SectionMap.add(0);
            }
        }
    }

    @Override
    public Object[] getSections() {
        return sections;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        int i = SectionMap.indexOf(sectionIndex);
        while (i == -1) {
            if (sectionIndex < 26) {
                sectionIndex = sectionIndex + 1;
                i = SectionMap.indexOf(sectionIndex);
            } else {
                return SectionMap.size() - 1;
            }
        }
        return i;
    }

    @Override
    public int getSectionForPosition(int position) {
        return SectionMap.get(position);
    }

    private static class ViewHolder {
        ImageView cardImage;
        CheckedTextView nameText;
    }
}
