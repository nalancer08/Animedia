package com.appbuilders.animedia.Adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.appbuilders.animedia.Core.Anime;
import com.appbuilders.animedia.R;
import com.appbuilders.surface.SfScreen;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Erick Sanchez - App Builders CTO
 * Revision 1 - 30/10/17
 */

public class AlphabetAdapter extends ArrayAdapter<String> {

    private Context context;
    private ArrayList<String> alphabet;

    public AlphabetAdapter(Context context, String[] alphabet) {

        super(context, R.layout.alphabet_adapter, new ArrayList<String>(Arrays.asList(alphabet)));
        this.context = context;
        this.alphabet = new ArrayList<String>(Arrays.asList(alphabet));
    }

    public AlphabetAdapter(Context context, ArrayList<String> alphabet) {

        super(context, R.layout.alphabet_adapter, alphabet);
        this.context = context;
        this.alphabet = alphabet;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String letter = getItem(position);

        ViewHolder holder = null;
        LayoutInflater inflater = LayoutInflater.from(this.context);
        // LayoutInflater inflater = getLayoutInflater(); // In activity

        convertView = null;

        if ( convertView == null ) {

            convertView = inflater.inflate(R.layout.alphabet_adapter, null, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

            holder.getAlphabetLetter().setText(letter);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    public class ViewHolder {

        private View row;
        private TextView alphabet_letter = null;

        public ViewHolder(View row) {
            this.row = row;
        }

        public TextView getAlphabetLetter() {

            if ( this.alphabet_letter == null ) {
                this.alphabet_letter = (TextView) row.findViewById(R.id.alphabet_letter);
                this.alphabet_letter.setHeight(SfScreen.getInstance(getContext()).getDpY(200));
                this.alphabet_letter.setMinimumHeight(SfScreen.getInstance(getContext()).getDpY(200));
            }
            return this.alphabet_letter;
        }
    }
}
