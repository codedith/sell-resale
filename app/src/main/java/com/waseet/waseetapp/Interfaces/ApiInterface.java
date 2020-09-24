package com.waseet.waseetapp.Interfaces;

import com.waseet.waseetapp.Models.AdsDetails;
import com.waseet.waseetapp.Models.CSRFTokenResponse;
import com.waseet.waseetapp.Models.CategoryListResponse;
import com.waseet.waseetapp.Models.CommonResponse;
import com.waseet.waseetapp.Models.CountryListResponse;
import com.waseet.waseetapp.Models.FavouritesListResponse;
import com.waseet.waseetapp.Models.LoginResponse;
import com.waseet.waseetapp.Models.MarkNotification;
import com.waseet.waseetapp.Models.MyAdsResponse;
import com.waseet.waseetapp.Models.NotificationListResponse;
import com.waseet.waseetapp.Models.PostedImageResponse;
import com.waseet.waseetapp.Models.ProductListResponse;
import com.waseet.waseetapp.Models.CityListResponse;
import com.waseet.waseetapp.Models.SubCategoryListResponse;
import com.waseet.waseetapp.Models.UserInfoResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("get_csrf_token")
    Call<CSRFTokenResponse> getCSrfToken();

    @GET("country_list")
    Call<CountryListResponse> getCounrtyList();

