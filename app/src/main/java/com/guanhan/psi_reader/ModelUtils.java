package com.guanhan.psi_reader;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Created by guanhan on 12/15/2016.
 */
public class ModelUtils {

    private static Gson gson = new Gson();

    public static <T> T toObject(String json, TypeToken<T> typeToken) {
        return gson.fromJson(json, typeToken.getType());
    }

    public static <T> String toString(T object, TypeToken<T> typeToken) {
        return gson.toJson(object, typeToken.getType());
    }
}
