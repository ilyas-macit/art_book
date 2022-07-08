package com.ilyasmacit.art_book;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ilyasmacit.art_book.databinding.RecyclerRowBinding;

import java.util.ArrayList;

public class ArtAdapter extends RecyclerView.Adapter<ArtAdapter.ArtHolder>{
    ArrayList<Arts> artArrayList = null;

    public ArtAdapter(ArrayList<Arts> artArrayList) {
        this.artArrayList = artArrayList;
    }

    @Override
    public ArtHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerRowBinding binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent, false);
        return new ArtHolder(binding);
    }

    @Override
    public void onBindViewHolder( ArtHolder holder, int position) {

        holder.binding.recyclerViewText.setText(artArrayList.get(holder.getAdapterPosition()).name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), DetailsActivity.class);
                intent.putExtra("info", false);
                intent.putExtra("Id",artArrayList.get(holder.getAdapterPosition()).id);
                holder.itemView.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return artArrayList.size();
    }

    public class ArtHolder extends RecyclerView.ViewHolder{
        private RecyclerRowBinding binding;
        public ArtHolder(RecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
