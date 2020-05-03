package com.example.covid_19alertapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.covid_19alertapp.R;
import com.example.covid_19alertapp.extras.Constants;
import com.example.covid_19alertapp.extras.DateTimeHandler;
import com.example.covid_19alertapp.models.Comment;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class CommentFeedAdapter extends RecyclerView.Adapter<CommentFeedAdapter.MyViewHolder> {


    SharedPreferences sharedPreferences;
    Context context;
    ArrayList<Comment> commetList;
    public CommentFeedAdapter(Context context,ArrayList<Comment> commetList)
    {
         this.commetList = commetList;
         this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.comment_view, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.comment_body.setText(commetList.get(position).getComment_text());

        String date = DateTimeHandler.DateToday();
        if(commetList.get(position).getComment_date().equals(date)) { date = "Today"; }
        String date_time = date+ ", "+ commetList.get(position).getComment_time();
        holder.dateTime.setText(date_time);

        if(commetList.get(position).getUser_id().equals(FeedAdapter.POST.getUserID()))
        {
            holder.auth_tag.setVisibility(View.VISIBLE);
            holder.line.setBackgroundColor(ContextCompat.getColor(context, R.color.color_item_view_relief_line));
        }


        sharedPreferences = context.getSharedPreferences(Constants.USER_INFO_SHARED_PREFERENCES,MODE_PRIVATE);
        String user_ID = sharedPreferences.getString(Constants.uid_preference,null);

        if(user_ID.equals(commetList.get(position).getUser_id()))
        {
            holder.comment_author.setText("You");
        }
        else
        {
            holder.comment_author.setText(commetList.get(position).getUser_name());

        }
    }

    @Override
    public int getItemCount() {
        return commetList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView dateTime, comment_body, comment_author,auth_tag;
        RecyclerView line;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTime  = itemView.findViewById(R.id.textView_DatenTime_comment_view);
            comment_author = itemView.findViewById(R.id.textView_username_comment_view);
            comment_body = itemView.findViewById(R.id.comment_Text_comment_view);
            line = itemView.findViewById(R.id.line_comment_view);
            auth_tag = itemView.findViewWithTag(R.id.comment_view_author);
        }
    }
}
