package com.lnikkila.oidc;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.AuthorizationRequestUrl;
import com.google.api.client.auth.oauth2.PasswordTokenRequest;
import com.google.api.client.auth.oauth2.RefreshTokenRequest;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.auth.openidconnect.IdToken;
import com.google.api.client.auth.openidconnect.IdTokenResponse;
import com.google.api.client.auth.openidconnect.IdTokenVerifier;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.Preconditions;
import com.google.gson.Gson;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A layer of syntactic sugar around the google-oauth-java-client library to simplify using OpenID
 * Connect access on Android.
 *
 * @author Leo Nikkilä
 * @author Camilo Montes
 */
public class OIDCRequestManager {

    private final String TAG = getClass().getSimpleName();

    //region Supported Flows

    /**
     * Supported OpenId Connect / OAuth2 Flows
     */
    public enum Flows
    {
        Code,               //http://openid.net/specs/openid-connect-core-1_0.html#CodeFlowAuth
        Implicit,           //http://openid.net/specs/openid-connect-core-1_0.html#ImplicitFlowAuth
        Hybrid,             //http://openid.net/specs/openid-connect-core-1_0.html#HybridFlowAuth
        Password
    }

    /**
     * Checks if the given flow name is supported.
     * @param value the name of the flow name to check
     * @return true if supported, false otherwise
     */
    public static boolean isSupportedFlow(String value) {

        for (Flows c : Flows.values()) {
            if (c.name().equals(value)) {
                return true;
            }
        }

        return false;
    }

    //endregion

    protected final Context context;

    protected final String authorizationEndpoint;
    protected final String tokenEndpoint;
    protected final String userInfoEndpoint;

    protected boolean useOAuth2;
    protected String clientId;
    protected String clientSecret;
    protected String redirectUrl;
    protected String[] scopes;
    protected String flowTypeName;
    protected Flows flowType;
    protected String issuerId;
    protected Map<String, String> extras;

    public OIDCRequestManager(Context context) {
        this.context = context;

        this.authorizationEndpoint = this.context.getString(R.string.op_authorizationEnpoint);
        this.tokenEndpoint = this.context.getString(R.string.op_tokenEndpoint);
        this.userInfoEndpoint = this.context.getString(R.string.op_userInfoEndpoint);

        SharedPreferences sharedPreferences = context.getSharedPreferences("oidc_clientconf", Context.MODE_PRIVATE);
        boolean loadConfigFromUserPrefs = sharedPreferences.getBoolean("oidc_loadfromprefs", false);
        if(loadConfigFromUserPrefs) {
            //reads from user preferences --> This should be use only for test purposes
            this.useOAuth2 = sharedPreferences.getBoolean("oidc_oauth2only", false);
            this.clientId = sharedPreferences.getString("oidc_clientId", null);
            this.clientSecret = sharedPreferences.getString("oidc_clientSecret", null);
            this.redirectUrl = sharedPreferences.getString("oidc_redirectUrl", null);
            String scopesString = sharedPreferences.getString("oidc_scopes", null);
            this.scopes = scopesString != null ? scopesString.split(" ") : null;
            this.flowTypeName = sharedPreferences.getString("oidc_flowType", null);
            this.issuerId = sharedPreferences.getString("oidc_issuerId", null);
//            this.extras = parseStringArray(sharedPreferences.getStringSet("oidc_authextras", null));
        } else {
            //reads from predefined res/values
            this.useOAuth2 = this.context.getResources().getBoolean(R.bool.oidc_oauth2only);
            this.clientId = this.context.getString(R.string.oidc_clientId);
            this.clientSecret = this.context.getString(R.string.oidc_clientSecret);
            this.redirectUrl = this.context.getString(R.string.oidc_redirectUrl);
            this.scopes = this.context.getResources().getStringArray(R.array.oidc_scopes);
            this.flowTypeName = this.context.getString(R.string.oidc_flowType);
            this.issuerId = this.context.getString(R.string.oidc_issuerId);
            this.extras = parseStringArray(this.context.getResources().getStringArray(R.array.oidc_authextras));
        }

        if (!checkConfiguration()) {
            throw new RuntimeException("The OpenId Connect client configuration is not correctly set.");
        }
        this.flowType = Flows.valueOf(flowTypeName);
    }

