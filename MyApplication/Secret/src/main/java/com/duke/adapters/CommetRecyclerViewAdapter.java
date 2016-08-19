package com.duke.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.duke.beans.Comment;
import com.duke.customview.CircleImageView;
import com.duke.secret.CommentActivity;
import com.duke.secret.R;

import java.util.List;


public class CommetRecyclerViewAdapter extends RecyclerView.Adapter<CommetRecyclerViewAdapter.MyViewHolder> {
    private List<Comment> comments;
    private CommentActivity act;


    public CommetRecyclerViewAdapter(List<Comment> comments, CommentActivity act) {
        this.comments = comments;
        this.act = act;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


    class MyViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView avatar;
        private TextView username, gender, time, content;
        private ImageView genderSign;

        public MyViewHolder(View view)
        {
            super(view);
            avatar = (CircleImageView) view.findViewById(R.id.item_comment_avatar);

            username = (TextView) view.findViewById(R.id.item_comment_nickname);
            gender = (TextView) view.findViewById(R.id.item_comment_gender);
            genderSign = (ImageView) view.findViewById(R.id.item_comment_gender_sign);
            time = (TextView) view.findViewById(R.id.item_comment_time);
            content = (TextView) view.findViewById(R.id.item_comment_content);
        }
    }
}