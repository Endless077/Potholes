package com.example.potholes.View;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.potholes.Model.Pothole;
import com.example.potholes.Presenter.HomePagePresenter;
import com.example.potholes.R;

import java.util.ArrayList;
import java.util.List;

public class PotholesAdapter extends RecyclerView.Adapter<PotholesAdapter.PotholesViewHolder>{

    List<Pothole> potholesList;
    Context context;
    HomePagePresenter mHomePagePresenter;

    public PotholesAdapter(Context ctx, List<Pothole> lst, HomePagePresenter mHomePagePresenter){
        this.context = ctx;
        this.potholesList = lst;
        this.mHomePagePresenter = mHomePagePresenter;
    }

    public void clearList() {
        potholesList.clear();
    }

    public void add(Pothole p) {
        potholesList.add(p);
    }

    public void remove(int i) {
        potholesList.remove(i);
    }

    public List<Pothole> getListPothole() {
        return potholesList;
    }

    public void setListCompilation(List<Pothole> listPothole) {
        this.potholesList = listPothole;
    }

    @NonNull
    @Override
    public PotholesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.potholes_row, parent, false);

        return new PotholesAdapter.PotholesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PotholesViewHolder holder, int position) {
        holder.indirizzoPothole.setText(potholesList.get(holder.getAdapterPosition()).getIndirizzo());
        holder.coordinate.setText(potholesList.get(holder.getAdapterPosition()).getLatLong());
        holder.username.setText(potholesList.get(holder.getAdapterPosition()).getUsername());
    }

    @Override
    public int getItemCount() {
        return potholesList.size();
    }

    public class PotholesViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout mainLayout;
        TextView indirizzoPothole;
        TextView username;
        TextView coordinate;

        public PotholesViewHolder(@NonNull View itemView) {
            super(itemView);
            indirizzoPothole = itemView.findViewById(R.id.textViewIndirizzoPotholeCard);
            username = itemView.findViewById(R.id.textViewUsernameCard);
            coordinate = itemView.findViewById(R.id.textViewCoordinateCard);
            mainLayout = itemView.findViewById(R.id.mainLayoutPotholesRow);
        }
    }
}
