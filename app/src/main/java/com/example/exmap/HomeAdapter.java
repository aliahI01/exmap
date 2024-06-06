package com.example.exmap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class HomeAdapter extends FirestoreRecyclerAdapter<TransactionModel, HomeAdapter.HomeViewHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    Context context;

    public HomeAdapter(@NonNull FirestoreRecyclerOptions<TransactionModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull HomeAdapter.HomeViewHolder holder, int position, @NonNull TransactionModel model) {
        String type = model.getType();

        if (type.equals("Expense")){
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.scholar));
        } else {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.hijaupastel));
        }

        holder.tv_categ.setText(model.getCategory());
        holder.tv_amount.setText(model.getAmountTrans());
    }

    @NonNull
    @Override
    public HomeAdapter.HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.categ_view, null);
        return new HomeAdapter.HomeViewHolder(itemview);
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView tv_categ, tv_amount;
        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardview);
            tv_categ = itemView.findViewById(R.id.tv_categ);
            tv_amount = itemView.findViewById(R.id.tv_amount);
        }
    }
}
