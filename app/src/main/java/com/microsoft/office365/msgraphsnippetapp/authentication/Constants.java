package com.microsoft.office365.msgraphsnippetapp.authentication;

/*
These constant values configure the client app to use OAuth2 and open id connect
to authenticate with Azure and authorize the app to access the specified scopes.
Read more about scopes: https://docs.microsoft.com/en-us/azure/active-directory/develop/active-directory-v2-scopes
 */
interface Constants {
    String AUTHORITY_URL = "https://login.microsoftonline.com/common";
    // Update these two constants with the values for your application:
    String CLIENT_ID = "a19a5b81-ba9a-4936-92f4-a6b89f8a01fd";
    String REDIRECT_URI = "https://login.microsoftonline.com/common/oauth2/nativeclient";
    String MICROSOFT_GRAPH_API_ENDPOINT_RESOURCE_ID = "https://graph.microsoft.com/";
    String SCOPES = "openid profile User.Read Mail.Send offline_access";

}
