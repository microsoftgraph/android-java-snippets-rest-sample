package com.microsoft.unifiedapi.service;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface UnifiedMeService {

    @GET("/{version}/me")
    void getMe(
            @Path("version") String version,
            Callback<Void> callback
    );

    @GET("/{version}/me")
    void getMeResponsibilities(
            @Path("version") String version,
            @Query("$select") String select,
            Callback<Void> callback
    );

    @GET("/{version}/me/{entity}")
    void getMeEntities(
            @Path("version") String version,
            @Path("entity") String entity,
            Callback<Void> callback
    );

    @GET("/{version}/me/userPhoto")
    void getMePhoto(
            @Path("version") String version,
            Callback<Void> callback
    );

    @GET("/{version}/me/manager")
    void getMeManager(
            @Path("version") String version,
            Callback<Void> callback
    );

    @GET("/{version}/me/directReports")
    void getDirectReports(
            @Path("version") String version,
            Callback<Void> callback
    );

    @GET("/{version}/me/workingWith")
    void getWorkingWith(
            @Path("version") String version,
            Callback<Void> callback
    );

    @GET("/{version}/me/memberOf")
    void getMemberOf(
            @Path("version") String version,
            Callback<Void> callback
    );
}