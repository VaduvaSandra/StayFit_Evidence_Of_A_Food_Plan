package com.example.prototip;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepViewHolder> {
    private List<String> steps;
    public StepAdapter(List<String> steps){
        this.steps = steps;
    }

    @NonNull
    @Override
    public StepAdapter.StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_step, parent, false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepAdapter.StepViewHolder holder, int position) {
        String step = steps.get(position);
        holder.bind(step,position);
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    public class StepViewHolder extends RecyclerView.ViewHolder {
        private TextView stepNumberTextView;
        private TextView stepDescriptionTextView;

        public StepViewHolder(@NonNull View itemView) {
            super(itemView);
            stepNumberTextView = itemView.findViewById(R.id.textViewStepNumber);
            stepDescriptionTextView = itemView.findViewById(R.id.textViewStepDescription);
        }

        public void bind(String step, int position) {
            int stepNumber = position + 1;
            stepNumberTextView.setText(String.valueOf(stepNumber));
            stepDescriptionTextView.setText(step);
        }
    }
}
