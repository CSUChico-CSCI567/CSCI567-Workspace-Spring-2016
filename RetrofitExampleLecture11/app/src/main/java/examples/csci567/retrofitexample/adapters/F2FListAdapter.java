package examples.csci567.retrofitexample.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import examples.csci567.retrofitexample.MainActivity;
import examples.csci567.retrofitexample.R;
import examples.csci567.retrofitexample.RecipeItem;

/**
 * Created by bryandixon on 6/13/15.
 */
public class F2FListAdapter extends RecyclerView.Adapter<F2FListAdapter.ViewHolder> {

    private OnItemClickListener mOnItemClickListener;
    private final LayoutInflater mInflater;
    private ArrayList<RecipeItem> mData = new ArrayList<>();

    public static interface OnItemClickListener {
        public void onItemClick(RecipeItem item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mItemTitle;
        public ImageView mImageView;
        private RecipeItem mRecipeItem;

        public ViewHolder(View v) {
            super(v);
            mItemTitle = (TextView) v.findViewById(R.id.item_title);
            mImageView = (ImageView) v.findViewById(R.id.image_url);
            v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mRecipeItem);
                    }
                }
            });
        }

        public void bind(RecipeItem item) {
            mRecipeItem = item;
            mItemTitle.setText(item.getTitle());
            Uri uri = Uri.parse(item.getImageURL());
            Context context = MainActivity.context;
            //Code to convert dp to pixels
            int dp = 250;
            Resources r = context.getResources();
            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
            int pxi = Math.round(px);
            //Use converted dp to resize picture for image view to that size
            Picasso.with(context)
                    .load(uri)
                    .resize(pxi, pxi)
                    .centerCrop()
                    .into(mImageView);
        }
    }

    public F2FListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public F2FListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.recipe_item, parent, false));
    }

    @Override
    public void onBindViewHolder(F2FListAdapter.ViewHolder holder, int position) {
        holder.bind(mData.get(position));
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void setData(List<RecipeItem> data) {
        mData.clear();
        mData.addAll(data);
    }

    public void addData(RecipeItem item) {
        mData.add(item);
    }

    public void removeData(RecipeItem item) {
        if(mData.contains(item)) {
            mData.remove(item);
        }
    }

    public ArrayList<RecipeItem> getData() {
        return mData;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
