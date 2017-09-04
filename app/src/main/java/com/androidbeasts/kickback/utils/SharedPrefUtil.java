package com.androidbeasts.kickback.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

/*Class to manage Shared Preferences tasks*/
public class SharedPrefUtil {
    Context context;
    private SharedPreferences shared;

    public SharedPrefUtil(Context context) {
        this.context = context;
        shared = context.getSharedPreferences("com.androidbeasts", Context.MODE_PRIVATE);
    }

    //check whether the cache has this key
    public boolean checkForKey(String key) {
        return shared.contains(key);
    }

    public void addString(String key, String value) {
        shared.edit().putString(key, value).apply();
    }

    public String getString(String key) {
        if (shared.getString(key, "") != null) {
            return shared.getString(key, "");
        }
        return "";
    }

    public void addInt(String key, int value) {
        shared.edit().putInt(key, value).apply();
    }

    public int getInt(String key) {
        if (shared.getInt(key, -1) != -1) {
            return shared.getInt(key, -1);
        }
        return -1;
    }

    public void addLong(String key, long value) {
        shared.edit().putLong(key, value).apply();
    }

    public long getLong(String key) {
        if (shared.getLong(key, -1) != -1) {
            return shared.getLong(key, -1);
        }
        return -1;
    }

    public int getSpecialInt(String key) {
        if (shared.getInt(key, 0) != 0) {
            return shared.getInt(key, 0);
        }
        return 0;
    }

    public void addBoolean(String key, boolean value) {
        shared.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key) {
        return shared.getBoolean(key, false);
    }

    public void addJsonArray(String key, JSONArray jsonArray) throws JSONException {
        if (getJsonArray(key) != null) {
            JSONArray array = new JSONArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                array.put(jsonArray.getJSONObject(i));
            }
            try {
                for (int i = 0; i < getJsonArray(key).length(); i++) {
                    array.put(getJsonArray(key).getJSONObject(i));
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
            shared.edit().putString(key, array.toString()).apply();
            System.out.println("Saved array: " + array.toString());
        } else {
            shared.edit().putString(key, jsonArray.toString()).apply();
            System.out.println("Saved array: " + jsonArray.toString());
        }
    }

    public void removeAndAddJsonArray(String key, JSONArray jsonArray) {
        shared.edit().putString(key, jsonArray.toString()).apply();
        System.out.println("Saved array: " + jsonArray.toString());
    }

    /*public void removeAndAddJsonArrayWithString(String key, JSONArray jsonArray) throws JSONException {
        shared.edit().putString(key, jsonArray.toString()).apply();
        System.out.println("Saved array: " + jsonArray.toString());
    }*/

    private JSONArray getJsonArray(String key) throws JSONException {
        JSONArray jsonArray;

        if (shared.getString(key, null) != null) {
            jsonArray = new JSONArray(shared.getString(key, ""));
            return jsonArray;
        }
        return null;
    }

    public void addStringToJsonArray(String key, JSONArray jsonArray) throws JSONException {
        if (getJsonArrayWithString(key) != null) {
            JSONArray array = new JSONArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                array.put(jsonArray.getString(i));
            }
            try {
                for (int i = 0; i < getJsonArrayWithString(key).length(); i++) {
                    array.put(getJsonArray(key).getString(i));
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
            shared.edit().putString(key, array.toString()).apply();
            System.out.println("Saved array with string: " + array.toString());
        } else {
            shared.edit().putString(key, jsonArray.toString()).apply();
            System.out.println("Saved array with string: " + jsonArray.toString());
        }
    }

    private JSONArray getJsonArrayWithString(String key) throws JSONException {
        JSONArray jsonArray;

        if (shared.getString(key, null) != null) {
            jsonArray = new JSONArray(shared.getString(key, ""));
            return jsonArray;
        }
        return null;
    }

    public void removeAll() {
        shared.edit().clear().apply();
    }

    public void removeString(String key) {
        shared.edit().remove(key).apply();
    }
}
