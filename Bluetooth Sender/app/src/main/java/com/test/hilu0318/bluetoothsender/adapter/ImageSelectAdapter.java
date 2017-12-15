package com.test.hilu0318.bluetoothsender.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.hilu0318.bluetoothsender.R;
import com.test.hilu0318.bluetoothsender.domain.ImageInfo;

import java.util.List;

/**
 * Created by hilu0 on 2017-11-28.
 */

public class ImageSelectAdapter extends ArrayAdapter<ImageInfo> {
    public ImageSelectAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<ImageInfo> data) {
        super(context, resource, textViewResourceId, data);
    }

    class ViewHolder{
        TextView filenameTv;
        TextView filesizeTv;
        ImageView imageViewIv;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view =  super.getView(position, convertView, parent);
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        if(viewHolder == null){
            viewHolder = new ViewHolder();
            viewHolder.filenameTv = (TextView)view.findViewById(R.id.ad_img_filename);
            viewHolder.filesizeTv = (TextView)view.findViewById(R.id.ad_img_filesize);
            viewHolder.imageViewIv = (ImageView)view.findViewById(R.id.ad_img_view);
            view.setTag(viewHolder);
        }

        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inSampleSize = 4;

        Bitmap bm = BitmapFactory.decodeFile(getItem(position).getFilepath(), opt);

        viewHolder.filenameTv.setText(getItem(position).getFilename());
        viewHolder.filesizeTv.setText(makeFilesize(getItem(position).getFilesize()));
        viewHolder.imageViewIv.setImageBitmap(rotateBitmap(bm, getItem(position).getOrientation()));
        viewHolder.imageViewIv.setScaleType(ImageView.ScaleType.CENTER_CROP);

        return view;
    }

    private Bitmap rotateBitmap(Bitmap src, int dg){
        Matrix matrix = new Matrix();
        switch (dg){
            case 0:{
                matrix.postRotate(dg);
                break;
            }
            case 90:{
                matrix.postRotate(90);
                break;
            }
            case 180:{
                matrix.postRotate(180);
                break;
            }
            case 270:{
                matrix.postRotate(270);
                break;
            }
        }
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    public String makeFilesize(int filesize){
        float result = Math.round(((float)filesize/10000f))/100f;
        return result+"MB";
    }
}
