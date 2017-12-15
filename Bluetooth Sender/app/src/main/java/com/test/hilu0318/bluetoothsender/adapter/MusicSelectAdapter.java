package com.test.hilu0318.bluetoothsender.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.test.hilu0318.bluetoothsender.R;
import com.test.hilu0318.bluetoothsender.domain.MusicInfo;

import java.util.List;

/**
 * Created by hilu0 on 2017-12-04.
 */

public class MusicSelectAdapter extends ArrayAdapter<MusicInfo> {

    public MusicSelectAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<MusicInfo> data) {
        super(context, resource, textViewResourceId, data);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        ViewHoler viewHoler = (ViewHoler)view.getTag();

        if(viewHoler == null){
            viewHoler = new ViewHoler();
            viewHoler.filenameTv = (TextView)view.findViewById(R.id.ad_ms_filename);
            viewHoler.filesizeTv = (TextView)view.findViewById(R.id.ad_ms_filesize);
            viewHoler.mimetypeTv = (TextView)view.findViewById(R.id.ad_ms_mimetype);
        }

        viewHoler.filenameTv.setText(getItem(position).getFilename());
        viewHoler.filesizeTv.setText(getMBsize(getItem(position).getFilesize()));
        viewHoler.mimetypeTv.setText(getItem(position).getMimetype());

        view.setTag(viewHoler);

        return view;
    }

    private String getMBsize(int filesize) {
        return Math.round((float)filesize/10000f)/100f + "MB";
    }

    private class ViewHoler{
        TextView filenameTv;
        TextView filesizeTv;
        TextView mimetypeTv;
    }
}
