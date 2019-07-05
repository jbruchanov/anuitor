package com.scurab.android.anuitorsample;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scurab.android.anuitorsample.common.BaseFragment;

/**
 * Created by JBruchanov on 20/02/2016.
 */
public class RecyclerViewFragment extends BaseFragment {

    private RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mRecyclerView = new RecyclerView(inflater.getContext());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(new SampleAdapter(getContext()));
    }

    static class SampleAdapter extends RecyclerView.Adapter<SampleViewHolder> {

        private String[] mSamples;
        private LayoutInflater mInflater;

        public SampleAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
            mSamples = new String[3];
            mSamples[0] = context.getString(R.string.lorem_ipsum);
            mSamples[1] = context.getString(R.string.lorem_ipsum_short);
            mSamples[2] = context.getString(R.string.lorem_ipsum_long);
        }

        @Override
        public SampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SampleViewHolder(mInflater.inflate(R.layout.card_view_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(SampleViewHolder holder, int position) {
            holder.mOrder.setText(String.valueOf(position + 1));
            holder.mData.setText(mSamples[position % mSamples.length]);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return 30;
        }
    }

    private static class SampleViewHolder extends RecyclerView.ViewHolder{
        final TextView mOrder;
        final TextView mData;

        public SampleViewHolder(View itemView) {
            super(itemView);
            mOrder = itemView.findViewById(R.id.order);
            mData = itemView.findViewById(R.id.data);
        }
    }
}
