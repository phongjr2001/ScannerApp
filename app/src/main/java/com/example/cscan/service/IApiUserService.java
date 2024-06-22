package com.example.cscan.service;

import com.example.cscan.models.ChangePasswordRequest;
import com.example.cscan.models.ChangePinRequest;
import com.example.cscan.models.DataTypes;
import com.example.cscan.models.Datas;
import com.example.cscan.models.Documents;
import com.example.cscan.models.GroupImage;
import com.example.cscan.models.Images;
import com.example.cscan.models.TokenModel;
import com.example.cscan.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface IApiUserService {

    IApiUserService apiService = new Retrofit.Builder()
            .baseUrl("http://192.168.205.1:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IApiUserService.class);

    @POST("api/Users/Register")
    Call<User> registerUser(@Body User user);

    @POST("api/Users/Login")
    Call<User> login(@Body User user);

    @POST("api/Users/ChangePassword")
    Call<Void> changePassword(@Body ChangePasswordRequest changePasswordRequest);
    @POST("api/Users/ChangePin")
    Call<Void> changePin(@Body ChangePinRequest changePinRequest);
    @POST("api/GroupImages/InsertGroup")
    Call<GroupImage> insertGroup(@Body GroupImage groupImage);

    @PUT("api/GroupImages/updateGroup")
    Call<GroupImage> updateGroup(@Body GroupImage groupImage);

    @DELETE("api/GroupImages/deleteGroupImage/{groupId}")
    Call<Void> deleteGroup(@Path("groupId") int groupId);
    @POST("api/Images/InsertImage")
    Call<Images> insertImage(@Body Images imgaes);

    @GET("api/Images/getAllImage/{groupId}")
    Call<List<Images>> getAllImages(@Path("groupId") int groupId);

    @GET("api/GroupImages/getAllGroup/{userId}")
    Call<List<GroupImage>> getAllGroup(@Path("userId") int userId);

    @DELETE("api/Images/deleteImage/{imageId}")
    Call<Void> deleteImage(@Path("imageId") int imageId);

    @POST("api/Documents/InsertDocument")
    Call<Documents> insertDocument(@Body Documents documents);

    @GET("api/Documents/GetAllDocument/{userId}")
    Call<List<Documents>> getAllDocument(@Path("userId") int userId,
                                         @Header("Authorization") String token);
    @PUT("api/Documents/updateDocument")
    Call<Documents> updateDocument(@Body Documents documents);
    @DELETE("api/Documents/deleteDocument/{ducumentId}")
    Call<Void> deleteDocument(@Path("ducumentId") int ducumentId);


    @POST("api/DataTypes/InsertDataType")
    Call<DataTypes> InsertDataType(@Body DataTypes dataTypes);

    @GET("api/DataTypes/getAllDataType/{documentId}")
    Call<List<DataTypes>> getAllDataType(@Path("documentId") int documentId);

    @POST("api/Datas/InsertData")
    Call<Datas> InsertData(@Body Datas datas);

    @GET("api/Datas/getAllData/{typeDataId}")
    Call<List<Datas>> getAllData(@Path("typeDataId") int typeDataId);

    @DELETE("api/Datas/deleteData/{dataId}")
    Call<Void> deleteData(@Path("dataId") int dataId);

    @PUT("api/Datas/UpdateData")
    Call<Datas> UpdateData(@Body Datas datas);

    @POST("api/Datas/RefreshToken")
    Call<TokenModel> RefreshToken(@Body TokenModel tokenModel);
}

