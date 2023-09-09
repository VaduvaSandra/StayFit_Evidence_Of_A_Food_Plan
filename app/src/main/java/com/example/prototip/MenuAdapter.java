package com.example.prototip;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {
    private ArrayList<MenuItem> menuList;
    private OnAddButtonClickListener addButtonClickListener;
    private Context context;


    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public ImageButton addButton;


        public MenuViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.menu_image);
            textView = itemView.findViewById(R.id.menu_text);
            addButton = itemView.findViewById(R.id.menu_add_button);
        }
    }

    public MenuAdapter(Context context, ArrayList<MenuItem> menuList) {
        this.context = context;
        this.menuList = menuList;
    }
    public void setMenuList(ArrayList<MenuItem> menuList) {
        this.menuList = menuList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, parent, false);
        MenuViewHolder viewHolder = new MenuViewHolder(v);
        return viewHolder;
    }

    @NonNull

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MenuItem currentItem = menuList.get(position);

        holder.imageView.setImageResource(currentItem.getImageResource());
        holder.textView.setText(currentItem.getTitle());
        holder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addButtonClickListener != null){
                    addButtonClickListener.onAddButtonClick(position);
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewActivity(position);
            }
        });
    }

    private void openNewActivity(int position) {
        if(position==0){
            Intent breakfastIntent = new Intent(context,BreakfastActivity.class);
            context.startActivity(breakfastIntent);
        }else if(position==1){
            Intent lunchIntent = new Intent(context,LunchActivity.class);
            context.startActivity(lunchIntent);
        }else if(position==2){
            Intent dinnerIntent = new Intent(context,DinnerActivity.class);
            context.startActivity(dinnerIntent);
        }else{
            Intent snacksIntent = new Intent(context,SnacksActivity.class);
            context.startActivity(snacksIntent);
        }
    }

    public interface OnAddButtonClickListener{
        void onAddButtonClick(int position);
    }

    public void setOnAddButtonClickListener(OnAddButtonClickListener listener){
        this.addButtonClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }
}
