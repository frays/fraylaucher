package com.fray.launcher;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.cms.OnyxCmsCenter;
import com.onyx.android.sdk.data.cms.OnyxLibraryItem;
import com.onyx.android.sdk.data.cms.OnyxMetadata;
import com.onyx.android.sdk.data.cms.OnyxThumbnail;
import com.onyx.android.sdk.data.util.RefValue;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ray on 03.08.2014.
 */
public class LastsFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.lastsfragment, container, false);

        return v;
    }

    static class ViewHolder {
        TextView tv;
        ImageView iv;
    }

    @Override
    public void onResume() {
        super.onResume();
        ArrayList<OnyxLibraryItem> mLibraryItems = new ArrayList<OnyxLibraryItem>();
        OnyxCmsCenter.getLibraryItems(getActivity(), SortOrder.CreationTime, null, "fb2,zip,epub", 5, mLibraryItems);

        LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.lastReadedLayout);
        layout.removeAllViewsInLayout();
        ViewHolder holder;

        for (OnyxLibraryItem item : mLibraryItems) {
            OnyxMetadata metadata = OnyxCmsCenter.getMetadata(getActivity(), item.getPath());
            View v = getActivity().getLayoutInflater().inflate(R.layout.file_item, null);
            holder = new ViewHolder();
            holder.tv = (TextView) v.findViewById(R.id.lib_fname);
            holder.iv = (ImageView) v.findViewById(R.id.lib_fimage);

            if (metadata == null) {
                holder.tv.setText(item.getName());
                holder.iv.setId(R.drawable.book);
            } else {
                holder.tv.setText(metadata.getTitle());

                RefValue<Bitmap> btmp = new RefValue<Bitmap>();
                OnyxCmsCenter.getThumbnail(getActivity(), metadata, OnyxThumbnail.ThumbnailKind.Large, btmp);
                if (btmp.getValue() == null)
                    holder.iv.setImageResource(R.drawable.book);
                else
                    holder.iv.setImageBitmap(btmp.getValue());
            }
            v.setTag(item.getPath());
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    File f = new File((String)view.getTag());
                    intent.setDataAndType(Uri.fromFile(f), FrayTools.get_mime_by_filename(f.getName()));
                    try {
                        getActivity().startActivity(intent);
                    }
                    catch (Exception e) {
                        Toast.makeText(getActivity(), "Can't start application", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            layout.addView(v);
        }

    }
}
