package com.example.exmap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.MyViewHolder>{

    Context context;
    List<TransactionModel> transactionModelArrayList;
    private Dialog dialog;

    public interface Dialog{
        void onClick(int pos);
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public TransactionAdapter (Context context, List<TransactionModel> transactionModelArrayList){
        this.context = context;
        this.transactionModelArrayList = transactionModelArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_view, null);
        return new MyViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        String type = transactionModelArrayList.get(position).getType();

        if (type.equals("Expense")){
            holder.amountTrans.setTextColor(ContextCompat.getColor(context, R.color.scholar));
        } else {
            holder.amountTrans.setTextColor(ContextCompat.getColor(context, R.color.hijaupastel));
        }

        holder.amountTrans.setText(transactionModelArrayList.get(position).getAmountTrans());
        holder.category.setText(transactionModelArrayList.get(position).getCategory());
        holder.date.setText(transactionModelArrayList.get(position).getDate());
        holder.notes.setText(transactionModelArrayList.get(position).getNote());
        Picasso.get().load(transactionModelArrayList.get(position).getReceipt()).into(holder.receipt);
    }

    @Override
    public int getItemCount() {
        return transactionModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView date, category,notes, amountTrans;
        ImageView receipt, more;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            category = itemView.findViewById(R.id.categList);
            amountTrans = itemView.findViewById(R.id.amount);
            notes = itemView.findViewById(R.id.notes);
            receipt = itemView.findViewById(R.id.receipt);
            more = itemView.findViewById(R.id.more);
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dialog!=null){
                        dialog.onClick(getLayoutPosition());
                    }
                }
            });
        }
    }
}
