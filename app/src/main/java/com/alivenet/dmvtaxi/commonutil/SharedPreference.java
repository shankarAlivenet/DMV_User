package com.alivenet.dmvtaxi.commonutil;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.alivenet.dmvtaxi.pojo.Driverdetails;
import com.alivenet.dmvtaxi.pojo.Driverfulldetails;
import com.alivenet.dmvtaxi.pojo.RideDriverComplete;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class SharedPreference {

    public static final String PREFS_NAME = "PRODUCT_APP";
    public static final String FAVORITES = "Product_Favorite";
    public static final String FAVORITES_SEND = "Product_Favorite_Send";
    public static final String PREF_NAME_SEND = "pref_name_send";
    private static final String MODE_PRIVATE ="RideDriver" ;

    public SharedPreference() {
        super();
    }

    // This four methods are used for maintaining favorites.
    public void saveFavorites(Context context, ArrayList<Driverfulldetails> favorites) {
        SharedPreferences settings;
        Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);
        editor.putString(FAVORITES, jsonFavorites);
        editor.commit();
    }

    public void addFavorite(Context context, Driverfulldetails product) {

        ArrayList<Driverfulldetails> favorites = getFavorites(context);
        if (favorites == null) {
            favorites = new ArrayList<Driverfulldetails>();
        }
        favorites.add(product);
        saveFavorites(context, favorites);
    }

    public void removeFavorite(Context context, Driverfulldetails product) {
        ArrayList<Driverfulldetails> favorites = getFavorites(context);
        if (favorites != null) {
            favorites.remove(product);
            saveFavorites(context, favorites);
        }
    }

    public void removeFavorite(Context context, int pos) {
        ArrayList<Driverfulldetails> favorites = getFavorites(context);
        if (favorites != null) {
            favorites.remove(pos);
            saveFavorites(context, favorites);
        }
    }

    public ArrayList<Driverfulldetails> getFavorites(Context context) {
        SharedPreferences settings;
        List<Driverfulldetails> favorites;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        if (settings.contains(FAVORITES)) {

            String jsonFavorites = settings.getString(FAVORITES, null);

            Gson gson = new Gson();

            Driverfulldetails[] favoriteItems = gson.fromJson(jsonFavorites,

                    Driverfulldetails[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<Driverfulldetails>(favorites);
        } else
            return null;

        return (ArrayList<Driverfulldetails>) favorites;
    }


    // This four methods are used for maintaining send favorites.
    public void saveFavoritesSend(Context context, ArrayList<Driverfulldetails> favorites) {
        SharedPreferences settings;
        Editor editor;
        settings = context.getSharedPreferences(PREF_NAME_SEND, Context.MODE_PRIVATE);
        editor = settings.edit();
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);
        editor.putString(FAVORITES_SEND, jsonFavorites);
        editor.commit();
    }

    public void addFavoriteSend(Context context, Driverfulldetails product) {

        ArrayList<Driverfulldetails> favorites = getFavoritesSend(context);
        if (favorites == null) {
            favorites = new ArrayList<Driverfulldetails>();
        }
        favorites.add(product);
        saveFavoritesSend(context, favorites);
    }

    public void removeFavoriteSend(Context context, ArrayList<Driverfulldetails> favorites ) {
        //ArrayList<Driverdetails> favorites = getFavoritesSend(context);
        if (favorites != null) {
            //favorites.remove(product);
            saveFavoritesSend(context, favorites);
        }
    }

    public void removeFavoriteSend(Context context, int pos) {
        ArrayList<Driverfulldetails> favorites = getFavoritesSend(context);
        if (favorites != null) {
            favorites.remove(pos);
            saveFavoritesSend(context, favorites);
        }
    }

    public ArrayList<Driverfulldetails> getFavoritesSend(Context context) {
        SharedPreferences settings;
        List<Driverfulldetails> favorites;

        settings = context.getSharedPreferences(PREF_NAME_SEND, Context.MODE_PRIVATE);

        if (settings.contains(FAVORITES_SEND)) {
            String jsonFavorites = settings.getString(FAVORITES_SEND, null);
            Gson gson = new Gson();
            Driverfulldetails[] favoriteItems = gson.fromJson(jsonFavorites,
                    Driverfulldetails[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<Driverfulldetails>(favorites);
        } else
            return null;

        return (ArrayList<Driverfulldetails>) favorites;
    }

    public void SaveRideComplete(Context context,RideDriverComplete favorites) {
        SharedPreferences  mPrefs = context.getSharedPreferences(MODE_PRIVATE,Context.MODE_PRIVATE);
        Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(favorites);
        prefsEditor.putString("MyObject", json);
        prefsEditor.commit();
    }

    public RideDriverComplete getDriverRidercompleate(Context context)
    {
        SharedPreferences  mPrefs = context.getSharedPreferences(MODE_PRIVATE,Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
        RideDriverComplete obj = gson.fromJson(json, RideDriverComplete.class);

        return obj;
    }


}

