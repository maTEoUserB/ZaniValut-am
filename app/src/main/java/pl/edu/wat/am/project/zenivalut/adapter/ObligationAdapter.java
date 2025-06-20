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
import pl.edu.wat.am.project.zenivalut.model.ObligationData;

public class ObligationAdapter extends RecyclerView.Adapter<ObligationAdapter.ViewHolder> {
public interface OnUpdateClick {
    void onUpdate(long id);
}

private final List<ObligationData> obligations;
private final ObligationAdapter.OnUpdateClick onUpdateClick;
private final Context context;

public ObligationAdapter(Context context, List<ObligationData> obligations, ObligationAdapter.OnUpdateClick onUpdateClick) {
    this.context = context;
    this.obligations = obligations;
    this.onUpdateClick = onUpdateClick;
}

public static class ViewHolder extends RecyclerView.ViewHolder {
    TextView title, dateToPay, obligationAmount;
    Button updateButton;

    public ViewHolder(View view) {
        super(view);
        title = view.findViewById(R.id.titleText);
        dateToPay = view.findViewById(R.id.dateToPayText);
        obligationAmount = view.findViewById(R.id.obligationAmountText);
        updateButton = view.findViewById(R.id.updateButton);
    }
}

@Override
public ObligationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_obligation, parent, false);
    return new ObligationAdapter.ViewHolder(view);
}

@Override
public void onBindViewHolder(ObligationAdapter.ViewHolder holder, int position) {
    ObligationData tx = obligations.get(position);
    holder.title.setText(tx.getObligationTitle());
    holder.dateToPay.setText(context.getString(R.string.deadline) + " " + String.valueOf(tx.getDateToPay()));
    holder.obligationAmount.setText(context.getString(R.string.obligation_amount) + " " + String.valueOf(tx.getObligationAmount()));

    holder.updateButton.setOnClickListener(v -> onUpdateClick.onUpdate(tx.getId()));
}

@Override
public int getItemCount() {
    return obligations.size();
}
}
