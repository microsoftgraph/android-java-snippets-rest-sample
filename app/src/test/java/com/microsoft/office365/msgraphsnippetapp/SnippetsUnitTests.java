/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphsnippetapp;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.office365.microsoftgraphvos.Attendee;
import com.microsoft.office365.microsoftgraphvos.DriveItem;
import com.microsoft.office365.microsoftgraphvos.Event;
import com.microsoft.office365.microsoftgraphvos.Folder;
import com.microsoft.office365.microsoftgraphvos.Group;
import com.microsoft.office365.microsoftgraphvos.MessageWrapper;
import com.microsoft.office365.msgraphapiservices.MSGraphContactService;
import com.microsoft.office365.msgraphapiservices.MSGraphDrivesService;
import com.microsoft.office365.msgraphapiservices.MSGraphEventsService;
import com.microsoft.office365.msgraphapiservices.MSGraphGroupsService;
import com.microsoft.office365.msgraphapiservices.MSGraphMailService;
import com.microsoft.office365.msgraphapiservices.MSGraphMeService;
import com.microsoft.office365.msgraphapiservices.MSGraphUserService;
import com.microsoft.office365.msgraphsnippetapp.snippet.EventsSnippets;
import com.microsoft.office365.msgraphsnippetapp.snippet.GroupsSnippets;
import com.microsoft.office365.msgraphsnippetapp.snippet.MessageSnippets;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SnippetsUnitTests {
    private static String accessToken;
    private static String clientId = System.getenv("test_client_id_v1");
    private static String username = System.getenv("test_username");
    private static String password = System.getenv("test_password");
    private static String dateTime;

    private static MSGraphContactService contactService;
    private static MSGraphDrivesService drivesService;
    private static MSGraphEventsService eventsService;
    private static MSGraphGroupsService groupsService;
    private static MSGraphMailService mailService;
    private static MSGraphMeService meService;
    private static MSGraphUserService userService;

    private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    private static final String GRANT_TYPE = "password";
    private static final String TOKEN_ENDPOINT = "https://login.microsoftonline.com/common/oauth2/token";
    private static final String REQUEST_METHOD = "POST";

    @BeforeClass
    public static void getAccessTokenUsingPasswordGrant() throws IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException, JSONException {
        URL url = new URL(TOKEN_ENDPOINT);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        String urlParameters = String.format(
                "grant_type=%1$s&resource=%2$s&client_id=%3$s&username=%4$s&password=%5$s",
                GRANT_TYPE,
                URLEncoder.encode(ServiceConstants.AUTHENTICATION_RESOURCE_ID, "UTF-8"),
                clientId,
                username,
                password
        );

        connection.setRequestMethod(REQUEST_METHOD);
        connection.setRequestProperty("Content-Type", CONTENT_TYPE);
        connection.setRequestProperty("Content-Length", String.valueOf(urlParameters.getBytes("UTF-8").length));

        connection.setDoOutput(true);
        DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
        dataOutputStream.writeBytes(urlParameters);
        dataOutputStream.flush();
        dataOutputStream.close();

        connection.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JsonParser jsonParser = new JsonParser();
        JsonObject grantResponse = (JsonObject)jsonParser.parse(response.toString());
        accessToken = grantResponse.get("access_token").getAsString();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        request = request.newBuilder()
                                .addHeader("Authorization", "Bearer " + accessToken)
                                // This header has been added to identify this sample in the Microsoft Graph service.
                                // If you're using this code for your project please remove the following line.
                                .addHeader("SampleID", "android-java-snippets-rest-sample")
                                .build();

                        return chain.proceed(request);
                    }
                })
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServiceConstants.AUTHENTICATION_RESOURCE_ID)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        contactService = retrofit.create(MSGraphContactService.class);
        drivesService = retrofit.create(MSGraphDrivesService.class);
        eventsService = retrofit.create(MSGraphEventsService.class);
        groupsService = retrofit.create(MSGraphGroupsService.class);
        mailService = retrofit.create(MSGraphMailService.class);
        meService = retrofit.create(MSGraphMeService.class);
        userService = retrofit.create(MSGraphUserService.class);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.US);
        dateTime = simpleDateFormat.format(new Date());
    }

    @Test
    public void getContacts() throws IOException {
        Call<ResponseBody> call = contactService.getContacts("beta");
        Response response = call.execute();
        Assert.assertTrue("HTTP Response was not successful", response.isSuccessful());
    }

    @Test
    public void getDrive() throws IOException {
        Call<ResponseBody> call = drivesService.getDrive("v1.0");
        Response response = call.execute();
        Assert.assertTrue("HTTP Response was not successful", response.isSuccessful());
    }

    @Test
    public void getOrganizationDrives() throws IOException {
        Call<ResponseBody> call = drivesService.getOrganizationDrives("v1.0");
        Response response = call.execute();
        Assert.assertTrue("HTTP Response was not successful", response.isSuccessful());
    }

    @Test
    public void getCurrentUserFiles() throws IOException {
        Call<ResponseBody> call = drivesService.getCurrentUserFiles("v1.0");
        Response response = call.execute();
        Assert.assertTrue("HTTP Response was not successful", response.isSuccessful());
    }

    @Test
    public void createUpdateDownloadRenameDeleteFile() throws IOException {
        Call<ResponseBody> call = drivesService.putNewFile(
                "v1.0",
                "UnitTest_" + dateTime + ".txt",
                "File created by unit test"
        );
        Response<ResponseBody> response = call.execute();
        Assert.assertTrue("File creation was not successful", response.isSuccessful());

        String rawJson = response.body().string();
        String fileId = new JsonParser().parse(rawJson).getAsJsonObject().get("id").getAsString();

        call = drivesService.updateFile(
                "v1.0",
                fileId,
                "File updated by unit test"
        );
        response = call.execute();
        Assert.assertTrue("File update was not successful", response.isSuccessful());

        call = drivesService.downloadFile(
                "v1.0",
                fileId
        );
        response = call.execute();
        Assert.assertTrue("File download was not successful", response.isSuccessful());

        DriveItem delta = new DriveItem();
        delta.name = "UnitTest_" + dateTime + "(updated).txt";
        call = drivesService.renameFile(
                "v1.0",
                fileId,
                delta
        );
        response = call.execute();
        Assert.assertTrue("File renaming was not successful", response.isSuccessful());

        call = drivesService.deleteFile(
                "v1.0",
                fileId
        );
        response = call.execute();
        Assert.assertTrue("File deletion was not successful", response.isSuccessful());
    }

    @Test
    public void createDeleteFolder() throws IOException {
        DriveItem folder = new DriveItem();
        folder.name = "UnitTest_" + dateTime;
        folder.folder = new Folder();
        folder.conflictBehavior = "rename";
        Call<ResponseBody> call = drivesService.createFolder(
                "v1.0",
                folder
        );
        Response<ResponseBody> response = call.execute();
        Assert.assertTrue("Folder creation was not successful", response.isSuccessful());

        String rawJson = response.body().string();
        String folderId = new JsonParser().parse(rawJson).getAsJsonObject().get("id").getAsString();

        call = drivesService.deleteFile(
                "v1.0",
                folderId
        );
        response = call.execute();
        Assert.assertTrue("File deletion was not successful", response.isSuccessful());
    }

    @Test
    public void getEvents() throws IOException {
        Call<ResponseBody> call = eventsService.getEvents("v1.0");
        Response response = call.execute();
        Assert.assertTrue("HTTP Response was not successful", response.isSuccessful());
    }

    @Test
    public void createUpdateDeleteEvent() throws IOException {
        Event event = EventsSnippets.createEvent();
        event.subject = "UnitTest_" + dateTime;
        event.attendees = new Attendee[]{};
        Call<ResponseBody> call = eventsService.createNewEvent(
                "v1.0",
                event
        );
        Response<ResponseBody> response = call.execute();
        Assert.assertTrue("Event creation was not successful", response.isSuccessful());

        String rawJson = response.body().string();
        String id = new JsonParser().parse(rawJson).getAsJsonObject().get("id").getAsString();

        Event amended = new Event();
        amended.subject = "UnitTest_" + dateTime + "_(updated)";
        call = eventsService.updateEvent(
                "v1.0",
                id,
                amended
        );
        response = call.execute();
        Assert.assertTrue("Event update was not successful", response.isSuccessful());

        call = eventsService.deleteEvent(
                "v1.0",
                id
        );
        response = call.execute();
        Assert.assertTrue("Event deletion was not successful", response.isSuccessful());
    }

    @Test
    public void getGroups() throws IOException {
        Call<ResponseBody> call = groupsService.getGroups("v1.0", new HashMap<String, String>());
        Response response = call.execute();
        Assert.assertTrue("HTTP Response was not successful", response.isSuccessful());
    }

    @Test
    public void createGetUpdateEntitiesDeleteGroup() throws IOException {
        Group group = GroupsSnippets.createGroup();
        group.displayName = "UnitTest_" + dateTime;

        Call<ResponseBody> call = groupsService.createGroup(
                "v1.0",
                group
        );
        Response<ResponseBody> response = call.execute();
        Assert.assertTrue("Group creation was not successful", response.isSuccessful());

        String rawJson = response.body().string();
        String id = new JsonParser().parse(rawJson).getAsJsonObject().get("id").getAsString();

        call = groupsService.getGroup(
                "v1.0",
                id
        );
        response = call.execute();
        Assert.assertTrue("Group retrieval was not successful", response.isSuccessful());

        Group amended = new Group();
        amended.displayName = "UnitTest_" + dateTime + "_(updated)";
        call = groupsService.updateGroup(
                "v1.0",
                id,
                amended
        );
        response = call.execute();
        Assert.assertTrue("Group update was not successful", response.isSuccessful());

        call = groupsService.getGroupEntities(
                "v1.0",
                id,
                "owners"
        );
        response = call.execute();
        Assert.assertTrue("Group entities retrieval was not successful", response.isSuccessful());

        call = groupsService.deleteGroup(
                "v1.0",
                id
        );
        response = call.execute();
        Assert.assertTrue("Group deletion was not successful", response.isSuccessful());
    }

    @Test
    public void getMail() throws IOException {
        Call<ResponseBody> call = mailService.getMail("v1.0");
        Response response = call.execute();
        Assert.assertTrue("HTTP Response was not successful", response.isSuccessful());
    }

    @Test
    public void sendMail() throws IOException {
        MessageWrapper message = MessageSnippets.createMessage(
                "UnitTest_" + dateTime,
                "Message created from a unit test",
                username
        );
        Call<ResponseBody> call = mailService.createNewMail(
                "v1.0",
                message
        );
        Response response = call.execute();
        Assert.assertTrue("HTTP Response was not successful", response.isSuccessful());
    }
}
