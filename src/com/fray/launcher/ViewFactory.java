package com.fray.launcher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.onyx.android.sdk.data.cms.OnyxLibraryItem;

import java.io.File;

/**
 * Created by fray on 06.06.14.
 */
public class ViewFactory {

    private static ViewFactory factory;
    private static Context context;
    private static LayoutInflater inflater;

    private ViewFactory(){}

    public static void setContext(Context ctx) {
        if (factory == null)
            factory = new ViewFactory();
        context = ctx;
        inflater = LayoutInflater.from(ctx);
    }

    public static ViewFactory Instace() {
        return factory;
    }

    static class ViewHolder {
        TextView tv;
        ImageView iv;
    }

    public static ViewType type = ViewType.GRID;

    private View getView(View convertView) {
        ViewHolder holder;
        View v = convertView;
        if (v == null) {
            switch (type) {
                case GRID:
                    v = inflater.inflate(R.layout.file_item, null);
                    break;
                default:
                    v = inflater.inflate(R.layout.elem, null);
            }

            if(v == null){
                return null;
            }
            holder = new ViewHolder();
            switch (type) {
                case GRID:
                    holder.tv = (TextView)v.findViewById(R.id.lib_fname);
                    holder.iv = (ImageView)v.findViewById(R.id.lib_fimage);
                    break;
                default:
                    holder.tv = (TextView) v.findViewById(R.id.app_name);
                    holder.iv = (ImageView) v.findViewById(R.id.app_icon);
            }
            v.setTag(holder);
        }

        return v;
    }

    public View getItem(View convertView, OnyxLibraryItem data) {
        View v = getView(convertView);

        ViewHolder h = (ViewHolder) v.getTag();
        h.tv.setText(data.getName());
        h.iv.setImageResource(R.drawable.book);

        return v;
    }

    public View getItem(View convertView, File item) {
        View v = getView(convertView);

        ViewHolder h = (ViewHolder) v.getTag();

        if (item != null) {
            h.tv.setText(item.getName());
            try {
                if (item.isDirectory()) {
                    h.iv.setImageResource(R.drawable.dir);
                }
                else if (item.getName().endsWith("fb2")) {
                    h.iv.setImageResource(R.drawable.fb2);
                }
                else if (item.getName().endsWith("txt")) {
                    h.iv.setImageResource(R.drawable.txt);
                }
                else
                    h.iv.setImageResource(R.drawable.file);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        return v;
    }

    public View getItem(View convertView, String item) {
        View v = getView(convertView);

        ViewHolder h = (ViewHolder) v.getTag();

        if (item != null) {
            String[] itemp = item.split("\\%");
            h.tv.setText(itemp[2]);
            try {
                h.iv.setImageDrawable(context.getPackageManager().getApplicationIcon(itemp[0]));
            }
            catch (Exception e){}
        }
        return v;
    }

}

enum ViewType {
    LIST, GRID
}
