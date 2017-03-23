package com.lnikkila.oidc.authenticator;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.auth.openidconnect.IdTokenResponse;
import com.lnikkila.oidc.OIDCAccountManager;
import com.lnikkila.oidc.OIDCRequestManager;
import com.lnikkila.oidc.R;
import com.lnikkila.oidc.minsdkcompat.CompatUri;
import com.lnikkila.oidc.security.UserNotAuthenticatedWrapperException;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * An Activity that is launched by the Authenticator for requesting authorisation from the user and
 * creating an Account.
 *
 * The user will interact with the OIDC server via a WebView that monitors the URL for parameters
 * that indicate either a successful authorisation or an error. These parameters are set by the
 * spec.
 *
 * After the Authorization Token has successfully been obtained, we use the single-use token to
 * fetch an ID Token, an Access Token and a Refresh Token. We create an Account and persist these
 * tokens.
 *
 * @author Leo Nikkilä
 * @author Camilo Montes
 */
public class AuthenticatorActivity extends AccountAuthenticatorActivity {

    private final String TAG = getClass().getSimpleName();

    public static final int ASK_USER_ENCRYPT_PIN_REQUEST_CODE = 1;

    public static final String KEY_IS_NEW_ACCOUNT       = "com.lnikkila.oidc.KEY_IS_NEW_ACCOUNT";
    public static final String KEY_ACCOUNT_NAME         = "com.lnikkila.oidc.KEY_ACCOUNT_NAME";

    private OIDCAccountManager accountManager;
    private OIDCRequestManager requestManager;
    private KeyguardManager keyguardManager;
    private Account account;
    private boolean isNewAccount;

    protected String secureState;

    /*package*/ RelativeLayout parentLayout;
    /*package*/ WebView webView;

    /*package*/ View passwordGrantFormLayout;
    /*package*/ TextInputLayout userNameInputLayout;
    /*package*/ TextInputLayout userPasswordInputLayout;
    /*package*/ Button validatePasswordGrantFormButton;

    //region Activity Lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        Bundle extras = getIntent().getExtras();

        // Are we supposed to create a new account or renew the authorisation of an old one?
        isNewAccount = extras.getBoolean(KEY_IS_NEW_ACCOUNT, false);

        // In case we're renewing authorisation, we also got an Account object that we're supposed
        // to work with.
        String accountName = extras.getString(KEY_ACCOUNT_NAME);

        accountManager = new OIDCAccountManager(this);
        keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        // starts the request manager with the OIDC client setting in /res/values/oidc_clientconf.xml
        requestManager = new OIDCRequestManager(this);


        if (accountName != null) {
            account = accountManager.getAccountByName(accountName);
        }

