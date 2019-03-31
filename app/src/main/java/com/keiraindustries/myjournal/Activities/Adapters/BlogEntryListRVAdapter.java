package com.keiraindustries.myjournal.Activities.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.keiraindustries.myjournal.Activities.BlogEntryListActivity;
import com.keiraindustries.myjournal.Activities.BlogPostView;
import com.keiraindustries.myjournal.Model.Blog;
import com.keiraindustries.myjournal.R;
import com.keiraindustries.myjournal.Data.JournalData;

import java.util.Date;
import java.util.List;

/**
 * Created by keira on 1/5/19.
 */

public class BlogEntryListRVAdapter extends RecyclerView.Adapter<BlogEntryListRVAdapter.MyViewHolder> {

    private List<Blog> blogList;
    private Context context;
    public BlogEntryListActivity blogListAct;

    public BlogEntryListRVAdapter(BlogEntryListActivity blogListAct) {
        this();
        this.blogList = JournalData.getInstance().getBlogList();
        this.context = blogListAct;
        this.blogListAct = blogListAct;
    }

    public BlogEntryListRVAdapter() {
        super();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.blog_list_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.blog = blogList.get(position);

        holder.tvEntryTitle.setText(holder.blog.getTitle());
        holder.tvEntryDate.setText(JournalData.getInstance().getDateFormat().format(new Date(holder.blog.getEntryDate())));
        holder.itemView.setBackgroundColor(Color.argb(0,0,0,0));

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                blogListAct.deletePopup(holder, v);
                return false;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(context, BlogPostView.class);
                intent.putExtra(JournalData.BLOGIDNUM, holder.getAdapterPosition());
                context.startActivity(intent);

            }
        });

    }


    @Override
    public int getItemCount() {
        return blogList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView tvEntryTitle;
        public TextView tvEntryDate;
        public Blog blog;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvEntryTitle = itemView.findViewById(R.id.tvEntryCardTitle);
            tvEntryDate = itemView.findViewById(R.id.tvEntryCardDate);

        }

    }


}