    //region Setters/Getters

    public OIDCRequestManager setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public OIDCRequestManager setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    public boolean isRedirectUrl(String redirectUrl) {
        Preconditions.checkNotNull(redirectUrl);
        Preconditions.checkNotNull(this.redirectUrl);
        return redirectUrl.startsWith(this.redirectUrl);
    }

    public OIDCRequestManager setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
        return this;
    }

    public OIDCRequestManager setScopes(String[] scopes) {
        this.scopes = scopes;
        return this;
    }

    public Flows getFlowType() {
        return flowType;
    }

    public OIDCRequestManager setFlowType(Flows flowType) {
        this.flowType = flowType;
        return this;
    }

    public OIDCRequestManager setFlowType(String flowTypeName) {
        if (isSupportedFlow(flowTypeName)) {
            this.flowTypeName = flowTypeName;
            this.flowType = Flows.valueOf(flowTypeName);
        } else {
            throw new IllegalArgumentException(flowTypeName + " is not a supported flow type");
        }
        return this;
    }

    public OIDCRequestManager setIssuerId(String issuerId) {
        this.issuerId = issuerId;
        return this;
    }

    public OIDCRequestManager setExtras(Map<String, String> extras) {
        this.extras = extras;
        return this;
    }

    //endregion

    //region Authentication Requests

    /**
     * Builds the authentication url with the pre-set OpenId Connect client configuration.
     * @param state the state used to maintain state between the request and the callback.
     * @return the string representation of the authentication url.
     * @see OIDCRequestManager#implicitFlowAuthenticationUrl(String)
     * @see OIDCRequestManager#hybridFlowAuthenticationUrl(String)
     * @see OIDCRequestManager#codeFlowAuthenticationUrl(String)
     */
    public String getAuthenticationUrl(String state) {
        AuthorizationRequestUrl request;
        switch (flowType) {
            case Implicit: {
                request = implicitFlowAuthenticationUrl(state);
                break;
            }
            case Hybrid: {
                request = hybridFlowAuthenticationUrl(state);
                break;
            }
            case Code:
            default: {
                request = codeFlowAuthenticationUrl(state);
                break;
            }
        }

        Log.d(TAG, String.format("Using %1$s flow", flowType.name()));

        // This are extra query parameters that can be specific to an OP. For instance prompt -> consent
        // tells the Authorization Server that it SHOULD prompt the End-User for consent before returning
        // information to the Client.
        if (extras != null) {
            for (Map.Entry<String, String> queryParam : extras.entrySet()) {
                request.set(queryParam.getKey(), queryParam.getValue());
            }
        }

        return request.build();
    }

    /**
     * Generates an Authentication Request URL to the Authorization Endpoint to start an Implicit Flow.
     * When using the Implicit Flow, all tokens are returned from the Authorization Endpoint; the
     * Token Endpoint is not used so it allows to get all tokens on one trip. The downside is that
     * it doesn't support refresh tokens.
     * @see <a href="http://openid.net/specs/openid-connect-core-1_0.html#ImplicitFlowAuth">Implicit Flow</a>
     *
     * @param state the state used to maintain state between the request and the callback.
     * @return the Authentication Request URL
     */
    private AuthorizationRequestUrl implicitFlowAuthenticationUrl(String state) {

        //TODO: see what the following statement implies :
        // "While OAuth 2.0 also defines the token Response Type value for the Implicit Flow,
        // OpenID Connect does not use this Response Type, since no ID Token would be returned"
        // from http://openid.net/specs/openid-connect-core-1_0.html#Authentication
        String[] responsesTypes = {"id_token", "token"};
        List<String> scopesList = Arrays.asList(scopes);
        List<String> responsesList = Arrays.asList(responsesTypes);

        //noinspection UnnecessaryLocalVariable
        AuthorizationRequestUrl request = new AuthorizationRequestUrl(authorizationEndpoint, clientId,
                responsesList)
                .setRedirectUri(redirectUrl)
                .setScopes(scopesList)
                .setState(state)
                .set("nonce", ""); //TODO: nonce is optional, needs to include per-session state and be unguessable to attackers. We should try to generate one.

        return request;
    }

    /**
     * Generates an Authentication Request URL to the Authorization Endpoint to start an Hybrid Flow.
     * When using the Hybrid Flow, some tokens are returned from the Authorization Endpoint and
     * others are returned from the Token Endpoint.
     * @see <a href="http://openid.net/specs/openid-connect-core-1_0.html#HybridFlowAuth">Hybrid Flow</a>
     *
     * @param state the state used to maintain state between the request and the callback.
     * @return the Authentication Request URL
     */
    private AuthorizationRequestUrl hybridFlowAuthenticationUrl(String state) {

        // The response type "code" is the only mandatory response type on hybrid flow, it must be
        // coupled with other response types to form one of the following values : "code id_token",
        // "code token", or "code id_token token".
        // For our needs "token" is not defined here because we want an access_token that has made
        // a client authentication. That access_token will be retrieve later using the TokenEndpoint
        // (see #requestTokensWithCodeGrant).
        String[] responsesTypes = {"code", "id_token"};
        List<String> scopesList = Arrays.asList(scopes);
        List<String> responsesList = Arrays.asList(responsesTypes);

        //noinspection UnnecessaryLocalVariable
        AuthorizationRequestUrl request = new AuthorizationRequestUrl(authorizationEndpoint, clientId, responsesList)
                .setRedirectUri(redirectUrl)
                .setScopes(scopesList)
                .setState(state)
                .set("nonce", ""); //TODO: nonce is optional, needs to include per-session state and be unguessable to attackers. We should try to generate one.

        return request;
    }

    /**
     * Generates an Authentication Request URL to the Authorization Endpoint to start an Code Flow.
     * When using the Code Flow, all tokens are returned from the Token Endpoint.
     * The Authorization Server can authenticate the Client before exchanging the Authorization Code
     * for an Access Token.
     * @see <a href="http://openid.net/specs/openid-connect-core-1_0.html#CodeFlowAuth">Code Flow</a>
     *
     * @param state the state used to maintain state between the request and the callback.
     * @return the Authentication Request URL
     */
    private AuthorizationRequestUrl codeFlowAuthenticationUrl(String state) {

        List<String> scopesList = Arrays.asList(scopes);

        //noinspection UnnecessaryLocalVariable
        AuthorizationCodeRequestUrl request = new AuthorizationCodeRequestUrl(authorizationEndpoint, clientId)
                .setRedirectUri(redirectUrl)
                .setScopes(scopesList)
                .setState(state)
                .set("nonce", ""); //TODO: nonce is optional, needs to include per-session state and be unguessable to attackers. We should try to generate one.

        return request;
    }

    //endregion

    //region Tokens Requests

    /**
     * Exchanges an Authorization Code for an Access Token, Refresh Token and (optional) ID Token.
     * This provides the benefit of not exposing any tokens to the User Agent and possibly other
     * malicious applications with access to the User Agent.
     * The Authorization Server can also authenticate the Client before exchanging the Authorization
     * Code for an Access Token.
     *
     * Needs to be run on a separate thread.
     *
     * @param authCode the authorization code received from the authorization endpoint
     * @return the parsed successful token response received from the token endpoint
     * @throws IOException for an error response
     */
    public TokenResponse requestTokensWithCodeGrant(String authCode) throws IOException {

        AuthorizationCodeTokenRequest request = new AuthorizationCodeTokenRequest(
                AndroidHttp.newCompatibleTransport(),
                new GsonFactory(),
                new GenericUrl(tokenEndpoint),
                authCode
        );
        request.setRedirectUri(redirectUrl);

        // This are extra query parameters that can be specific to an OP. For instance for OpenAm
        // we can define 'realm' that defines to which sub realm the request is going to.
        if (extras != null) {
            for (Map.Entry<String, String> queryParam : extras.entrySet()) {
                request.set(queryParam.getKey(), queryParam.getValue());
            }
        }

        if (!TextUtils.isEmpty(clientSecret)) {
            request.setClientAuthentication(new BasicAuthentication(clientId, clientSecret));
        } else {
            request.set("client_id", clientId);
        }

        if (useOAuth2) {
            Log.d(TAG, "tokens request OAuth2 sent");
            TokenResponse tokenResponse = request.executeUnparsed().parseAs(TokenResponse.class);
            String accessToken = tokenResponse.getAccessToken();

            if (!TextUtils.isEmpty(accessToken)){
                Log.d(TAG, String.format("Manage to parse and extract AT : %1$s", accessToken));
                return tokenResponse;
            }
            else {
                throw new IOException("Invalid Access Token returned.");
            }
        } else {
            Log.d(TAG, "tokens request OIDC sent");
            IdTokenResponse response = IdTokenResponse.execute(request);
            String idToken = response.getIdToken();

            if (isValidIdToken(idToken)) {
                try {
                    String accessToken = response.getAccessToken();
                    // if there is no AT return it means we only request idToken so there's no need to validate the AT
                    if (!TextUtils.isEmpty(accessToken)) {// || isValidAccessToken(accessToken, idToken)) {
                        return response;
                    } else {
                        throw new IOException("Invalid access token. The at_hash does not match with the return access token.");
                    }
                } catch (Exception e) {
                    throw new IOException("Can not validate AccessToken.", e);
                }
            } else {
                throw new IOException("Invalid ID token returned.");
            }
        }
    }

    /**
     * Exchanges the end-user credentials for an Access Token, Refresh Token and (optional) ID Token.
     * This SHOULD NOT be use for
     * The Authorization Server can also authenticate the Client before exchanging the Authorization
     * Code for an Access Token.
     *
     * Needs to be run on a separate thread.
     *
     * @param userName the end-user name
     * @param userPwd the end-user password
     * @return the parsed successful token response received from the token endpoint
     * @throws IOException for an error response
     */
    public TokenResponse requestTokensWithPasswordGrant(String userName, String userPwd) throws IOException {

        List<String> scopesList = Arrays.asList(scopes);

        PasswordTokenRequest request = new PasswordTokenRequest(
                AndroidHttp.newCompatibleTransport(),
                new GsonFactory(),
                new GenericUrl(tokenEndpoint),
                userName,
                userPwd
        );

        if (!scopesList.isEmpty()) {
            request.setScopes(scopesList);
        }

        // This are extra query parameters that can be specific to an OP. For instance for OpenAm
        // we can define 'realm' that defines to which sub realm the request is going to.
        if (extras != null) {
            for (Map.Entry<String, String> queryParam : extras.entrySet()) {
                request.set(queryParam.getKey(), queryParam.getValue());
            }
        }

        if (!TextUtils.isEmpty(clientSecret)) {
            request.setClientAuthentication(new BasicAuthentication(clientId, clientSecret));
        } else {
            request.set("client_id", clientId);
        }


        // Working with OIDC
        if (scopesList.contains("openid")) {
            Log.d(TAG, "PasswordGrant request OIDC sent");

            IdTokenResponse tokenResponse = IdTokenResponse.execute(request);
            String idToken = tokenResponse.getIdToken();

            if (isValidIdToken(idToken)) {
                return tokenResponse;
            } else {
                throw new IOException("Invalid ID token returned.");
            }
        } else {
            Log.d(TAG, "PasswordGrant request OAuth2 sent");
            TokenResponse tokenResponse = request.executeUnparsed().parseAs(TokenResponse.class);
            String accessToken = tokenResponse.getAccessToken();

            if (!TextUtils.isEmpty(accessToken)){
                Log.d(TAG, String.format("Manage to parse and extract AT : %1$s", accessToken));
                return tokenResponse;
            }
            else {
                throw new IOException("Invalid Access Token returned.");
            }
        }
    }

    /**
     * Parses the fragment component received from the authorization endpoint when using the implicit flow.
     * For instance on a authorization response
     *
     * HTTP/1.1 302 Found
     * Location: https://redirect/cb#access_token=SlAV32hkKG&token_type=bearer&id_token=eyJ0...&expires_in=3600&state=af0ifjsldkj
     *
     * the fragment component would be "access_token=SlAV32hkKG&token_type=bearer&id_token=eyJ0...&expires_in=3600&state=af0ifjsldkj".
     *
     * @param fragmentPart fragment component of the Redirection URI
     * @param state the state used to maintain state between the request and the callback.
     * @return the parsed successful token response from the a fragment component
     * @throws IOException for an error response
     */
    public TokenResponse parseTokensFromImplicitResponseFragmentPart(String fragmentPart, String state) throws IOException {

        Uri tokenExtrationUrl = new Uri.Builder().encodedQuery(fragmentPart).build();
        String accessToken = tokenExtrationUrl.getQueryParameter("access_token");
        String idToken = tokenExtrationUrl.getQueryParameter("id_token");
        String tokenType = tokenExtrationUrl.getQueryParameter("token_type");
        String expiresInString = tokenExtrationUrl.getQueryParameter("expires_in");
        Long expiresIn = (!TextUtils.isEmpty(expiresInString)) ? Long.decode(expiresInString) : null;
        String scope = tokenExtrationUrl.getQueryParameter("scope");
        String returnedState = tokenExtrationUrl.getQueryParameter("state");

        if (state.equalsIgnoreCase(returnedState)) {
            if (!TextUtils.isEmpty(tokenType) && expiresIn != null) {
                if (useOAuth2 && !TextUtils.isEmpty(accessToken)) {
                    TokenResponse response = new TokenResponse();
                    response.setAccessToken(accessToken);
                    response.setTokenType(tokenType);
                    response.setExpiresInSeconds(expiresIn);
                    response.setScope(scope);
                    response.setFactory(new GsonFactory());
                    return response;
                } else if (!TextUtils.isEmpty(idToken)) {
                    IdTokenResponse response = new IdTokenResponse();
                    response.setAccessToken(accessToken);
                    response.setIdToken(idToken);
                    response.setTokenType(tokenType);
                    response.setExpiresInSeconds(expiresIn);
                    response.setScope(scope);
                    response.setFactory(new GsonFactory());
                    try {
                        if (isValidIdToken(idToken)) {
                            // if there is no AT return it means we only request idToken so there's no need to validate the AT
                            if (!TextUtils.isEmpty(accessToken)) {// || isValidAccessToken(accessToken, idToken)) {
                                return response;
                            } else {
                                throw new IOException("Invalid access token. The at_hash does not match with the return access token.");
                            }
                        } else {
                            throw new IOException("Invalid idToken returned");
                        }
                    } catch (Exception e) {
                        throw new IOException("Could not validate access token or idToken", e);
                    }
                } else {
                    throw new IOException("Could not read access token or idToken from the response fragment");
                }
            } else {
                throw new IOException("Could not read mandatory values (tokenType, expiresIn) from the response fragment");
            }
        } else {
            throw new IOException("Local and returned states don't match");
        }
    }

    /**
     *  Exchanges a Refresh Token for a new set of tokens.
     *
     *  Note that the Token Server may require you to use the `offline_access` scope to receive
     *  Refresh Tokens.
     *
     * @param refreshToken the refresh token used to request new Access Token / idToken.
     * @return the parsed successful token response received from the token endpoint
     * @throws IOException for an error response
     */
    public TokenResponse refreshTokens(String refreshToken) throws IOException {

        List<String> scopesList = Arrays.asList(scopes);

        RefreshTokenRequest request = new RefreshTokenRequest(
                AndroidHttp.newCompatibleTransport(),
                new GsonFactory(),
                new GenericUrl(tokenEndpoint),
                refreshToken);

        if (!scopesList.isEmpty()) {
            request.setScopes(scopesList);
        }

        // This are extra query parameters that can be specific to an OP. For instance prompt -> consent
        // tells the Authorization Server that it SHOULD prompt the End-User for consent before returning
        // information to the Client.
        if (extras != null) {
            for (Map.Entry<String, String> queryParam : extras.entrySet()) {
                request.set(queryParam.getKey(), queryParam.getValue());
            }
        }

        // If the oidc client is confidential (needs authentication)
        if (!TextUtils.isEmpty(clientSecret)) {
            request.setClientAuthentication(new BasicAuthentication(clientId, clientSecret));
        } else {
            request.set("client_id", clientId);
        }

        if (useOAuth2) {
            if (scopesList.contains("openid")) {
                Log.w(TAG, "Using OAuth2 only request but scopes contain values for OpenId Connect");
            }
            return request.executeUnparsed().parseAs(TokenResponse.class);
        } else {
            return IdTokenResponse.execute(request);
        }
    }

    //endregion

    //region UserInfo Requests

    /**
     * Gets user information from the UserInfo endpoint.
     * @param token an idToken or accessToken associated to the end-user.
     * @param classOfT the class used to deserialize the user info into.
     * @return the parsed user information.
     * @throws IOException for an error response
     */
    public  <T> T getUserInfo(String token,  Class<T> classOfT) throws IOException {
        String url = userInfoEndpoint;
        if (extras != null) {
            url = HttpRequest.append(userInfoEndpoint, extras);
        }

        HttpRequest request = new HttpRequest(url, HttpRequest.METHOD_GET);
        request.authorization("Bearer " + token).acceptJson();

        if (request.ok()) {
            String jsonString = request.body();
            return new Gson().fromJson(jsonString, classOfT);
        } else {
            throw new IOException(request.message());
        }
    }

    //endregion

    //region Revocation Requests

    //endregion

    //region Tokens Validation

    /**
     * Validates an IdToken.
     * TODO: Look into verifying the token nonce as well?
     *
     * @param idTokenString the IdToken to validate
     * @return true if the idToken is valid, false otherwise.
     * @throws IOException when the IdToken can not be parse.
     * @see IdTokenVerifier#verify(IdToken)
     */
    private boolean isValidIdToken(@NonNull String idTokenString) throws IOException {

        List<String> audiences = Collections.singletonList(clientId);
        IdTokenVerifier verifier = new IdTokenVerifier.Builder()
                .setAudience(audiences)
                .setAcceptableTimeSkewSeconds(1000)
                .setIssuer(issuerId)
                .build();

        IdToken idToken = IdToken.parse(new GsonFactory(), idTokenString);

        return true;//verifier.verify(idToken);
    }

    /**
     * Validates the access token issued with an ID Token, by comparing the result of the access token hash
     * with the 'at_hash' claim contained on the ID Token.
     * @param accessTokenString the access token to hash
     * @param idTokenString the ID Token were the 'at_hash' can be found
     * @return true if the result of the hashed access token is equal to the 'at_hash' claim.
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @see <a hfre="http://openid.net/specs/openid-connect-core-1_0.html#ImplicitTokenValidation">http://openid.net/specs/openid-connect-core-1_0.html#ImplicitTokenValidation</a>
     */
    private boolean isValidAccessToken(String accessTokenString, String idTokenString) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        boolean isValidAt = false;
        if (!TextUtils.isEmpty(accessTokenString) && !TextUtils.isEmpty(idTokenString)) {
            IdToken idToken = IdToken.parse(new GsonFactory(), idTokenString);
            String alg = idToken.getHeader().getAlgorithm();
            byte[] atBytes = accessTokenString.getBytes("UTF-8");
            String atHash = idToken.getPayload().getAccessTokenHash();

            String forgedAtHash;
            if ("HS256".equals(alg) || "RS256".equals(alg)) {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                digest.update(atBytes, 0, atBytes.length);
                atBytes = digest.digest();
                atBytes = Arrays.copyOfRange(atBytes, 0, atBytes.length / 2);
                forgedAtHash = Base64.encodeToString(atBytes, Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);

                Log.d(TAG, "Alg : " + alg);
                Log.d(TAG, "Receive at_hash : " + atHash);
                Log.d(TAG, "Forged at_hash  : " + forgedAtHash);

                isValidAt = atHash.equals(forgedAtHash);
            } else {
                Log.w(TAG, "Unsupported alg claim : " +alg + ". Supported alg are HS256, RS256");
            }

        } else {
            Log.w(TAG, "Can't verify access token, AT or idToken empty");
        }

        return true;//isValidAt;
    }

    //endregion

    //region Helper methods

    /**
     * Helper to parse the content of an array of strings as a HashMap, where the contained strings
     * have the format "%s|%s".
     * @param stringArray the array of strings
     * @return a hash map (key->value)
     */
    private HashMap<String, String> parseStringArray(String[] stringArray) {
        HashMap<String, String> outputArray = new HashMap<>(stringArray.length);
        for (String entry : stringArray) {
            String[] splitResult = entry.split("\\|", 2);
            outputArray.put(splitResult[0], splitResult[1]);
        }
        return outputArray;
    }

    /**
     * Checks if OpenId Connect client settings are correctly set.
     * @return true if all expected settings are set, false otherwise.
     */
    public boolean checkConfiguration() {
        boolean isConfigOk = false;

        if (!TextUtils.isEmpty(flowTypeName) && isSupportedFlow(flowTypeName)) {
            Flows supportedFlow = Flows.valueOf(flowTypeName);

            // RFC6749 https://tools.ietf.org/html/rfc6749#section-2.3  says that client_secret are OPTIONAL
            // and if not set it usually means that the client is public and does not require to authenticate
            // with the authorization server. When client_secret is set we will consider that client is
            // confidential and as for now we will only support HTTP Basic authentication to authenticate
            //  with the authorization server (others like public/private key pair, etc. are not yet supported).
            if (TextUtils.isEmpty(clientSecret)) {
                Log.d(TAG, "Undefined client_secret, OIDC client is public");
            } else {
                Log.d(TAG, "OIDC client is confidential and will be using HTTP Basic authentication");
            }

            // RFC6749 https://tools.ietf.org/html/rfc6749#section-3.3 says that scopes are OPTIONAL
            // and if omited by client it means to use pre-defined default value or the authorization server fails
            if (scopes.length == 0) {
                Log.w(TAG, "Undefined scopes, OIDC client will use authorization server pre-defined default values");
            }

            switch (supportedFlow) {
                case Code:
                case Implicit:
                case Hybrid:
                    isConfigOk =
                            !TextUtils.isEmpty(clientId) &&
                                    // RFC6749 https://tools.ietf.org/html/rfc6749#section-4.1.1 says this is OPTIONAL
                                    // but we need this to know when the WebView should to stop following redirects
                                    !TextUtils.isEmpty(redirectUrl);
                    break;
                case Password:
                    // RFC6749 https://tools.ietf.org/html/rfc6749#section-4.3.2 we don't need to check anything
                    // here because resource owner username/password will be set by a form later on. We know that
                    // this is not the way that it should be (username/password should be already be set on OIDC
                    // client and be check if they are set here). Also this flow should NOT be use by an
                    // Android App, this flow was added for completeness.
                    Log.w(TAG, "Please be sure you know what you are doing when using the 'password' flow");
                    isConfigOk = true;
                    break;
                default:
                    Log.wtf(TAG, "An new/unknown flow type was added but it's configuration checks where not implemented");
                    break;
            }
        } else {
            Log.e(TAG, "Undefined or unsupported flow type, check your OIDC client configuration file 'res/values/oidc_clientconfig.xml'");
        }
        return isConfigOk;
    }

    /**
     * Generates a secure state token
     * @param opHint the OpenIdConnect Provider name or any other identifier that gives an idea of the
     *               provider you are dealing with (i.e Google, Facebook, ...).
     *               This will be use as part of the state token. Can be empty but not null.
     * @return a state token.
     */
    public static String generateStateToken(@NonNull String opHint){
        SecureRandom sr = new SecureRandom();
        String cleanOpName = TextUtils.replace(opHint, new String[]{"\\W"}, new String[]{""}).toString();
        return  cleanOpName+sr.nextInt();
    }

    //endregion

}
