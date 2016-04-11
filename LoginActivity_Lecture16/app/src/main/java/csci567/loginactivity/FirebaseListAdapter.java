package csci567.loginactivity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bryandixon on 4/10/16.
 */
public class FirebaseListAdapter extends RecyclerView.Adapter<FirebaseListAdapter.ViewHolder> {

    private OnItemClickListener mOnItemClickListener;
    private final LayoutInflater mInflater;
    private ArrayList<FirebaseItem> mData = new ArrayList<>();

    public static interface OnItemClickListener {
        public void onItemClick(FirebaseItem item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mItemTitle;
        private FirebaseItem mCloudantItem;

        public ViewHolder(View v) {
            super(v);
            mItemTitle = (TextView) v.findViewById(R.id.firebase_item);
            v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mCloudantItem);
                    }
                }
            });
        }

        public void bind(FirebaseItem item) {
            mCloudantItem = item;
            mItemTitle.setText(item.getMessage());
        }
    }

    public FirebaseListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public FirebaseListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.firebase_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(FirebaseListAdapter.ViewHolder holder, int position) {
        holder.bind(mData.get(position));
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void setData(List<FirebaseItem> data) {
        mData.clear();
        mData.addAll(data);
    }

    public void addData(FirebaseItem item) {
        mData.add(item);
    }

    public void removeData(FirebaseItem item) {
        if(mData.contains(item)) {
            mData.remove(item);
        }
    }

    public void removeAll(){
        mData.clear();
    }

    public ArrayList<FirebaseItem> getData() {
        return mData;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
