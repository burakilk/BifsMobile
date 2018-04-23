package com.androilk.bifs.Class;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by FBI on 3.02.2018.
 */


// [START post_class]
@IgnoreExtraProperties
public class PostRecipe{

    public String uid;
    public String author;
    public String title;
    public String body;
    public String data ="";
    public int starCount = 0;
    public List<String> products_id;
    public Map<String, Boolean> stars = new HashMap<>();

    public PostRecipe() {
        // Default constructor required for calls to DataSnapshot.getValue(com.androilk.bifs.Class.Post.class)
    }

    public PostRecipe(String uid, String author, String title, String body,List<String> products_id) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.body = body;
        this.products_id = products_id;
    }


    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("title", title);
        result.put("body", body);
        result.put("starCount", starCount);
        result.put("stars", stars);
        for (int i = 0; i < products_id.size(); i++) {
            data += products_id.get(i)+",";
        }
        result.put("products_id", data);

        return result;
    }
}