        boolean isPasswordFlow = requestManager.getFlowType() == OIDCRequestManager.Flows.Password;
        initAuthenticationWebView(isPasswordFlow);
        //setupPasswordGrantForm(isPasswordFlow);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.d(TAG, "KeyguardSecure is not used for pre M devices");
        } else {
            if (accountManager.isKeyPinRequired() && !keyguardManager.isKeyguardSecure()) {
                Toast.makeText(this,
                        "Secure lock screen hasn't set up. Go to 'Settings -> Security -> Screenlock' to set up a lock screen",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Handles possible webView leak : http://stackoverflow.com/a/8011027/665823
        if (parentLayout != null) parentLayout.removeAllViews();
        if(webView != null) webView.destroy();
    }

    //endregion

    //region Authentication WebView

    @SuppressLint("SetJavaScriptEnabled")
    private void initAuthenticationWebView(boolean isPasswordFlow) {
        if (!isPasswordFlow) {
            // parentLayout = (RelativeLayout) findViewById(R.id.authenticatorActivityLayout);
            parentLayout = (RelativeLayout) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);

            // Initialise the WebView
            // see  http://stackoverflow.com/a/8011027/665823 of why we doing this :
            webView = new WebView(this);
            parentLayout.addView(webView, new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            //instead of this :
            //webView = (WebView) findViewById(R.id.WebView);

            webView.getSettings().setJavaScriptEnabled(getResources().getBoolean(R.bool.webview_allow_js));
            webView.setWebViewClient(new AuthorizationWebViewClient());
            webView.setVisibility(View.VISIBLE);

            //
            String authUrl = getAuthenticationUrl();
            Log.d(TAG, String.format("Initiated activity with authentication WebView and URL '%s'.", authUrl));
            webView.loadUrl(authUrl);
        }
    }

    private class AuthorizationWebViewClient extends WebViewClient {

        /**
         * Forces the WebView to not load the URL if it can be handled.
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return handleUri(url) || super.shouldOverrideUrlLoading(view, url);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String url) {
            showErrorDialog("Network error: got %s for %s.", description, url);
        }

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            showErrorDialog("Network error: got %s for %s.", error.getDescription().toString(), request.getUrl().toString());
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            String cookies = CookieManager.getInstance().getCookie(url);
            Log.d(TAG, String.format("Cookies for url %1$s : %2$s", url, cookies));
        }
    }

    /**
     * Generate the authentication URL using the OIDC client settings and a generate secure state.
     * @return url as string
     */
    private String getAuthenticationUrl() {
        //Generates a new state to help prevent cross-site scripting attacks
        secureState = OIDCRequestManager.generateStateToken(getString(R.string.op_usualName));
        // Generate the authentication URL using the OIDC client settings
        return requestManager.getAuthenticationUrl(secureState);
    }

    /**
     * Tries to handle the given URI as the redirect URI.
     *
     * @param uri URI to handle.
     * @return Whether the URI was handled.
     */
    private boolean handleUri(String uri) {
        if (handleAuthorizationErrors(uri)) {
            return true;
        } else if (requestManager.isRedirectUrl(uri)) {
            finishAuthorization(uri);
            return true;
        }
        return false;
    }

    //endregion

    //region PasswordGrant form setup

    @SuppressWarnings("ConstantConditions")
    private void setupPasswordGrantForm(boolean isPasswordFlow) {
        passwordGrantFormLayout = findViewById(R.id.passwordGrantFormLayout);
        if (isPasswordFlow) {
            userNameInputLayout = (TextInputLayout) findViewById(R.id.userNameInputLayout);
            userNameInputLayout.setHint(getString(R.string.OIDCUserNameOptionHint));
            userNameInputLayout.getEditText().addTextChangedListener(new OIDCOptionsTextWatcher(userNameInputLayout));

            userPasswordInputLayout = (TextInputLayout) findViewById(R.id.userPasswordInputLayout);
            userPasswordInputLayout.setHint(getString(R.string.OIDCUserPwdOptionHint));
            userPasswordInputLayout.getEditText().addTextChangedListener(new OIDCOptionsTextWatcher(userPasswordInputLayout));

            validatePasswordGrantFormButton = (Button) findViewById(R.id.validateFormButton);
            validatePasswordGrantFormButton.setText(R.string.OIDCLoginnHint);
            validatePasswordGrantFormButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText userNameEdit = (EditText) findViewById(R.id.userNameEditText);
                    EditText userPwdEdit = (EditText) findViewById(R.id.userPasswordEditText);

                    String userName = userNameEdit.getText().toString();
                    String userPwd = userPwdEdit.getText().toString();

                    if (checkPasswordGrantForm(userName, userPwd)) {
                        PasswordFlowTask task = new PasswordFlowTask();
                        task.execute(userName, userPwd);
                    } else {
                        Log.w(TAG, "Mandatory fields on password grant form missing");
                    }
                }
            });
            passwordGrantFormLayout.setVisibility(View.VISIBLE);
        } else {
            passwordGrantFormLayout.setVisibility(View.GONE);
        }
    }

    private boolean checkPasswordGrantForm(String userName, String userPwd) {
        boolean isOk = true;
        if (TextUtils.isEmpty(userName)){
            userNameInputLayout.setError(getString(R.string.OIDCOptionsMandatoryError));
            userNameInputLayout.setErrorEnabled(true);
            isOk = false;
        }
        if (TextUtils.isEmpty(userPwd)){
            userPasswordInputLayout.setError(getString(R.string.OIDCOptionsMandatoryError));
            userPasswordInputLayout.setErrorEnabled(true);
            isOk = false;
        }
        return  isOk;
    }

    protected static class OIDCOptionsTextWatcher implements TextWatcher {
        TextInputLayout textInputLayout;

        public OIDCOptionsTextWatcher(TextInputLayout textInputLayout) {
            this.textInputLayout = textInputLayout;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            textInputLayout.setErrorEnabled(false);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    //endregion

    //region Flow handling

    /**
     * Handles the result embedded in the redirect URI.
     *
     * @param redirectUriString Received redirect URI with query parameters.
     */
    private void finishAuthorization(String redirectUriString) {
        Uri redirectUri = Uri.parse(redirectUriString);

        Set<String> parameterNames;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            parameterNames = CompatUri.getQueryParameterNames(redirectUri);
        } else {
            parameterNames = redirectUri.getQueryParameterNames();
        }

        String extractedFragment = redirectUri.getEncodedFragment();

        switch (requestManager.getFlowType()) {
            case Implicit: {
                if (!TextUtils.isEmpty(extractedFragment)) {
                    ImplicitFlowTask task = new ImplicitFlowTask();
                    task.execute(extractedFragment);

                } else {
                    Log.e(TAG, String.format(
                            "redirectUriString '%1$s' doesn't contain fragment part; can't extract tokens",
                            redirectUriString));
                }
                break;
            }
            case Hybrid: {
                if (!TextUtils.isEmpty(extractedFragment)) {
                    HybridFlowTask task = new HybridFlowTask();
                    task.execute(extractedFragment);

                } else {
                    Log.e(TAG, String.format(
                            "redirectUriString '%1$s' doesn't contain fragment part; can't request tokens",
                            redirectUriString));
                }
                break;
            }
            case Code:
            default: {
                // The URL will contain a `code` parameter when the user has been authenticated
                if (parameterNames.contains("state")) {
                    String state = redirectUri.getQueryParameter("state");
                    if (parameterNames.contains("code")) {
                        String authToken = redirectUri.getQueryParameter("code");

                        // Request the ID token
                        CodeFlowTask task = new CodeFlowTask();
                        task.execute(authToken, state);
                    } else {
                        Log.e(TAG, String.format(
                                "redirectUriString '%1$s' doesn't contain code param; can't extract authCode",
                                redirectUriString));
                    }
                    break;
                }
            }
        }
    }

    /**
     * Tries to handle errors on the given URI. Authorization errors are handled when the URI
     * contains a "error" parameter.
     *
     * @param uri URI to handle.
     * @return Whether the URI had an error to handle.
     */
    private boolean handleAuthorizationErrors(String uri){
        Uri parsedUri = Uri.parse(uri);

        Set<String> parameterNames;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            parameterNames = CompatUri.getQueryParameterNames(parsedUri);
        } else {
            parameterNames = parsedUri.getQueryParameterNames();
        }

        // We need to check if the error is not in the fragment (for Implicit/Hybrid Flow)
        if (parameterNames.isEmpty()) {
            String extractedFragment = parsedUri.getEncodedFragment();
            if (!TextUtils.isEmpty(extractedFragment)) {
                parsedUri = new Uri.Builder().encodedQuery(extractedFragment).build();
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                    parameterNames = CompatUri.getQueryParameterNames(parsedUri);
                }
                else {
                    parameterNames = parsedUri.getQueryParameterNames();
                }
            }
        }


        if (parameterNames.contains("error")) {
            // In case of an error, the `error` parameter contains an ASCII identifier, e.g.
            // "temporarily_unavailable" and the `error_description` *may* contain a
            // human-readable description of the error.
            //
            // For a list of the error identifiers, see
            // http://tools.ietf.org/html/rfc6749#section-4.1.2.1
            String error = parsedUri.getQueryParameter("error");
            String errorDescription = parsedUri.getQueryParameter("error_description");

            // If the user declines to authorise the app, there's no need to show an error message.
            if (error.equals("access_denied")) {
                Log.i(TAG, String.format("User declines to authorise the app : %s", errorDescription));
            }
            else {
                showErrorDialog("Error code: %s\n\n%s", error, errorDescription);
            }

            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Abstract task for authorization flows handling.
     */
    private abstract class AuthorizationFlowTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPostExecute(Boolean wasSuccess) {
            if (wasSuccess) {
                // The account manager still wants the following information back
                Intent intent = new Intent();

                intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, account.name);
                intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, account.type);

                setAccountAuthenticatorResult(intent.getExtras());
                setResult(RESULT_OK, intent);
                finish();
            } else {
                showErrorDialog("Could not get ID Token.");
            }
        }

        protected boolean createOrUpdateAccount(TokenResponse response) {
            if (isNewAccount) {
                createAccount(response);
            } else {
                saveTokens(response);
            }
            return true;
        }
    }

    /**
     * Handles Implicit flow by creating an {@link IdTokenResponse} from a Uri fragment asynchronously.
     * <br/>
     * An Uri string containing a Uri fragment is passed as first parameter of the
     * {@link AsyncTask#execute(Object[])} method, i.e :
     * <br/>
     * <i>
     * http://domain/redirect.html#scope=offline_access%20openid%20profile&state=xyz&code=xxx&id_token=yyyy
     * </i>
     */
    private class ImplicitFlowTask extends AuthorizationFlowTask {

        @Override
        protected Boolean doInBackground(String... args) {
            String fragmentPart = args[0];
            try {
                TokenResponse response = requestManager.parseTokensFromImplicitResponseFragmentPart(fragmentPart, secureState);
                return createOrUpdateAccount(response);
            } catch (IOException e) {
                Log.e(TAG, "Could not reconstruct a token response from the HTTP fragment", e);
                return false;
            }
        }
    }

    /**
     * Handles Hybrid flow by extracting asynchronously the authorization code from a Uri fragment
     * then exchanging it for an {@link IdTokenResponse} by making a request to the token endpoint.
     * <br/>
     * An Uri string containing a Uri fragment is passed as first parameter of the
     * {@link AsyncTask#execute(Object[])} method, i.e :
     * <br/>
     * <i>
     * http://domain/redirect.html#scope=offline_access%20openid%20profile&state=xyz&code=xxx&id_token=yyyy
     * </i>
     */
    private class HybridFlowTask extends AuthorizationFlowTask {
        @Override
        protected Boolean doInBackground(String... args) {
            String fragmentPart = args[0];
            boolean didStoreTokens = false;

            Uri tokenExtrationUrl = new Uri.Builder().encodedQuery(fragmentPart).build();
            String idToken = tokenExtrationUrl.getQueryParameter("id_token");
            String authCode = tokenExtrationUrl.getQueryParameter("code");
            String returnedState = tokenExtrationUrl.getQueryParameter("state");

            if(secureState.equalsIgnoreCase(returnedState)) {
                if (!TextUtils.isEmpty(idToken) && !TextUtils.isEmpty(authCode)) {
                    Log.i(TAG, "Requesting access_token with AuthCode : " + authCode);

                    //TODO: we already have the idToken and we aren't doing anything with it... why? Will it be returned once more when we get the access token?
                    try {
                        TokenResponse response = requestManager.requestTokensWithCodeGrant(authCode);
                        didStoreTokens = createOrUpdateAccount(response);
                    } catch (IOException e) {
                        Log.e(TAG, "Could not get response from the token endpoint", e);
                    }
                }
            } else {
                Log.e(TAG, "Local and returned states don't match");
            }
            return didStoreTokens;
        }
    }

    /**
     * Handles Code flow by requesting asynchronously a {@link IdTokenResponse} to the
     * token endpoint using an authorization code.
     * <br/>
     * The authorization code is passed as first parameter of the
     * {@link AsyncTask#execute(Object[])} method.
     * <br/>
     */
    private class CodeFlowTask extends AuthorizationFlowTask {
        @Override
        protected Boolean doInBackground(String... args) {
            String authCode = args[0];
            String returnedState = args[1];
            boolean didStoreTokens = false;

            if (secureState.equalsIgnoreCase(returnedState)) {
                Log.i(TAG, "Requesting access_token with AuthCode : " + authCode);
                try {
                    TokenResponse response = requestManager.requestTokensWithCodeGrant(authCode);
                    didStoreTokens = createOrUpdateAccount(response);
                } catch (IOException e) {
                    Log.e(TAG, "Could not get response from the token endpoint", e);
                }
            } else {
                Log.e(TAG, "Local and returned states don't match");
            }
            return didStoreTokens;
        }
    }

    private class PasswordFlowTask extends AuthorizationFlowTask {
        @Override
        protected Boolean doInBackground(String... args) {
            String userName = args[0];
            String userPwd = args[1];
            boolean didStoreTokens = false;

            Log.d(TAG, "Requesting access_token with username : " + userName);
            try {
                TokenResponse response = requestManager.requestTokensWithPasswordGrant(userName, userPwd);
                didStoreTokens = createOrUpdateAccount(response);
            } catch (IOException e) {
                Log.e(TAG, "Could not get response from the token endpoint", e);
            }
            return didStoreTokens;
        }
    }

    //endregion

    //region Account Management
    //TODO: this should be handled by the OIDCAccountManager

    /**
     * AccountManager expects that each account has a unique name. If a new account has the same name
     * as a previously created one, it will overwrite the older account.
     *
     * Unfortunately the OIDC spec cannot guarantee[1] that any user information is unique, save for
     * the user ID (i.e. the ID Token subject) which, depending on the authentication server, is hardly
     * human-readable. This makes choosing between multiple accounts difficult.
     *
     * We'll resort to naming each account 'app_name : claim'. Usually a claim to use here could be 'name'
     * or 'email' if that user information is unique.
     *
     * [1]: http://openid.net/specs/openid-connect-basic-1_0.html#ClaimStability
     *
     * The 'app_name' will be as a fallback if the other information isn't available for some reason
     * (for instance no contact with UserInfo Endpoint, or bad claim extraction).
     *
     * @param response the TokenResponse receive from the authentication server.
     * @param claimAsPartOfAccountName claim to be use as part the account name (ex: email, name, given_name).
     *                                 If null it will use sub claim as part of the accout name.
     * @return the account name to be use when creating an account on the AccountManager
     */
    private String getAccountName(TokenResponse response, String claimAsPartOfAccountName) {
        String accountName = null;
        if (response instanceof IdTokenResponse) {
            try {
                // Asserts the identity of the user, called subject in OpenID (sub)
                String accountSubject = ((IdTokenResponse)response).parseIdToken().getPayload().getSubject();

                if ((accountSubject != null && !TextUtils.isEmpty(accountSubject)) || claimAsPartOfAccountName == null){
                    accountName = String.format("%1$s : %2$s", getString(R.string.app_name), accountSubject);
                } else {
                    // If for a reason we can't get the subject or want to use a other claim instead,
                    // we will try to get the `claimAsAccountName` using the UserInfo Endpoint
                    Map userInfo = requestManager.getUserInfo(response.getAccessToken(), Map.class);
                    if (userInfo.containsKey(claimAsPartOfAccountName)) {
                        String userName = (String) userInfo.get(claimAsPartOfAccountName);
                        accountName = String.format("%1$s : %2$s", getString(R.string.app_name), userName);
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Could not get needed account info using the given TokenResponse.", e);
            }
        }

        // Fallback to app's name if the other information isn't available
        if(accountName == null || TextUtils.isEmpty(accountName)) {
            accountName = getString(R.string.app_name);
        }

        return accountName.trim();
    }

    private void createAccount(TokenResponse response) {
        Log.d(TAG, "Creating account.");

        String accountType = getString(R.string.account_authenticator_type);
        String claimAsAccountName = "name"; //FIXME : this be some kind of oidc client parameter. What to do... what to do...
        String accountName = getAccountName(response, claimAsAccountName);

        account = new Account(accountName, accountType);
        accountManager.getAccountManager().addAccountExplicitly(account, null, null);

        Log.d(TAG, String.format("Saved tokens : (AT %1$s) (RT %2$s)", response.getAccessToken(), response.getRefreshToken()));

        // Store the tokens in the account
        saveTokens(response);

        Log.d(TAG, "Account created.");
    }


    private void saveTokens(TokenResponse response) {
        try {
            accountManager.saveTokens(account, response);
        } catch (UserNotAuthenticatedWrapperException e) {
            showAuthenticationScreen(ASK_USER_ENCRYPT_PIN_REQUEST_CODE);
        }
    }

    //endregion

    /**
     * TODO: Improve error messages.
     *
     * @param message Error message that can contain formatting placeholders.
     * @param args    Formatting arguments for the message, or null.
     */
    private void showErrorDialog(String message, String... args) {
        if (args != null) {
            message = String.format(message, args);
        }

        new AlertDialog.Builder(this)
                .setTitle("Sorry, there was an error")
                .setMessage(message)
                .setCancelable(true)
                .setNeutralButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        finish();
                    }
                })
                .create()
                .show();
    }

    private void showAuthenticationScreen(int requestCode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.e(TAG, "This should never happend for pre M devices");
        } else {
            Intent intent = keyguardManager.createConfirmDeviceCredentialIntent(null, null);
            if (intent != null) {
                startActivityForResult(intent, requestCode);
            }
        }
    }

    /**
     * Create an intent for showing the authorisation web page from an external app/service context.
     * This is usually used to request authorization when tokens expire.
     * @param context the Context where the intent is trigger from, like Activity, App, or Service
     * @param accountName the account name that we need authorization for
     * @return an intent to open AuthenticatorActivity
     */
    public static Intent createIntentForReAuthorization(Context context, String accountName) {
        Intent intent = new Intent(context, AuthenticatorActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(AuthenticatorActivity.KEY_ACCOUNT_NAME, accountName);
        return intent;
    }

    /**
     * Create an intent for showing the authorisation web page from an external app/service context.
     * This is usually used to request authorization when creating a new account.
     * @param context the Context where the intent is trigger from, like Activity, App, or Service
     * @param accountName the account name to be created
     * @return an intent to open AuthenticatorActivity
     */
    public static Intent createIntentForAccountCreation(Context context, String accountName) {
        Intent intent = new Intent(context, AuthenticatorActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(AuthenticatorActivity.KEY_ACCOUNT_NAME, accountName);
        intent.putExtra(AuthenticatorActivity.KEY_IS_NEW_ACCOUNT, true);
        return intent;
    }
}
