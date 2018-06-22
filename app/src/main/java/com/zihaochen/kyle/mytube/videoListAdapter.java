package com.zihaochen.kyle.mytube;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class videoListAdapter extends RecyclerView.Adapter <videoListAdapter.ViewHolder> {
    private List<String> mData;
    private Context context;
    private List<Drawable> mThumb;
    private List<Drawable> mlike;
    private List<Drawable> mSubscribe;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private RecyclerView recyclerView;
    private Context loginActivity;
    private static RecyclerViewClickListener itemListener;

    // data is passed into the constructor
    public videoListAdapter(Context context, List<String> data , List<Drawable> thumbnail, List<Drawable> likee, List<Drawable> sub) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mThumb = thumbnail;
        this.mlike = likee;
        this.mSubscribe = sub;
        //this.loginActivity = context;
        this.context = context;
        //this.itemListener = itemListener;
    }
    // binds the data to the TextView in each row
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.listitem, parent, false);
        return new ViewHolder(view);
    }
    public void onBindViewHolder(ViewHolder holder, int position) {
        String title = mData.get(position);
        Drawable thumbImage = mThumb.get(position);
        Drawable likeImage = mlike.get(position);
        Drawable subImage = mSubscribe.get(position);

        holder.like.setBackground(likeImage);
        holder.subscribe.setBackground(subImage);
        holder.myTextView.setText(title);
        holder.myImageView.setBackground(thumbImage);
        holder.myTextView.setTag(position);
        holder.myImageView.setTag(position);
    }



    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        ImageView like;
        ImageView subscribe;
        ImageView myImageView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.videoTitle);
            myImageView = itemView.findViewById(R.id.thumdnailImage);
            like = itemView.findViewById(R.id.like);
            subscribe = itemView.findViewById(R.id.subscription);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    // get position
                    int pos = getAdapterPosition();
                    Log.d("hi","position is" + pos);
                    // check if item still exists
                    if(pos != RecyclerView.NO_POSITION){
                        ((Login)loginActivity).startPlay(pos);
                    }
                }
            });
        }

        @Override
        public void onClick(View view) {
            itemListener.recyclerViewListClicked(view,this.getLayoutPosition());
            /*
            int position = getAdapterPosition();
            if(context instanceof Login){
                ((Login)loginActivity).startPlay(position);
            }
            */
        }
    }


    @Override
    public int getItemCount() {
        return  mData.size();
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


}
