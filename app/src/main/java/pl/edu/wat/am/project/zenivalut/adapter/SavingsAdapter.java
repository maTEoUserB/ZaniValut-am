package pl.edu.wat.am.project.zenivalut.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pl.edu.wat.am.project.zenivalut.R;
import pl.edu.wat.am.project.zenivalut.model.SavingsListData;

public class SavingsAdapter extends RecyclerView.Adapter<SavingsAdapter.ViewHolder> {
    public interface OnUpdateClick {
        void onUpdate(SavingsListData saving);
    }

    private final List<SavingsListData> savings;
    private final SavingsAdapter.OnUpdateClick onUpdateClick;
    private final Context context;

    public SavingsAdapter(Context context, List<SavingsListData> savings, OnUpdateClick onUpdateClick) {
        this.context = context;
        this.savings = savings;
        this.onUpdateClick = onUpdateClick;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, currentAmount, finalAmmount, deadline;
        Button updateButton;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.titleText);
            currentAmount = view.findViewById(R.id.currentAmountText);
            finalAmmount = view.findViewById(R.id.finalAmountText);
            deadline = view.findViewById(R.id.deadlineText);
            updateButton = view.findViewById(R.id.updateButton);
        }
    }

    @Override
    public SavingsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_savings, parent, false);
        return new SavingsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SavingsAdapter.ViewHolder holder, int position) {
        SavingsListData tx = savings.get(position);
        holder.title.setText(tx.getTitle());
        holder.currentAmount.setText(context.getString(R.string.current_amount) + " " + String.valueOf(tx.getCurrentAmount()));
        holder.finalAmmount.setText(context.getString(R.string.final_amount) + " " + String.valueOf(tx.getFinalAmmount()));
        holder.deadline.setText(context.getString(R.string.deadline) + " " + tx.getDeadline().toString());

        holder.updateButton.setOnClickListener(v -> onUpdateClick.onUpdate(tx));
    }

    @Override
    public int getItemCount() {
        return savings.size();
    }
}
