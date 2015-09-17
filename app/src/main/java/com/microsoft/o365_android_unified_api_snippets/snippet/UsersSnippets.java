package com.microsoft.o365_android_unified_api_snippets.snippet;

import com.google.gson.JsonObject;
import com.microsoft.o365_android_unified_api_snippets.util.SharedPrefsUtil;
import com.microsoft.unifiedapi.service.UnifiedUserService;

import java.util.UUID;

import retrofit.mime.TypedString;

import static com.microsoft.o365_android_unified_api_snippets.R.array.get_organization_filtered_users;
import static com.microsoft.o365_android_unified_api_snippets.R.array.get_organization_users;
import static com.microsoft.o365_android_unified_api_snippets.R.array.insert_organization_user;

public abstract class UsersSnippets<Result> extends AbstractSnippet<UnifiedUserService, Result> {

    public UsersSnippets(Integer descriptionArray) {
        super(SnippetCategory.userSnippetCategory, descriptionArray);
    }

    static UsersSnippets[] getUsersSnippets() {
        return new UsersSnippets[]{
                // Marker element
                new UsersSnippets(null) {

                    @Override
                    public void request(UnifiedUserService o, retrofit.Callback callback) {
                    }
                },

                /*
                 * GET Gets all of the users in your tenant\'s directory.
                 * HTTP GET https://graph.microsoft.com/beta/myOrganization/users
                 * @see https://msdn.microsoft.com/office/office365/HowTo/office-365-unified-api-reference#msg_ref_entityType_User
                 */
                new UsersSnippets<Void>(get_organization_users) {
                    @Override
                    public void request(
                            UnifiedUserService unifiedUserService,
                            retrofit.Callback<Void> callback) {
                        unifiedUserService.getUsers(getVersion(), callback);
                    }
                },

                /*
                 * GET Gets all of the users in your tenant's directory who are from the United States, using $filter.
                 * HTTP GET https://graph.microsoft.com/beta/myOrganization/users?$filter=country eq \'United States\'
                 * @see https://msdn.microsoft.com/office/office365/HowTo/office-365-unified-api-reference#msg_ref_entityType_User
                 */
                new UsersSnippets<Void>(get_organization_filtered_users) {
                    @Override
                    public void request(
                            UnifiedUserService unifiedUserService,
                            retrofit.Callback<Void> callback) {
                        unifiedUserService.getFilterdUsers(getVersion(), "eq 'United States'", callback);
                    }
                },

                 /*
                 * POST Adds a new user to the tenant's directory
                 * HTTP POST https://graph.microsoft.com/beta/myOrganization/users
                 * @see https://msdn.microsoft.com/office/office365/HowTo/office-365-unified-api-reference#msg_ref_entityType_User
                 */
                new UsersSnippets<Void>(insert_organization_user) {
                    @Override
                    public void request(
                            UnifiedUserService unifiedUserService,
                            retrofit.Callback<Void> callback) {

                        //Use a random UUI for the user name
                        String randomUserName = UUID.randomUUID().toString();

                        //create body
                        JsonObject newUser = new JsonObject();
                        newUser.addProperty("accountEnabled", true);
                        newUser.addProperty("displayName", randomUserName);
                        newUser.addProperty("mailNickname", randomUserName);
                        String tenant = SharedPrefsUtil.getSharedPreferences().getString(SharedPrefsUtil.PREF_USER_TENANT, "");
                        newUser.addProperty("userPrincipalName", randomUserName + '@' + tenant);

                        //create password profile
                        JsonObject passwordProfile = new JsonObject();
                        passwordProfile.addProperty("password", "p@ssw0rd!");
                        passwordProfile.addProperty("forceChangePasswordNextLogin", false);
                        newUser.add("passwordProfile", passwordProfile);

                        TypedString body = new TypedString(newUser.toString()) {
                            @Override
                            public String mimeType() {
                                return "application/json";
                            }
                        };

                        //Call service to POST the new user
                        unifiedUserService.postNewUser(getVersion(), body, callback);
                    }
                }
        };
    }

    public abstract void request(UnifiedUserService unifiedUserService, retrofit.Callback<Result> callback);
}
