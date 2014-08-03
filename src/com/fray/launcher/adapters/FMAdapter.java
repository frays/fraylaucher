package com.fray.launcher.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.*;
import com.fray.launcher.FileManager;
import com.fray.launcher.R;
import com.fray.launcher.ViewFactory;
import com.fray.launcher.FrayTools;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

/**
 * Created by fray on 15.05.14.
 */

public class FMAdapter extends BaseAdapter implements AdapterView.OnItemClickListener, View.OnClickListener {
    private static File[] files;
    private static String path = "/mnt/storage/Books";

    private static File dir = new File(path);
    private Context context;

    void updateFileList() {
        files = dir.listFiles(LibFilter.getInstance());

        if (files != null) {
            Arrays.sort(files, new LibComparator());
            FileManager.changeDir(dir.getName());
            this.notifyDataSetInvalidated();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //Log.i(Main.TAG, "In ItemClick");
        if (files[i].isDirectory())
        {
            if (files[i].canRead()) {
                dir = files[i];
                updateFileList();
            }
            else  {
                Toast.makeText(context, R.string.hasnopermission, Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            if (files[i].exists())
            {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(files[i]), FrayTools.get_mime_by_filename(files[i].getName()));
                try {
                    context.startActivity(intent);
                }
                catch (Exception e) {
                    Toast.makeText(context,"Can't start application",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public FMAdapter(Context con)
    {
        super();
        this.context = con;
        try {
            updateFileList();
        }
        catch (Exception e) {
            dir = new File("/");
            updateFileList();
        }

    }


    @Override
    public int getCount()
    {
        if (files != null)
            return files.length;
        return 0;
    }

    @Override
    public long getItemId(int pos)
    {
        return pos;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        return ViewFactory.Instace().getItem(convertView,files[position]);
    }

    public String getItem(int pos)
    {
        return files[pos].getName();
    }

    @Override
    public void onClick(View v)
    {
        try {
            dir = dir.getParentFile();
            updateFileList();
        }
        catch (Exception e)
        {
            Toast toast = Toast.makeText(context,R.string.itsroot,Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
    }

}

class LibComparator implements java.util.Comparator<File> {
    public int compare(File a, File b) {
        if (a == null && b == null) {
            return 0;
        }
        if (a == null && b != null) {
            return 1;
        }
        if (a != null && b == null) {
            return -1;
        }

        if (a.isDirectory() && b.isFile())
            return -1;
        if (b.isDirectory() && a.isFile())
            return 1;
        return a.getName().compareToIgnoreCase(b.getName());
    }
}


class LibFilter implements FileFilter {

    private static LibFilter filter;

    private LibFilter(){}

    @Override
    public boolean accept(File pathname) {
        if (pathname != null)
            return !pathname.getName().startsWith(".");
        else
            return false;
    }

    static LibFilter getInstance(){
        if (filter == null)
            filter = new LibFilter();

        return filter;

    }
}
