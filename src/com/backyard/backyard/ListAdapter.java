package com.backyard.backyard;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListAdapter extends ArrayAdapter<Report> {

public ListAdapter(Context context, int textViewResourceId) {
    super(context, textViewResourceId);
    // TODO Auto-generated constructor stub
}

private List<Report> items;

public ListAdapter(Context context, int resource, List<Report> items) {

    super(context, resource, items);

    this.items = items;

}

@Override
public View getView(int position, View convertView, ViewGroup parent) {

    View v = convertView;

    if (v == null) {

        LayoutInflater vi;
        vi = LayoutInflater.from(getContext());
        v = vi.inflate(R.layout.reportslist, null);

    }

    Report p = items.get(position);

    if (p != null) {

        TextView tt = (TextView) v.findViewById(R.id.id);
        TextView tt1 = (TextView) v.findViewById(R.id.categoryId);
        TextView tt3 = (TextView) v.findViewById(R.id.description);

        /*if (tt != null) {
            tt.setText(p._id);
        }*/
        if (tt1 != null) {

            tt1.setText(p.getSector());
        }
        if (tt3 != null) {

            tt3.setText(p.getTime());
        }
    }

    return v;

}
}
