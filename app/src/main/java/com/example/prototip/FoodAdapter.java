package com.example.prototip;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> implements Filterable {

    private List<FoodItem> foodList; //Lista de obiecte FoodItem
    private List<FoodItem> foodListFull; //Lista originala pt filtrare
    private OnItemClickListener mListener; //Interfata pt gestionarea evenimentelor de click

    //Interfata pentru gestionarea evenimentelor de click
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    //Listener pt evenimentele de click
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    //Clasa pentru reprezentarea elementului vizual al fiecarui element in RecyclerView
    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView; //Imaginea alimentului
        private TextView mTitle; //Titlul alimentului
        private TextView mDescription;//Descrierea alimentului

        //Constructorul clasei FoodViewHolder
        public FoodViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.food_image);
            mTitle = itemView.findViewById(R.id.title_text_view);
            mDescription = itemView.findViewById(R.id.description_text_view);

            //Listener pt evenimentele de click pe elementul vizual
            itemView.setOnClickListener(view -> {
                if(listener != null){
                    int position = getAbsoluteAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        listener.onItemClick(position);
                    }
                }
            });
        }
        //Metoda pt a obtine imaginea alimentului
        public ImageView getImageView() {
            return mImageView;
        }

        //Metoda pt a obtine titlul alimentului
        public TextView getTitleTextView() {
            return mTitle;
        }

        //Metoda pt a obtine descrierea alimentului
        public TextView getDescriptionTextView() {
            return mDescription;
        }
    }

    //Constructor pentru adaptorul RecyclerView
    public FoodAdapter(List<FoodItem> foodList) {
        this.foodList = foodList;
        foodListFull = new ArrayList<>(foodList);
    }

    //Creaza si returneaza un nou FoodViewHolder pt fiecare element in RecyclerView
    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recype_layout, parent, false);
        return new FoodViewHolder(v,mListener);
    }

    //Afiseaza datele pentru fiecare element in RecyclerView
    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem currentItem = foodList.get(position);

        //Incarcam imaginea alimentului
        Glide.with(holder.mImageView.getContext())
                .load(currentItem.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .into(holder.mImageView);

        //Setam titlul si descrierea alimentului in elementul vizual
        holder.mTitle.setText(currentItem.getTitle());
        holder.mDescription.setText(currentItem.getDescription());
    }

    //Returneaza numarul total de elemente din RecyclerView
    @Override
    public int getItemCount() {
        return foodList.size();
    }

    //Returneaza un filtru pentru gestionarea cautarilor in RecyclerView
    @Override
    public Filter getFilter() {
        return foodFilter;
    }

    //Filtru pentru gestionarea cautarilor in RecyclerView
    private Filter foodFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<FoodItem> filteredList = new ArrayList<>();

            //Verificam daca sirul de cautare este gol si adauga toate elementele in lista filtrata
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(foodListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                //Filtrare dupa titlu sau descriere
                for (FoodItem item : foodListFull) {
                    if (item.getTitle().toLowerCase().contains(filterPattern) || item.getDescription().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            //Rezultatele filtrarii
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //Curata lista actuala si adauga elementele filtrate
            foodList.clear();
            foodList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
