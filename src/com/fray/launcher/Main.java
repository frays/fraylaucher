package com.fray.launcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
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

public class Main extends FragmentActivity implements View.OnClickListener {

    public static final String TAG = "fray_launcher";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); // Убираем заголовок
        setContentView(R.layout.main);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentBook);

        if (fragment == null) {
            fragment = new BookFragment();
            fm.beginTransaction()
                    .add(R.id.fragmentBook, fragment)
                    .commit();
        }

        fragment = fm.findFragmentById(R.id.fragmentButtons);

        if (fragment == null) {
            fragment = new ButtonsFragment();
            fm.beginTransaction()
                    .add(R.id.fragmentButtons, fragment)
                    .commit();
        }

        ViewFactory.setContext(this);

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
            case R.id.mainMenu_appScan:
                AppAdapter.updateAppList(this);
                break;
        }

        return true;
    }

    public static String timeFormat(long ms) {
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

        return true;
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {
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
                    startActivity(this.getPackageManager().getLaunchIntentForPackage("lysesoft.andsmb"));
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

}

