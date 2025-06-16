package pl.edu.wat.am.project.zenivalut.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pl.edu.wat.am.project.zenivalut.R;
import pl.edu.wat.am.project.zenivalut.model.LastTransactionsDTO;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    public interface OnDeleteClick {
        void onDelete(long id);
    }

    private final List<LastTransactionsDTO> transactions;
    private final OnDeleteClick onDeleteClick;

    public TransactionAdapter(List<LastTransactionsDTO> transactions, OnDeleteClick onDeleteClick) {
        this.transactions = transactions;
        this.onDeleteClick = onDeleteClick;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, amount, type;
        Button deleteButton;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.titleText);
            amount = view.findViewById(R.id.amountText);
            type = view.findViewById(R.id.typeText);
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
        LastTransactionsDTO tx = transactions.get(position);
        holder.title.setText(tx.getTransactionTitle());
        holder.amount.setText(String.valueOf(tx.getAmount()));
        holder.type.setText(tx.getType());

        holder.deleteButton.setOnClickListener(v -> onDeleteClick.onDelete(tx.getId()));
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }
}
