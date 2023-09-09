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
import java.util.List;

/**
 * Adaptorul pentru RecyclerView utilizat pentru afișarea meniului.
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {
    private ArrayList<MenuItem> menuList;
    private OnAddButtonClickListener addButtonClickListener;
    private Context context;


    //Clasa interna pentru a tine referintele catre elementele din interfata unui utilizator
    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        private static final int DEFAULT_IMAGE_RESOURCE = R.id.menu_image;
        private static final int DEFAULT_TEXT_RESOURCE = R.id.menu_text;
        private static final int DEFAULT_BUTTON_RESOURCE = R.id.menu_add_button;

        private final ImageView imageView;
        private final TextView textView;
        private final ImageButton addButton;

        public MenuViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(DEFAULT_IMAGE_RESOURCE);
            textView = itemView.findViewById(DEFAULT_TEXT_RESOURCE);
            addButton = itemView.findViewById(DEFAULT_BUTTON_RESOURCE);
        }

        public ImageView getImageView() {
            return imageView;
        }

        public TextView getTextView() {
            return textView;
        }

        public ImageButton getAddButton() {
            return addButton;
        }
    }


    //Constructorul adaptorului
    public MenuAdapter(Context context, List<MenuItem> menuList) {
        this.context = context;
        this.menuList = new ArrayList<>(menuList);
    }

    //Seteaza lista de elemente din meniu
    public void setMenuList(List<MenuItem> menuList) {
        this.menuList = new ArrayList<>(menuList);
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MenuViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, parent, false));
    }

    @NonNull

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MenuItem currentItem = menuList.get(position);

        holder.imageView.setImageResource(currentItem.getImageResource());
        holder.textView.setText(currentItem.getTitle());

        holder.addButton.setOnClickListener(view -> {
            if(addButtonClickListener != null){
                addButtonClickListener.onAddButtonClick(position);
            }
        });
        holder.itemView.setOnClickListener(view -> openNewActivity(position));

    }

    //Deschide o noua activitate in functie de pozitia elementului din meniu
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

    //Interfata pentru listener-ul de evenimente de click pe butonul de adaugare
    public interface OnAddButtonClickListener{
        void onAddButtonClick(int position);
    }

    //Seteaza listener-ul pentru evenimentele de click pe butonul de adaugare.
    public void setOnAddButtonClickListener(OnAddButtonClickListener listener){
        this.addButtonClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }
}
