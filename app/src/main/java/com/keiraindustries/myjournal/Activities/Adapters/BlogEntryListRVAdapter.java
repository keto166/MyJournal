package com.keiraindustries.myjournal.Activities.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    public BlogEntryListRVAdapter(Context context) {
        this();
        this.blogList = JournalData.getInstance().getBlogList();
        this.context = context;
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
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.blog = blogList.get(position);

        holder.tvEntryTitle.setText(holder.blog.getTitle());
        holder.tvEntryDate.setText(JournalData.getInstance().getDateFormat().format(new Date(holder.blog.getEntryDate())));
    }


    @Override
    public int getItemCount() {
        return blogList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvEntryTitle;
        public TextView tvEntryDate;
        public Blog blog;

        public MyViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            tvEntryTitle = itemView.findViewById(R.id.tvEntryCardTitle);
            tvEntryDate = itemView.findViewById(R.id.tvEntryCardDate);

        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, BlogPostView.class);
            intent.putExtra(JournalData.BLOGIDNUM, getAdapterPosition());
            context.startActivity(intent);
        }


    }


}
