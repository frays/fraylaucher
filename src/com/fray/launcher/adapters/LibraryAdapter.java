package com.fray.launcher.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import com.fray.launcher.ViewFactory;
import com.onyx.android.sdk.data.AscDescOrder;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.cms.OnyxCmsCenter;
import com.onyx.android.sdk.data.cms.OnyxLibraryItem;

import java.util.ArrayList;

/**
 * Created by fray on 06.06.14.
 */
public class LibraryAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

    private OnyxLibraryItem[] data;
    private Context context;

    public LibraryAdapter(Context context) {
        this.context = context;
        ArrayList lst = new ArrayList();
        OnyxCmsCenter.getLibraryItems(context, SortOrder.CreationTime, AscDescOrder.Desc,"fb2,epub",100,lst);
        data = new OnyxLibraryItem[lst.size()];
        data = (OnyxLibraryItem[])lst.toArray(data);
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int i) {
        return data[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return ViewFactory.Instace().getItem(view,data[i]);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}