package com.waseet.waseetapp.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;

public class AppPreferencesShared {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String APP_SHARED_PREFS;

    public AppPreferencesShared(Context mContext) {
        sharedPreferences = mContext.getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        APP_SHARED_PREFS = "Waseet.com";

    }

    public void setCSrfToken(String cSrfToken) {
        editor.putString("cSrfToken", cSrfToken);
        editor.commit();
    }

    public String getCSrfToken() {
        return sharedPreferences.getString("cSrfToken", "");
    }

    public void setCountryId(String countryId) {
        editor.putString("countryId", countryId);
        editor.commit();
    }

    public String getCountryId() {
        return sharedPreferences.getString("countryId", "");
    }

    public void setStateId(String stateId) {
        editor.putString("stateId", stateId);
        editor.commit();
    }

    public String getStateId() {
        return sharedPreferences.getString("stateId", "");
    }

    public void setCityId(String cityId) {
        editor.putString("cityId", cityId);
        editor.commit();
    }

    public String getCityId() {
        return sharedPreferences.getString("cityId", "");
    }

    public void setLoginUserId(String loginUserId) {
        editor.putString("loginUserId", loginUserId);
        editor.commit();
    }

    public String getLoginUserId() {
        return sharedPreferences.getString("loginUserId", "");
    }

    public String getLocale() {
        return sharedPreferences.getString("locale", "en");  //"ar" hoga reality mai
    }

    public void setLocale(String locale) {
        editor.putString("locale", locale);
        editor.commit();
    }

    public void setCategory(String category) {
        editor.putString("category", category);
        editor.commit();
    }

    public String getCategory() {
        return sharedPreferences.getString("category", "");
    }

    public void setCategoryId(String categoryId) {
        editor.putString("categoryId", categoryId);
        editor.commit();
    }

    public String getCategoryId() {
        return sharedPreferences.getString("categoryId", "");
    }

    public void setSubCategory(String subCategory) {
        editor.putString("subCategory", subCategory);
        editor.commit();
    }

    public String getSubCategory() {
        return sharedPreferences.getString("subCategory", "");
    }

    public void setSubCategoryId(String subCategoryId) {
        editor.putString("subCategoryId", subCategoryId);
        editor.commit();
    }

    public String getSubCategoryId() {
        return sharedPreferences.getString("subCategoryId", "");
    }

    public void setUserProfileDetails(String userProfileDetails) {
        editor.putString("userProfileDetails", userProfileDetails);
        editor.commit();
    }

    public String getUserProfileDetails() {
        return sharedPreferences.getString("userProfileDetails", "");
    }

    public void setIsEditUserProfile(Boolean isEditUserProfile) {
        editor.putBoolean("isEditUserProfile", isEditUserProfile);
        editor.commit();
    }

    public Boolean getIsEditUserProfile() {
        return sharedPreferences.getBoolean("isEditUserProfile", false);
    }

    public void setLogInDirection(String logInDirection) {
        editor.putString("logInDirection", logInDirection);
        editor.commit();
    }

    public String getLogInDirection() {
        return sharedPreferences.getString("logInDirection", "start");
    }

    public void setIsLanguageSelected(Boolean isLanguageSelected) {
        editor.putBoolean("isLanguageSelected", isLanguageSelected);
        editor.commit();
    }

    public Boolean getIsLanguageSelected() {
        return sharedPreferences.getBoolean("isLanguageSelected", false);
    }

    public void ClearPreferences() {
        editor.clear().commit();
    }

    public void setPosetdAdsId(String posetdAdsId) {
        editor.putString("posetdAdsId", posetdAdsId);
        editor.commit();
    }

    public String getPosetdAdsId() {
        return sharedPreferences.getString("posetdAdsId", "");
    }

    public void setIsFilter(Boolean isFilter) {
        editor.putBoolean("isFilter", isFilter);
        editor.commit();
    }

    public Boolean getIsFilter() {
        return sharedPreferences.getBoolean("isFilter", false);
    }

    public void setFilterDetailsList(String filterDetailsList) {
        editor.putString("filterDetailsList", filterDetailsList);
        editor.commit();
    }

    public String getFilterDetailsList() {
        return sharedPreferences.getString("filterDetailsList", "");
    }

    public void setMainCategoryName(String mainCategoryName) {
        editor.putString("mainCategoryName", mainCategoryName);
        editor.commit();
    }

    public String getMainCategoryName() {
        return sharedPreferences.getString("mainCategoryName", "");
    }

    public void setSubCategoryName(String subCategoryName) {
        editor.putString("subCategoryName", subCategoryName);
        editor.commit();
    }

    public String getSubCategoryName() {
        return sharedPreferences.getString("subCategoryName", "");
    }

    public void setLanguageNavigation(String languageNavigation) {
        editor.putString("languageNavigation", languageNavigation);
        editor.commit();
    }

    public String getLanguageNavigation() {
        return sharedPreferences.getString("languageNavigation", "");
    }

}
