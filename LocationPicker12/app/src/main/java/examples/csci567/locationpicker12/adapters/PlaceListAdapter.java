package examples.csci567.locationpicker12.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import examples.csci567.locationpicker12.PlaceItem;
import examples.csci567.locationpicker12.R;

/**
 * Created by bryandixon on 3/3/16.
 */
public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.ViewHolder> {

    private OnItemClickListener mOnItemClickListener;
    private final LayoutInflater mInflater;
    private ArrayList<PlaceItem> mData = new ArrayList<>();

    public static interface OnItemClickListener {
        public void onItemClick(PlaceItem item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_name)
        public TextView name;

        @Bind(R.id.item_latlon)
        public TextView latlon;

        private PlaceItem mPlaceItem;


        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(v);
            v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mPlaceItem);
                    }
                }
            });
        }

        public void bind(PlaceItem item) {
            name.setText(item.getPlace().getName());
            latlon.setText(item.getPlace().getLatLng().toString());
            //update local vars
        }
    }

    public PlaceListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);

    }

    @Override
    public PlaceListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.location_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(PlaceListAdapter.ViewHolder holder, int position) {
        holder.bind(mData.get(position));
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void setData(List<PlaceItem> data) {
        mData.clear();
        mData.addAll(data);
    }

    public void addData(PlaceItem item) {
        mData.add(item);
    }

    public void removeData(PlaceItem item) {
        if(mData.contains(item)) {
            mData.remove(item);
        }
    }

    public ArrayList<PlaceItem> getData() {
        return mData;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