//    @GET("country_state_list")
//    Call<CountryListResponse> getCountryStateList(@Query("country") String country);

    @GET("state_cities_list")
    Call<CityListResponse> getStateCityList(@Query("state") String state);

    @GET("category_list")
    Call<CategoryListResponse> getAllCategoryList();


    @GET("subcategories_list")
    Call<SubCategoryListResponse> getAllSubcategoriesList(@Query("slug") String slug);

    @GET("all_ads_list_category")
    Call<ProductListResponse> getAllAddedCategoryList(@Query("category") String category, @Query("subcategory") String subcategory);

    @FormUrlEncoded
    @POST("user_register_api")
    Call<CommonResponse> postUserRegistration(@Field("username") String userName, @Field("csrf_test_name") String cSRFTestName,
                                              @Field("email") String email, @Field("contact") String contact, @Field("country") String country,
                                              @Field("password") String password, @Field("confirmpassword") String confirmPassword,
                                              @Field("terms_n_conditions") Boolean termsAndConditions);

    @FormUrlEncoded
    @POST("user_login_api")
    Call<LoginResponse> postUserLogin(@Field("csrf_test_name") String cSRFTestName, @Field("email") String email,
                                      @Field("password") String password);

    @FormUrlEncoded
    @POST("change_password_api")
    Call<CommonResponse> postChangePassword(@Field("user_id") String userId, @Field("csrf_test_name") String cSRFTestName,
                                                   @Field("current_password") String currentPassword, @Field("new_password") String newPassword,
                                                   @Field("confirm_password") String confirmPassword);

    @FormUrlEncoded
    @POST("user_profile_api")
    Call<UserInfoResponse> postUserProfileData(@Field("csrf_test_name") String cSRFTestName, @Field("user_id") String userId);

    @Multipart
    @POST("update_user_data")
    Call<CommonResponse> postUpdateUserData(@Part("csrf_test_name") RequestBody cSRFTestName, @Part("user_id") RequestBody userId,
                                                   @Part MultipartBody.Part profilePicture, @Part("firstname") RequestBody firstName,
                                                   @Part("lastname") RequestBody lastName, @Part("email") RequestBody email,
                                                   @Part("contact") RequestBody contact, @Part("address") RequestBody address,
                                                   @Part("country") RequestBody country, @Part("state") RequestBody state,
                                                   @Part("city") RequestBody city);

    @FormUrlEncoded
    @POST("add_favorite_api")
    Call<CommonResponse> postAddFavoriteApi(@Field("csrf_test_name") String cSRFTestName, @Field("user_id") String userId,
                                                   @Field("ad_id") String adId);

    @FormUrlEncoded
    @POST("my_favourite_ads_api")
    Call<FavouritesListResponse> postMyFavouriteAds(@Field("csrf_test_name") String csrf_test_name, @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("mark_notification_view")
    Call<MarkNotification> postMarkNotificationView(@Field("csrf_test_name") String cSRFTestName, @Field("user_id") String userId,
                                                    @Field("notification_id") String notificationId);

    @FormUrlEncoded
    @POST("user_notification_api")
    Call<NotificationListResponse> postUserNotificationApi(@Field("csrf_test_name") String cSRFTestName, @Field("user_id") String userId);

    @Multipart
    @POST("add_ads")
    Call<CommonResponse> postAddNewAds(@Part("csrf_test_name") RequestBody cSRFTestName, @Part("user_id") RequestBody userId,
                                              @Part("category") RequestBody categoryId, @Part("subcategory") RequestBody subcategoryId,
                                              @Part("price") RequestBody price, @Part("negotiable") RequestBody negotiable,
                                              @Part("country") RequestBody countryId, @Part("state") RequestBody stateId,
                                              @Part("city") RequestBody cityId, @Part("address") RequestBody address,
                                              @Part("address-lang") RequestBody addressLang, @Part("address-lat") RequestBody addressLat,
                                              @Part("title") RequestBody title, @Part("tags") RequestBody tags,
                                              @Part MultipartBody.Part img1, /*@Part MultipartBody.Part img2, @Part MultipartBody.Part img3,*/
                                              @Part("description") RequestBody description);

    @Multipart
    @POST("ads_edit")
    Call<CommonResponse> postAddEditAds(@Part("csrf_test_name") RequestBody cSRFTestName, @Part("user_id") RequestBody userId,
                                        @Part("ads_id") RequestBody adsId, @Part("category") RequestBody categoryId,
                                        @Part("subcategory") RequestBody subcategoryId, @Part("price") RequestBody price,
                                        @Part("country") RequestBody countryId, @Part("state") RequestBody stateId,
                                        @Part("city") RequestBody cityId, @Part("title") RequestBody title,
                                        @Part("negotiable") RequestBody negotiable, @Part("address") RequestBody address,
                                        @Part("address-lang") RequestBody addressLang, @Part("address-lat") RequestBody addressLat,
                                        @Part("tags") RequestBody tags, @Part("description") RequestBody description,
                                              @Part ("old_img_1") RequestBody oldImg1, /*@Part MultipartBody.Part oldImg2,
                                               @Part MultipartBody.Part oldImg3,*/ @Part MultipartBody.Part img1/*,
                                               @Part MultipartBody.Part img2, @Part MultipartBody.Part img3*/);

    @FormUrlEncoded
    @POST("my_ads")
    Call<MyAdsResponse> postMyAds(@Field("csrf_test_name") String cSRFTestName, @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("ads_delete")
    Call<CommonResponse> postAdsDelete(@Field("csrf_test_name") String cSRFTestName, @Field("user_id") String userId, @Field("ads_id") String adsId);

    @FormUrlEncoded
    @POST("ads_details")
    Call<AdsDetails> postedAdsDetails(@Field("csrf_test_name") String cSRFTestName, @Field("user_id") String userId,
                                      @Field("ads_id") String adsId);

    @Multipart
    @POST("ads_images")
    Call<CommonResponse> postMoreAdsImages(@Part("csrf_test_name") RequestBody cSRFTestName, @Part("user_id") RequestBody userId,
                                                  @Part("ads_id") RequestBody adsId, @Part MultipartBody.Part img_1);

    @FormUrlEncoded
    @POST("get_ads_images")
    Call<PostedImageResponse> getPostedAdsImages(@Field("csrf_test_name") String cSRFTestName, @Field("user_id") String userId,
                                                 @Field("ads_id") String adsId);

    @FormUrlEncoded
    @POST("delete_ads_images")
    Call<CommonResponse> deleteAdsImages(@Field("csrf_test_name") String cSRFTestName, @Field("ads_image_id") String adsImageId);

    @FormUrlEncoded
    @POST("user_forget_password_api")
    Call<CommonResponse> userForgetPasswordApi(@Field("csrf_test_name") String cSRFTestName, @Field("email") String email);

    @GET("all_ads_list_category")
    Call<ProductListResponse> getFilteredCategoryList(@Query("category") String categorySlug, @Query("subcategory") String subcategorySlug,
                                                       @Query("price-min") String priceMin, @Query("price-max") String priceMax,
                                                       @Query("country") String countryId, @Query("state") String stateId,
                                                       @Query("city") String cityId, @Query("q") String searchQueryKey);

    @GET("all_ads_list_category")
    Call<ProductListResponse> getSearchDataList(@Query("q") String searchQueryKey);
}