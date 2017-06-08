package org.readium.sample.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.readium.sample.R;

import org.readium.r2_streamer.model.publication.link.Link;

import java.util.List;

/**
 * Created by Shrikant Badwaik on 24-Feb-17.
 */

public class SpineListAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<Link> list;
    private TextView view_1;

    public SpineListAdapter(Context context, List<Link> list) {
        super(context, 0);
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout layout = null;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = (LinearLayout) layoutInflater.inflate(R.layout.spinelist_adapter_resource, null);
        } else {
            layout = (LinearLayout) convertView;
        }

        view_1 = (TextView) layout.findViewById(R.id.spineTextView);

        Link link = list.get(position);
        view_1.setText(link.getHref());

        return layout;
    }
}
