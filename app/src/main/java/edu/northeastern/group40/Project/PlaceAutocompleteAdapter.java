package edu.northeastern.group40.Project;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.libraries.places.api.model.AutocompletePrediction;

import java.util.List;

public class PlaceAutocompleteAdapter extends ArrayAdapter<AutocompletePrediction> {


    public PlaceAutocompleteAdapter(Context context, List<AutocompletePrediction> predictions) {
        super(context, android.R.layout.simple_list_item_1, predictions);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                return new FilterResults();
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
            }
        };
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView textView = (TextView) super.getView(position, convertView, parent);
        AutocompletePrediction prediction = getItem(position);
        if (prediction != null) {
            textView.setText(prediction.getFullText(null));
        }
        return textView;
    }
}
