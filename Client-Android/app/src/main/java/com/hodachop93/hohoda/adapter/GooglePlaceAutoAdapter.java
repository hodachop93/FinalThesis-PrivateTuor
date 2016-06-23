package com.hodachop93.hohoda.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.model.GooglePlace;

import java.util.List;


public class GooglePlaceAutoAdapter extends BaseAdapter implements Filterable {

    private Activity mContext;
    private List<GooglePlace> places;

    public GooglePlaceAutoAdapter(Context context, List<GooglePlace> places) {
        this.mContext = (Activity) context;
        this.places = places;
    }

    public void clear() {
        if (places != null) {
            places.clear();
        }
    }

    public List<GooglePlace> getPlaces() {
        return places;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView tvTitle;
        convertView = mContext.getLayoutInflater().inflate(
                R.layout.item_google_place_search, parent, false);
        tvTitle = (TextView) convertView
                .findViewById(android.R.id.text1);

        String value = places.get(position).getPlaceName();
        tvTitle.setText(value);

        return convertView;
    }

    @Override
    public int getCount() {
        return places == null ? 0 : places.size();
    }

    @Override
    public GooglePlace getItem(int index) {
        return places.get(index);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    filterResults.values = places;
                    assert places != null;
                    filterResults.count = places.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                return ((GooglePlace) resultValue).getPlaceName();
            }
        };
    }
}
