package net.smok.utility;

import com.google.gson.JsonObject;

public interface SavableObject<T> {


    T createChild(JsonObject json);


}
