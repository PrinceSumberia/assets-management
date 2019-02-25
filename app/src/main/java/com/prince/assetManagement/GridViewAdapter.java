package com.prince.assetManagement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridViewAdapter extends BaseAdapter {
    private Context mContext;
    private String[] txt;
    private Integer[] img_id;

    GridViewAdapter(Context mContext, String[] txt, Integer[] img_id) {
        this.mContext = mContext;
        this.txt = txt;
        this.img_id = img_id;
    }


    @Override
    public int getCount() {
        return txt.length;
//        return 8;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder viewHolder = null;

        if (view == null) {
            viewHolder = new ViewHolder();
            view = layoutInflater.inflate(R.layout.grid_layout, null);
            view.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.title = view.findViewById(R.id.textView);
        viewHolder.image = view.findViewById(R.id.imageView);

        // Using an AsyncTask to load the slow images in a background thread
        new AsyncTask<ViewHolder, Void, Integer>() {
            private ViewHolder v;

            @Override
            protected Integer doInBackground(ViewHolder... params) {
                v = params[0];
                if (isCancelled()) {
                    return null;
                }
                return img_id[i];
            }

            @Override
            protected void onPostExecute(Integer result) {
                super.onPostExecute(result);
                v.image.setImageResource(result);
                v.title.setText(txt[i]);

            }
        }.execute(viewHolder);


        //Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(),
        //  img_id[i]);
        //viewHolder.image.setImageResource(img_id[i]);
        //viewHolder.image.setImageBitmap(icon);
//        viewHolder.title.setText(txt[i]);


        return view;
    }

    private static class ViewHolder {
        ImageView image;
        TextView title;
    }
}

