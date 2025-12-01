package tasnimghanmi.grp1.findmyfriend.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import tasnimghanmi.grp1.findmyfriend.Position;
import tasnimghanmi.grp1.findmyfriend.R;

public class PositionAdapter extends RecyclerView.Adapter<PositionAdapter.Holder> {

    private final ArrayList<Position> list;
    private final OnPositionClick listener;

    public interface OnPositionClick {
        void onClick(Position p);
    }

    public PositionAdapter(ArrayList<Position> list, OnPositionClick listener) {
        this.list = list;
        this.listener = listener;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        TextView tvPseudo, tvCoords;

        public Holder(@NonNull View itemView) {
            super(itemView);
            tvPseudo = itemView.findViewById(R.id.tvPseudo);
            tvCoords = itemView.findViewById(R.id.tvCoords);
        }
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_position, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Position p = list.get(position);

        holder.tvPseudo.setText(p.getPseudo());
        holder.tvCoords.setText("Lat: " + p.getLatitude() + " | Lon: " + p.getLongitude());

        holder.itemView.setOnClickListener(v -> listener.onClick(p));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
