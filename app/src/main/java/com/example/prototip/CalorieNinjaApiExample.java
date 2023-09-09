package com.example.prototip;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CalorieNinjaApiExample {

    private static final String API_KEY = "BNlqWMhOTyhyCFr2JNNCHA==v23HWMIKKNnCOPMN";
    private static final String API_URL = "https://api.calorieninjas.com/v1/nutrition?query=%s&num_servings=%d";

    public interface Callback {
        void onNutritionalInfoRetrieved(NutritionalInfo info);
        void onError(String errorMessage);
    }

    public static void searchFood(String foodName, int servingSize, Callback callback) {
        String urlStr = String.format(API_URL, foodName, servingSize);
        new SearchFoodAsyncTask(callback).execute(urlStr);
    }

    private static class SearchFoodAsyncTask extends AsyncTask<String, Void, NutritionalInfo> {
        private Callback callback;

        public SearchFoodAsyncTask(Callback callback) {
            this.callback = callback;
        }

        @Override
        protected NutritionalInfo doInBackground(String... strings) {
            String urlStr = strings[0];
            try {
                URL url = new URL(urlStr);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("X-Api-Key", API_KEY);
                int responseCode = con.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                con.disconnect();

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray itemsArray = jsonResponse.getJSONArray("items");
                JSONObject itemObject = itemsArray.getJSONObject(0);
                int calories = itemObject.getInt("calories");
                double protein = itemObject.getDouble("protein_g");
                double fat = itemObject.getDouble("fat_total_g");

                double carbohydrates = itemObject.getDouble("carbohydrates_total_g");

                return new NutritionalInfo(calories, protein, fat, carbohydrates);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(NutritionalInfo info) {
            if (info != null) {
                callback.onNutritionalInfoRetrieved(info);
            } else {
                callback.onError("Error retrieving nutritional information");
            }
        }

        public void execute(String urlStr) {
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,urlStr);
        }
    }

    public static class NutritionalInfo {
        public int calories;
        public double protein;
        public double fat;
        public double carbohydrates;

        public NutritionalInfo(int calories, double protein, double fat, double carbohydrates) {
            this.calories = calories;
            this.protein = protein;
            this.fat = fat;
            this.carbohydrates = carbohydrates;
        }
    }
}
