package name.mawentao.contactscard;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import Interface.DatabaseController;
import Utils.MyContentProvider;
import Utils.MyDatabaseController;
import Utils.NameCard;

public class EditActivity extends AppCompatActivity {

    public static final String INTENT_TAG = "Utils.NameCard";
    public static final int RESULT_DELETE = 80;
    public static final int RESULT_ADD = 90;
    public static final int RESULT_EDIT = 100;
    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    public static final int AVATAR_PHOTO_SIZE = 256; //头像宽高
    private File photoFile;
    private ImageButton imageButton;
    private EditText editName;
    private EditText editJob;
    private EditText editPhone;
    private EditText editEmail;
    private EditText editAddress;
    private NameCard nameCard;

    private TextView name;
    private TextView telephoneNumber;
    private ImageButton save;
    FloatingActionsMenu floatingActionsMenu;

    private DatabaseController databaseController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupWindowAnimations();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Intent intent = getIntent();
        nameCard = intent.getParcelableExtra(INTENT_TAG);
        databaseController = new MyDatabaseController();

        imageButton = (ImageButton) findViewById(R.id.avatar_button);
        editName = (EditText) findViewById(R.id.edit_name);
        editJob = (EditText) findViewById(R.id.edit_job);
        editPhone = (EditText) findViewById(R.id.edit_phone);
        editEmail = (EditText) findViewById(R.id.edit_email);
        editAddress = (EditText) findViewById(R.id.edit_address);
        floatingActionsMenu = (FloatingActionsMenu) findViewById(R.id.multiple_actions);

        String photoPath = nameCard.getPhotoPath();
        if (photoPath != null) {
            photoFile = MyContentProvider.getFile(this, photoPath);
            Bitmap photo = MyContentProvider.getBitmap(photoFile);
            Drawable drawable = new BitmapDrawable(this.getResources(), photo);
            imageButton.setBackground(drawable);
        } else {
            photoFile = MyContentProvider.generateTempFile(this);
            imageButton.setBackgroundResource(R.drawable.ic_person);
        }

        ImageButton sendmessage = (ImageButton) findViewById(R.id.message_button);
        ImageButton phone = (ImageButton) findViewById(R.id.call_button);
        name = (TextView) findViewById(R.id.name_top_part_text);
        telephoneNumber = (TextView) findViewById(R.id.phone_top_part_text);
        ImageButton back = (ImageButton) findViewById(R.id.back_button);
        ImageButton qrcode = (ImageButton) findViewById(R.id.get_qrcode_button);
        save = (ImageButton) findViewById(R.id.action_save);

