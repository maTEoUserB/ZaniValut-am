package pl.edu.wat.am.project.zenivalut.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pl.edu.wat.am.project.zenivalut.R;
import pl.edu.wat.am.project.zenivalut.model.TransactionsListData;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    public interface OnDeleteClick {
        void onDelete(long id);
    }

    private final List<TransactionsListData> transactions;
    private final OnDeleteClick onDeleteClick;

    public TransactionAdapter(List<TransactionsListData> transactions, OnDeleteClick onDeleteClick) {
        this.transactions = transactions;
        this.onDeleteClick = onDeleteClick;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, amount, description;
        Button deleteButton;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.titleText);
            amount = view.findViewById(R.id.amountText);
            description = view.findViewById(R.id.descriptionText);
            deleteButton = view.findViewById(R.id.deleteButton);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TransactionsListData tx = transactions.get(position);
        holder.title.setText(tx.getTransactionTitle());
        holder.amount.setText(String.valueOf(tx.getAmount()));
        holder.description.setText(tx.getTransactionDescription());

        holder.deleteButton.setOnClickListener(v -> onDeleteClick.onDelete(tx.getId()));
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }
}
