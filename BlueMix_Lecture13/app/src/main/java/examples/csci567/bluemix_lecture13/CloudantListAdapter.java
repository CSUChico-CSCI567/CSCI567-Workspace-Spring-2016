package examples.csci567.bluemix_lecture13;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bryandixon on 3/20/16.
 */
public class CloudantListAdapter extends RecyclerView.Adapter<CloudantListAdapter.ViewHolder> {

    private OnItemClickListener mOnItemClickListener;
    private final LayoutInflater mInflater;
    private ArrayList<CloudantItem> mData = new ArrayList<>();

    public static interface OnItemClickListener {
        public void onItemClick(CloudantItem item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mItemTitle;
        private CloudantItem mCloudantItem;

        public ViewHolder(View v) {
            super(v);
            mItemTitle = (TextView) v.findViewById(R.id.cloudant_item);
            v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mCloudantItem);
                    }
                }
            });
        }

        public void bind(CloudantItem item) {
            mCloudantItem = item;
            mItemTitle.setText(item.getTitle());
        }
    }

    public CloudantListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public CloudantListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.cloudant_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(CloudantListAdapter.ViewHolder holder, int position) {
        holder.bind(mData.get(position));
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void setData(List<CloudantItem> data) {
        mData.clear();
        mData.addAll(data);
    }

    public void addData(CloudantItem item) {
        mData.add(item);
    }

    public void removeData(CloudantItem item) {
        if(mData.contains(item)) {
            mData.remove(item);
        }
    }

    public ArrayList<CloudantItem> getData() {
        return mData;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
