package com.fray.launcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.fray.launcher.adapters.AppAdapter;
import com.onyx.android.sdk.data.AscDescOrder;
import com.onyx.android.sdk.data.cms.OnyxCmsCenter;
import com.onyx.android.sdk.data.cms.OnyxHistoryEntry;
import com.onyx.android.sdk.data.cms.OnyxMetadata;
import com.onyx.android.sdk.data.cms.OnyxThumbnail;
import com.onyx.android.sdk.data.util.RefValue;

import java.util.ArrayList;
import java.util.List;

public class Main extends Activity implements View.OnClickListener {


    public static final String TAG = "fray_launcher";
    boolean dbLock = false;

    private void setLastReading() {
        List<OnyxMetadata> lst = new ArrayList<OnyxMetadata>();
        OnyxCmsCenter.getRecentReadings(this, lst);

        if (lst.size() != 0) {
            OnyxMetadata metadata = lst.get(0);
            TextView twTitle = (TextView) findViewById(R.id.txtTitle);
            TextView twAuthor = (TextView) findViewById(R.id.txtAuthor);

            if (metadata.getTitle() != null) {
                twTitle.setText(metadata.getTitle());
                twAuthor.setText(metadata.getAuthors().toString());
            } else {
                twTitle.setText(metadata.getName());
                twAuthor.setHeight(0);
            }

            TextView twRead = (TextView) findViewById(R.id.txtNow);
            twRead.setText(getString(R.string.nowreading) + " (" + metadata.getProgress().toString() + "):");

            ImageView imgBook = (ImageView) findViewById(R.id.imgBook);
            RefValue<Bitmap> btmp = new RefValue<Bitmap>();

            OnyxCmsCenter.getThumbnail(this, metadata, OnyxThumbnail.ThumbnailKind.Large, btmp);

            if (btmp.getValue() == null)
                imgBook.setImageResource(R.drawable.book);
            else
                imgBook.setImageBitmap(btmp.getValue());
            Log.i(TAG, "Image was loaded");

        }
        Log.i(TAG, "All Views were successfully updated");

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); // Убираем заголовок
        setContentView(R.layout.main);

        ViewFactory.setContext(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        dbLock = false;
        switch (v.getId()) {
            case R.id.nowR:
                try {
                    startActivity(getPackageManager().getLaunchIntentForPackage("com.neverland.alreader"));
                } catch (Exception e) {
                    Toast.makeText(this, R.string.appnotstarted, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.imgFM:
                intent = new Intent(this, FileManager.class);
                intent.setAction(FileManager.ActionFM);
                startActivity(intent);
                break;
            case R.id.imgSet:
                startActivity(new Intent(Settings.ACTION_SETTINGS));
                break;
            case R.id.imgApp:
                intent = new Intent(this, FileManager.class);
                intent.setAction(FileManager.ActionApps);
                startActivity(intent);
                break;
            case R.id.imgSync:
                try {
                    startActivity(getPackageManager().getLaunchIntentForPackage("lysesoft.andsmb"));
                } catch (Exception e) {
                    Toast.makeText(this, R.string.appnotstarted, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.imgLib:
                intent = new Intent(this, FileManager.class);
                intent.setAction(FileManager.ActionLibrary);
                startActivity(intent);
                break;

            default:
                Log.i(TAG, "Click by " + v.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mainMenu_history:
                showDialog(1);
                break;
            case R.id.mainMenu_update:
                setLastReading();
            case R.id.mainMenu_appScan:
                AppAdapter.updateAppList(this);
                break;
        }

        return true;
    }

    String timeFormat(long ms) {
        String result;

        result = (ms % 60) + "с"; // секунды
        ms /= 60;

        if (ms > 0) // минуты
        {
            result = (ms % 60) + "м:" + result;
            ms /= 60;
        }
        if (ms > 0) {
            result = ms + "ч:" + result;
        }
        return result;
    }

    private String getHistory() {
        String result = "";

        List<OnyxMetadata> recentReadings = new ArrayList<OnyxMetadata>();
        OnyxCmsCenter.getRecentReading(this, null, 10, AscDescOrder.Desc, recentReadings);

        for (OnyxMetadata data : recentReadings) {

            List<OnyxHistoryEntry> historyEntries = OnyxCmsCenter.getHistorysByMD5(this, data.getMD5());
            long time = 0;

            for (OnyxHistoryEntry entry : historyEntries) {
                time += (entry.getEndTime().getTime() - entry.getStartTime().getTime()) / 1000;
            }

            result += data.getTitle() + ": " + timeFormat(time) + '\n';
        }

        return result;
    }

    protected Dialog onCreateDialog(int id) {
        if (id == 1) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle(getString(R.string.history));
            adb.setMessage(getHistory());
            adb.setIcon(android.R.drawable.ic_dialog_info);
            return adb.create();
        }
        return super.onCreateDialog(id);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != KeyEvent.KEYCODE_BACK) {
            return super.onKeyDown(keyCode, event);
        }
        if (!dbLock) {
            setLastReading();
            dbLock = true;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppAdapter.updateAppList(this); // Обновляем список приложений
        setLastReading(); // Считываем последнюю считанную книгу
    }

}
