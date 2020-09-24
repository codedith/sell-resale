package com.waseet.waseetapp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Category {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("slug")
    @Expose
    private String slug;
    @SerializedName("picture")
    @Expose
    private String picture;
    @SerializedName("name_ab")
    @Expose
    private String nameAb;
    @SerializedName("description_ab")
    @Expose
    private String descriptionAb;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getNameAb() {
        return nameAb;
    }

    public void setNameAb(String nameAb) {
        this.nameAb = nameAb;
    }

    public String getDescriptionAb() {
        return descriptionAb;
    }

    public void setDescriptionAb(String descriptionAb) {
        this.descriptionAb = descriptionAb;
    }
}
