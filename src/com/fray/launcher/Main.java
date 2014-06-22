package com.fray.launcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.fray.launcher.adapters.AppAdapter;
import com.onyx.android.sdk.data.cms.OnyxCmsCenter;
import com.onyx.android.sdk.data.cms.OnyxMetadata;
import com.onyx.android.sdk.data.cms.OnyxThumbnail;
import com.onyx.android.sdk.data.util.RefValue;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Main extends Activity implements View.OnClickListener {


    public static final String PROVIDER_AUTHORITY = "com.onyx.android.sdk.OnyxCmsProvider";
    public static final String DB_METADATA = "library_metadata";
    public static final String DB_HISTORY = "library_history";
    public static final String TAG = "fray_launcher";
    public static final String selfName = "com.fray.launcher";
    boolean dbLock = false;

    private void setLastReaded()
    {
        List<OnyxMetadata> lst = new ArrayList<OnyxMetadata>();
        OnyxCmsCenter.getRecentReadings(this, lst);

        if (lst.size() != 0)
        {
            OnyxMetadata metadata = lst.get(0);
            TextView twTitle = (TextView)findViewById(R.id.txtTitle);
            TextView twAuthor = (TextView)findViewById(R.id.txtAuthor);

            if (metadata.getTitle() != null)
            {
                twTitle.setText(metadata.getTitle());
                twAuthor.setText(metadata.getAuthors().toString());
            }
            else
            {
                twTitle.setText(metadata.getName());
                twAuthor.setHeight(0);
            }

            TextView twRead = (TextView)findViewById(R.id.txtNow);
            twRead.setText(getString(R.string.nowreading) + " (" + metadata.getProgress().toString() +"):");

            ImageView imgBook = (ImageView)findViewById(R.id.imgBook);
            RefValue<Bitmap> btmp = new RefValue<Bitmap>();

            OnyxCmsCenter.getThumbnail(this,metadata, OnyxThumbnail.ThumbnailKind.Large,btmp);

            if (btmp.getValue() == null)
                imgBook.setImageResource(R.drawable.book);
            else
                imgBook.setImageBitmap(btmp.getValue());
            Log.i(TAG,"Image was loaded");

            //String[] pr = progress.split("/");

            //ProgressBar pb = (ProgressBar)findViewById(R.id.pbRead);
            //double pgrss = Integer.parseInt(pr[0])*100/Integer.parseInt(pr[1]);
            //pb.setProgress((int)pgrss);
        }
        Log.i(TAG,"All Views were successfully updated");

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); // Убираем заголовок
        setContentView(R.layout.main);

        AppAdapter.updateAppList(this); // Обновляем список приложений
        setLastReaded(); // Считываем последнюю считанную книгу
        ViewFactory.setContext(this);

    }

    @Override
    public void onClick(View v)
    {
        Intent intent;
        dbLock = false;
        switch (v.getId())
        {
            case R.id.nowR:
                try {
                    startActivity(getPackageManager().getLaunchIntentForPackage("com.neverland.alreader"));
                }
                catch (Exception e) {
                    Toast.makeText(this,R.string.appnotstarted,Toast.LENGTH_SHORT);
                }
                break;
            case R.id.imgFM:
                intent = new Intent(this,FileManager.class);
                intent.setAction(FileManager.ActionFM);
                startActivity(intent);
                break;
            case R.id.imgSet:
                startActivity(new Intent(Settings.ACTION_SETTINGS));
                break;
            case R.id.imgApp:
                intent = new Intent(this,FileManager.class);
                intent.setAction(FileManager.ActionApps);
                startActivity(intent);
                break;
            case R.id.imgSync:
                startActivity(getPackageManager().getLaunchIntentForPackage("lysesoft.andsmb"));
                break;
            case R.id.imgLib:
                intent = new Intent(this,FileManager.class);
                intent.setAction(FileManager.ActionLibrary);
                startActivity(intent);
                break;

            default:
                Log.i(TAG,"Click by " + v.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        menu.add(0,1,0,"История");
        menu.add(0,2,0,"Обновить книгу");
        menu.add(0,3,0,"Пересканировать список приложений");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case 1:
                showDialog(1);
                break;
            case 2:
                setLastReaded();
            case 3:
                AppAdapter.updateAppList(this);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    String timeFormat(String key)
    {
        String result;

        long k = Integer.parseInt(key);
        result = (k % 60) + "с"; // секунды
        k /= 60;

        if (k > 0) // минуты
        {
            result = (k % 60) + "м:" + result;
            k /= 60;
        }
        if ( k > 0)
        {
            result = k + "ч:" + result;
        }
        return result;
    }

    private String getHistory()
    {
        String result = "";
        Hashtable hist = new Hashtable();

        Cursor c = getContentResolver().query(
                        Uri.parse("content://" + PROVIDER_AUTHORITY + "/" + DB_HISTORY),
                new String[]{"MD5", "StartTime", "EndTime"},null,null,null);
        if (c != null)
        {
            long k = 0, st, et;
            String s;
            c.moveToFirst();
            do
            {
                s = c.getString(0);
                if (hist.containsKey(s))
                {
                    k = Long.parseLong((String)hist.get(s));
                }
                else k = 0;
                st = c.getLong(1);
                et = c.getLong(2);
                k += (et - st)/1000;
                hist.put(s,String.valueOf(k));
            } while (c.moveToNext());

            String[] proj = {"MD5","Name","Title","Authors","Location","Progress"};
            String title = null;
            c.close();
            c = getContentResolver().query(
                    Uri.parse("content://" + PROVIDER_AUTHORITY + "/" + DB_METADATA),
                    proj,
                    "LastAccess is not null AND LastAccess != 0",null,
                    "LastAccess DESC");
            if (c != null)
            {
                c.moveToFirst();
                do
                {
                    String md5 = c.getString(0);
                    if (hist.containsKey(md5))
                    {
                        title = c.getString(2);
                        if (title == null)
                        {
                            title = c.getString(1);
                        }
                        result += c.getString(3) + " - " + title + " (" + c.getString(5) + "): " + timeFormat((String)hist.get(c.getString(0))) + "\n";
                    }
                }while (c.moveToNext());
            }

        }
        c.close();
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

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode != KeyEvent.KEYCODE_BACK)
        {
            return super.onKeyDown(keyCode,event);
        }
        if (!dbLock)
        {
            setLastReaded();
            dbLock = true;
        }
        return true;
    }

}
