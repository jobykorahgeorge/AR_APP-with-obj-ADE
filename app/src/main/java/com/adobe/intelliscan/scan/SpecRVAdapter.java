package com.adobe.intelliscan.scan;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adobe.intelliscan.R;

import java.util.ArrayList;

/**
 * Created by bento on 22/07/16.
 */
public class SpecRVAdapter extends RecyclerView.Adapter<SpecRVAdapter.ViewHolder> {
    private ArrayList<String> mSpecList;

    // Provide a suitable constructor (depends on the kind of dataset)
    public SpecRVAdapter(ArrayList<String> list) {
        mSpecList = list;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_spec_detail, parent, false);

        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final String spec = mSpecList.get(position);

        if (spec.startsWith("#")) {
            holder.mSpecTitle.setVisibility(View.VISIBLE);
            holder.specLayout1.setVisibility(View.GONE);
            holder.specLayout2.setVisibility(View.GONE);
            holder.separatorView.setVisibility(View.GONE);

            holder.mSpecTitle.setText(spec.substring(1));
        } else if (spec.contains(" - ")) {
            holder.mSpecTitle.setVisibility(View.GONE);
            holder.specLayout1.setVisibility(View.VISIBLE);
            holder.specLayout2.setVisibility(View.GONE);
            holder.separatorView.setVisibility(View.VISIBLE);

            String key = spec.split(" - ")[0];
            String value = spec.split(" - ")[1];

            holder.mSpecText1.setText(key);
            holder.mValueText.setText(value);
        }
        else{
            holder.mSpecTitle.setVisibility(View.GONE);
            holder.specLayout1.setVisibility(View.GONE);
            holder.specLayout2.setVisibility(View.VISIBLE);
            holder.separatorView.setVisibility(View.VISIBLE);

            holder.mSpecText2.setText(spec);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mSpecList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout specLayout1;
        LinearLayout specLayout2;
        TextView mSpecTitle;
        TextView mSpecText1;
        TextView mSpecText2;
        TextView mValueText;
        View separatorView;

        public ViewHolder(View itemView) {
            super(itemView);

            specLayout1 = (LinearLayout) itemView.findViewById(R.id.detail_type1);
            specLayout2 = (LinearLayout) itemView.findViewById(R.id.detail_type2);
            mSpecTitle = (TextView) itemView.findViewById(R.id.detail_title);
            mSpecText1 = (TextView) itemView.findViewById(R.id.detail_spec1);
            mSpecText2 = (TextView) itemView.findViewById(R.id.detail_spec2);
            mValueText = (TextView) itemView.findViewById(R.id.detail_value);
            separatorView = itemView.findViewById(R.id.detail_separator);
        }
    }

}
