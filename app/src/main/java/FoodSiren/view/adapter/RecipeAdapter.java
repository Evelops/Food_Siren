package FoodSiren.view.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eml_listview_test3.R;

import java.util.ArrayList;

import FoodSiren.model.data.Recipe;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.CustomViewHolder> {

    private ArrayList<Recipe> mList;
    private Activity context;
    private OnItemClickListener mListener = null;

    public RecipeAdapter(Activity context, ArrayList<Recipe> list) {
        this.context = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_recipe, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {
        Glide.with(context).load(mList.get(position).getImage()).into(viewholder.image);
        viewholder.name.setText(mList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Recipe recipe);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView image;
        protected TextView name;

        public CustomViewHolder(View view) {
            super(view);
            this.image = view.findViewById(R.id.RecipeAdapter_iv_image);
            this.name = view.findViewById(R.id.RecipeAdapter_tv_name);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onItemClick(mList.get(pos));
                        }
                    }
                }
            });
        }
    }
}