        editName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                name.setText(s);
            }
        });
        editPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                telephoneNumber.setText(s);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NameCard copyCard = nameCard.getCopy();
                nameCard.setName(editName.getText().toString());
                nameCard.setJob(editJob.getText().toString());
                nameCard.setPhone(editPhone.getText().toString());
                nameCard.setEmail(editEmail.getText().toString());
                nameCard.setAddress(editAddress.getText().toString());

                if (!nameCard.getName().isEmpty()) {
                    if (nameCard.getId() == -1) {
                        databaseController.saveCard(EditActivity.this, nameCard);
                        Intent intent = new Intent();
                        intent.putExtra(INTENT_TAG, nameCard);
                        setResult(RESULT_ADD, intent);
                        while (nameCard.getId() == -1) {
                        }
                        EditActivity.this.finish();
                    } else if (!nameCard.equals(copyCard)) {
                        databaseController.saveCard(EditActivity.this, nameCard);
                    }
                    save.setVisibility(View.INVISIBLE);
                    editName.setFocusable(false);
                    editPhone.setFocusable(false);
                    editEmail.setFocusable(false);
                    editJob.setFocusable(false);
                    editAddress.setFocusable(false);
                } else {
                    nameCard = copyCard;
                    Toast toast = Toast.makeText(EditActivity.this, "名字不能为空", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
        qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                Bitmap QRCode = null;
                try {
                    QRCode = MyContentProvider.generateQRCode(URLEncoder.encode(gson.toJson(nameCard), "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                ImageView img = new ImageView(EditActivity.this);
                img.setImageBitmap(QRCode);
                new AlertDialog.Builder(EditActivity.this).setTitle("扫描传递名片").setView(img).setPositiveButton("确定", null).show();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditActivity.this.onBackPressed();
            }
        });
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + nameCard.getPhone()));
                startActivity(intent);
            }
        });
        sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("smsto:" + nameCard.getPhone()));
                startActivity(intent);
            }
        });

        final View actionDelete = findViewById(R.id.action_delete);
        final View actionDisplay = findViewById(R.id.action_display);
        final View actionEdit = findViewById(R.id.action_edit);

        //为3个浮动按钮FAB设置conClick方法
        actionEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save.setVisibility(View.VISIBLE);
                editName.setFocusableInTouchMode(true);
                editPhone.setFocusableInTouchMode(true);
                editEmail.setFocusableInTouchMode(true);
                editJob.setFocusableInTouchMode(true);
                editAddress.setFocusableInTouchMode(true);
                editName.requestFocus();
                floatingActionsMenu.collapse();
            }
        });

        actionDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseController.deleteCard(EditActivity.this, nameCard);
                Intent intent = new Intent();
                intent.putExtra(INTENT_TAG, nameCard);
                setResult(RESULT_DELETE, intent);
                EditActivity.this.finish();
            }
        });

        actionDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditActivity.this, OpenglActivity.class);
                intent.putExtra(INTENT_TAG, nameCard);
                startActivity(intent);

            }
        });

        //判断是否为新建名片，是则显示保存按钮
        if (nameCard.getId() != -1) {
            save.setVisibility(View.INVISIBLE);
            editName.setFocusable(false);
            editPhone.setFocusable(false);
            editEmail.setFocusable(false);
            editJob.setFocusable(false);
            editAddress.setFocusable(false);
        } else {
            save.setVisibility(View.VISIBLE);
            editName.setFocusableInTouchMode(true);
            editPhone.setFocusableInTouchMode(true);
            editEmail.setFocusableInTouchMode(true);
            editJob.setFocusableInTouchMode(true);
            editAddress.setFocusableInTouchMode(true);
            editName.requestFocus();
        }

        //设置界面editText显示内容
        editName.setText(nameCard.getName());
        editJob.setText(nameCard.getJob());
        editPhone.setText(nameCard.getPhone());
        editEmail.setText(nameCard.getEmail());
        editAddress.setText(nameCard.getAddress());

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupWindowAnimations() {
        getWindow().requestFeature(android.view.Window.FEATURE_CONTENT_TRANSITIONS);
        Slide ts_enter = new Slide();
        ts_enter.setDuration(300);
        ts_enter.setSlideEdge(Gravity.RIGHT);
        getWindow().setEnterTransition(ts_enter);
    }

    @Override
    public void onBackPressed() {
        if (nameCard.getId() != -1) {
            Intent intent = new Intent();
            intent.putExtra(INTENT_TAG, nameCard);
            setResult(RESULT_EDIT, intent);
        } else {
            Intent intent = new Intent();
            intent.putExtra(INTENT_TAG, nameCard);
            setResult(RESULT_DELETE, intent);
        }
        super.onBackPressed();
    }

    public static void actionStart(Context context, NameCard nameCard, int requstCode) {
        Intent intent = new Intent(context, EditActivity.class);
        intent.putExtra(INTENT_TAG, nameCard);
        ((Activity) context).startActivityForResult(intent, requstCode);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void actionStarteithAnimation(Context context, NameCard nameCard, View view, int requstCode) {
        Intent intent = new Intent(context, EditActivity.class);
        intent.putExtra(INTENT_TAG, nameCard);
        ImageView cardImage = (ImageView) view.findViewById(R.id.card_image);
        Pair<View, String> pImage = Pair.create((View) cardImage, "ImageTransition");
        CheckedTextView nameText = (CheckedTextView) view.findViewById(R.id.card_name);
        Pair<View, String> pText = Pair.create((View) nameText, "TextTransition");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pImage, pText);
        ((Activity) context).startActivityForResult(intent, requstCode, options.toBundle());
    }

    public void setAvatar(View view) {
        new AlertDialog.Builder(this)
                .setTitle("头像设置")
                .setPositiveButton("拍照", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // 调用系统的拍照功能
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // 指定调用相机拍照后照片的储存路径
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
                    }
                })
                .setNegativeButton("相册", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_PICK, null);
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
                    }
                }).show();
    }

    private void startPhotoZoom(Uri uri, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    //将进行剪裁后的图片显示到UI界面上, 并保存到数据库
    private void setPicToView(Intent picdata) {
        Bundle bundle = picdata.getExtras();
        if (bundle != null) {
            Bitmap photo = bundle.getParcelable("data");
            Drawable drawable = new BitmapDrawable(this.getResources(), photo);
            imageButton.setBackground(drawable);
            try {
                /* Convert bitmap to byte array */
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                byte[] bitmapdata = bos.toByteArray();

                //write the bytes in file
                photoFile = MyContentProvider.generatePhotoFile(this);
                FileOutputStream fos = new FileOutputStream(photoFile);

                fos.write(bitmapdata);
                fos.flush();
                fos.close();

                nameCard.setPhotoPathFromFile(photoFile);
//                Log.i("PATH", nameCard.getPhotoPath());
                databaseController.saveCardPhotoPath(this, nameCard);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 处理图片返回事件
        switch (requestCode) {
            case PHOTO_REQUEST_TAKEPHOTO:
                startPhotoZoom(Uri.fromFile(photoFile), AVATAR_PHOTO_SIZE);
                break;

            case PHOTO_REQUEST_GALLERY:
                if (data != null)
                    startPhotoZoom(data.getData(), AVATAR_PHOTO_SIZE);
                break;

            case PHOTO_REQUEST_CUT:
                if (data != null)
                    setPicToView(data);
                break;
        }
    }
}
