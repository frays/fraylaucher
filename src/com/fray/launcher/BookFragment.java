package com.fray.launcher;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.fray.launcher.adapters.AppAdapter;
import com.onyx.android.sdk.data.cms.OnyxCmsCenter;
import com.onyx.android.sdk.data.cms.OnyxMetadata;
import com.onyx.android.sdk.data.cms.OnyxThumbnail;
import com.onyx.android.sdk.data.util.RefValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ray on 03.08.2014.
 */
public class BookFragment extends Fragment {

    private static String TAG = "BookFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bookfragment, container, false);

        LinearLayout l = (LinearLayout) v.findViewById(R.id.nowRead);
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startActivity(getActivity().getPackageManager().getLaunchIntentForPackage("com.neverland.alreader"));
                } catch (Exception e) {
                    Toast.makeText(getActivity(), R.string.appnotstarted, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }


    private void setLastReading() {
        List<OnyxMetadata> lst = new ArrayList<OnyxMetadata>();
        OnyxCmsCenter.getRecentReadings(getActivity(), lst);

        if (lst.size() != 0) {
            OnyxMetadata metadata = lst.get(0);
            TextView twTitle = (TextView) getActivity().findViewById(R.id.txtTitle);
            TextView twAuthor = (TextView) getActivity().findViewById(R.id.txtAuthor);

            if (metadata.getTitle() != null) {
                twTitle.setText(metadata.getTitle());
                twAuthor.setText(metadata.getAuthors().toString());
            } else {
                twTitle.setText(metadata.getName());
                twAuthor.setHeight(0);
            }

            TextView twRead = (TextView) getActivity().findViewById(R.id.txtNow);
            twRead.setText(getString(R.string.nowreading) + " (" + metadata.getProgress().toString() + "):");

            ImageView imgBook = (ImageView) getActivity().findViewById(R.id.imgBook);
            RefValue<Bitmap> btmp = new RefValue<Bitmap>();

            OnyxCmsCenter.getThumbnail(getActivity(), metadata, OnyxThumbnail.ThumbnailKind.Large, btmp);

            if (btmp.getValue() == null)
                imgBook.setImageResource(R.drawable.book);
            else
                imgBook.setImageBitmap(btmp.getValue());
            Log.i(TAG, "Image was loaded");

        }
        Log.i(TAG, "All Views were successfully updated");

    }

    @Override
    public void onResume() {
        super.onResume();
        AppAdapter.updateAppList(getActivity()); // Обновляем список приложений
        setLastReading(); // Считываем последнюю считанную книгу
    }
}
