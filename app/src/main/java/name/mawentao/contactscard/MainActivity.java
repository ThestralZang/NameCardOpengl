package name.mawentao.contactscard;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.transition.Slide;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import Interface.DatabaseController;
import Utils.MyContentProvider;
import Utils.MyDatabaseController;
import Utils.NameCard;
import Utils.NameCardAdapter;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public static final int EDIT_REQUEST = 1080;
    public static final int ADD_REQUEST = 1090;
    public static boolean isActionMode = false;

    private List<NameCard> cardList;
    private ListView cardListView;
    private NameCardAdapter adapter;
    mMultiChoiceModeListener mCallBack;
    private DatabaseController databaseController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupWindowAnimations();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseController = new MyDatabaseController();
        cardList = databaseController.getCardList(this);
        Collections.sort(cardList);
        adapter = new NameCardAdapter(this, R.layout.card_item, cardList);
        cardListView = (ListView) findViewById(R.id.card_list);
        cardListView.setAdapter(adapter);
        cardListView.setFastScrollEnabled(true);
        cardListView.setFastScrollAlwaysVisible(true);
        cardListView.setOnItemClickListener(this);
        mCallBack = new mMultiChoiceModeListener();
        cardListView.setMultiChoiceModeListener(mCallBack);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupWindowAnimations() {
        getWindow().requestFeature(android.view.Window.FEATURE_CONTENT_TRANSITIONS);
        Slide ts_exit = new Slide();
        ts_exit.setDuration(300);
        ts_exit.setSlideEdge(Gravity.LEFT);
        getWindow().setExitTransition(ts_exit);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Collections.sort(cardList);
        adapter.notifyDataSetChanged();
        adapter.updateSectionMap();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //Scan QR Code return
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                try {
                    Gson gson = new Gson();
                    NameCard newcard = gson.fromJson(URLDecoder.decode(result.getContents(), "utf-8"), NameCard.class);
                    newcard.setId(-1);
                    newcard.setPhotoPath(null);
                    EditActivity.actionStart(this, newcard, ADD_REQUEST);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }

        //EditActivity return
        switch (requestCode) {
            case EDIT_REQUEST:
                if (resultCode == EditActivity.RESULT_DELETE) {
                    NameCard reaultCard = data.getParcelableExtra(EditActivity.INTENT_TAG);
                    if (reaultCard.getPhotoPath() != null) {
                        MyContentProvider.getFile(this, reaultCard.getPhotoPath()).delete();
                    }
                    Iterator i = cardList.iterator();
                    while (i.hasNext()) {
                        if (((NameCard) i.next()).getId() == reaultCard.getId()) {
                            i.remove();
                        }
                    }
                }
                if (resultCode == EditActivity.RESULT_EDIT) {
                    NameCard reaultCard = data.getParcelableExtra(EditActivity.INTENT_TAG);
                    for (NameCard nc : cardList) {
                        if (nc.getId() == reaultCard.getId()) {
                            nc.setValue(reaultCard);
                            break;
                        }
                    }
                }
                break;
            case ADD_REQUEST:
                if (resultCode == EditActivity.RESULT_ADD) {
                    NameCard reaultCard = data.getParcelableExtra(EditActivity.INTENT_TAG);
                    cardList.add(reaultCard);
                }
                if (resultCode == EditActivity.RESULT_DELETE) {
                    NameCard reaultCard = data.getParcelableExtra(EditActivity.INTENT_TAG);
                    if (reaultCard.getPhotoPath() != null) {
                        MyContentProvider.getFile(this, reaultCard.getPhotoPath()).delete();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_scan:
                startScanUsedToolbar();
                break;
            case R.id.action_import:
                ProgressDialog dialog = ProgressDialog.show(this, "", "正在导入联系人...");
                List<NameCard> importCardList = MyContentProvider.getSystemContactsList(this);
                databaseController.insertCardList(this, importCardList);
                cardList.addAll(importCardList);
                Collections.sort(cardList);
                adapter.notifyDataSetChanged();
                adapter.updateSectionMap();
                dialog.dismiss();
                break;
            case R.id.action_add:
                NameCard newcard = new NameCard();
                EditActivity.actionStart(this, newcard, ADD_REQUEST);
                break;
            case R.id.action_select:
                cardListView.setItemChecked(0, true);
                cardListView.clearChoices();
                mCallBack.updateTitle();
                adapter.notifyDataSetChanged();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (l < 0) {
            // click headerView or footerView
            return;
        }
        int realPosition = (int) l;
        NameCard item = (NameCard) adapterView.getItemAtPosition(realPosition);
        EditActivity.actionStarteithAnimation(this, item, view, EDIT_REQUEST);
    }

    protected void startScanUsedToolbar() {
        //使用ZXing库扫描二维码
        new IntentIntegrator(this).setCaptureActivity(ToolbarCaptureActivity.class).initiateScan();
    }

    private class mMultiChoiceModeListener implements AbsListView.MultiChoiceModeListener {
        private ActionMode mActionMode;
        private CheckBox selectAllcb;
        private boolean manualDisSelect = true;

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            updateTitle();
            if (selectAllcb.isChecked() && !checked) {
                manualDisSelect = false;
                selectAllcb.setChecked(false);
            }
            adapter.notifyDataSetChanged();
        }

        @Override
        public boolean onCreateActionMode(final ActionMode mode, Menu menu) {
            isActionMode = true;
            mActionMode = mode;
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_context, menu);
            selectAllcb = (CheckBox) menu.findItem(R.id.action_select_all).getActionView();
            selectAllcb.setText("全选");
            selectAllcb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        for (int i = 0; i < adapter.getCount(); i++) {
                            cardListView.setItemChecked(i, true);
                        }
                    } else {
                        if (manualDisSelect) {
                            cardListView.clearChoices();
                            updateTitle();
                            adapter.notifyDataSetChanged();
                        } else {
                            manualDisSelect = true;
                        }
                    }
                }
            });
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_batch_delete:
                    ArrayList<NameCard> nc = new ArrayList<>();
                    for (int i = 0; i < adapter.getCount(); i++) {
                        if (cardListView.isItemChecked(i)) {
                            nc.add((NameCard) cardListView.getItemAtPosition(i));
                        }
                    }
                    for (NameCard aNameCard : nc) {
                        databaseController.deleteCard(MainActivity.this, aNameCard);
                        cardList.remove(aNameCard);
                    }
                    adapter.notifyDataSetChanged();
                    adapter.updateSectionMap();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            cardListView.clearChoices();
            isActionMode = false;
        }

        private void updateTitle() {
            mActionMode.setTitle(cardListView.getCheckedItemCount() + " 已选中");
        }
    }
}
