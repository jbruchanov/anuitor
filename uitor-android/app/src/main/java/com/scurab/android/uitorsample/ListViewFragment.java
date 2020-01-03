package com.scurab.android.uitorsample;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.scurab.android.uitorsample.common.BaseFragment;

/**
 * Created by jbruchanov on 26/06/2014.
 */
public class ListViewFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ListView lv = new ListView(inflater.getContext());
        lv.setAdapter(new SampleAdapter(getActivity()));
        return lv;
    }

    static class SampleAdapter extends BaseAdapter {

        private String[] mSamples;
        private Context mContext;

        public SampleAdapter(Context context) {
            mContext = context;
            mSamples = new String[3];
            mSamples[0] = context.getString(R.string.lorem_ipsum);
            mSamples[1] = context.getString(R.string.lorem_ipsum_short);
            mSamples[2] = context.getString(R.string.lorem_ipsum_long);
        }

        @Override
        public int getCount() {
            return 30;
        }

        @Override
        public Object getItem(int position) {
            return mSamples[position % mSamples.length];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Tag tag;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.list_item, null);
                tag = new Tag();
                convertView.setTag(tag);
                tag.order = convertView.findViewById(R.id.order);
                tag.data = convertView.findViewById(R.id.data);
            } else {
                tag = (Tag) convertView.getTag();
            }

            convertView.setTag(R.id.tag_position, position);
            tag.order.setText(String.valueOf(position + 1));
            tag.data.setText(mSamples[position % mSamples.length]);
            return convertView;
        }

        private static final class Tag {
            private TextView order;
            private TextView data;
        }
    }
}
