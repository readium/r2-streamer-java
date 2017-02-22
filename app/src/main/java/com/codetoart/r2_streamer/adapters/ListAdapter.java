package com.codetoart.r2_streamer.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codetoart.r2_streamer.R;
import com.codetoart.r2_streamer.model.searcher.SearchResult;

import java.util.List;

/**
 * Created by Shrikant Badwaik on 17-Feb-17.
 */

public class ListAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<SearchResult> list;
    private TextView view_1, view_2;

    public ListAdapter(Context context, List<SearchResult> list) {
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
            layout = (LinearLayout) layoutInflater.inflate(R.layout.list_adapter_resource, null);
        } else {
            layout = (LinearLayout) convertView;
        }

        view_1 = (TextView)layout.findViewById(R.id.titleText);
        view_2 = (TextView)layout.findViewById(R.id.matchingText);

        SearchResult searchResult = list.get(position);
        view_1.setText(searchResult.getTitle());
        view_2.setText(searchResult.getMatchString());

        return layout;
    }
}
