package com.fjamtechnology.friendlyreminder;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Franc on 5/3/2017.
 */

/**
 * adapter to populate list view
 */
public class CustomAdapter extends BaseAdapter {

    private final Activity context;
    private Context mContext;
    private String[]  Title;
    private int[] imge;

    /**
     * the adapter
     * @param context - context
     * @param text1  - text
     * @param imageIds
     */
    public CustomAdapter(Activity context, String[] text1,int[] imageIds) {
        this.context=context;
        Title = text1;
        imge = imageIds;

    }

    /**
     *
     * @return
     */
    public int getCount() {
        // TODO Auto-generated method stub
        return Title.length;
    }

    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater= context.getLayoutInflater();
        View row;
        row = inflater.inflate(R.layout.row, parent, false);
        TextView title;
        ImageView i1;
        i1 = (ImageView) row.findViewById(R.id.imgIcon);
        title = (TextView) row.findViewById(R.id.txtTitle);
        title.setText(Title[position]);
        i1.setImageResource(imge[position]);

        return (row);
    }
}
