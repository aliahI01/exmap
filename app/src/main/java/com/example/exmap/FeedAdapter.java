package com.example.exmap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class FeedAdapter extends FirestoreRecyclerAdapter<Feed, FeedAdapter.FeedViewHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    Context context;

    public FeedAdapter(@NonNull FirestoreRecyclerOptions<Feed> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull FeedViewHolder holder, int position, @NonNull Feed feed) {
        holder.tv_problem.setText(feed.problem);
        holder.tv_explanation.setText(feed.explanation);
        holder.tv_timestamp.setText(Utility.timeToString(feed.timestamp));
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_view, null);
        return new FeedViewHolder(itemview);
    }

    class FeedViewHolder extends RecyclerView.ViewHolder{

        TextView tv_problem, tv_explanation, tv_timestamp;
        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_problem = itemView.findViewById(R.id.tv_problem);
            tv_explanation = itemView.findViewById(R.id.tv_explanation);
            tv_timestamp = itemView.findViewById(R.id.tv_timestamp);
        }
    }
}
