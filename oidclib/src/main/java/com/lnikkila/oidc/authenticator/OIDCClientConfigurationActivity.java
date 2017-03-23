package com.lnikkila.oidc.authenticator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.lnikkila.oidc.OIDCRequestManager;
import com.lnikkila.oidc.R;

/**
 * Created by CQX342 on 10/02/2016.
 */
public class OIDCClientConfigurationActivity extends AppCompatActivity {

    private static final String TAG = OIDCClientConfigurationActivity.class.getSimpleName();

    /*package*/ SwitchCompat useOAuthSwitch;
    /*package*/ TextInputLayout clientIdInputLayout;
    /*package*/ TextInputLayout clientSecretInputLayout;
    /*package*/ TextInputLayout redirectUriInputLayout;
    /*package*/ TextInputLayout issuerInputLayout;
    /*package*/ TextInputLayout scopesInputLayout;
    /*package*/ EditText clientIdEdit;
    /*package*/ EditText clientSecretEdit;
    /*package*/ EditText redirectUriEdit;
    /*package*/ EditText issuerEdit;
    /*package*/ EditText scopesEdit;
    /*package*/ Spinner flowTypeSpinner;
    /*package*/ Button validateClientButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientconfiguration);
        setupClientConfigurationForm();
        loadClientConfigurationForm();
        Log.w(TAG, "This functionality should be use only for test purposes never on production");
    }

    //region ClientConfiguration form setup

    @SuppressWarnings("ConstantConditions")
    private void setupClientConfigurationForm() {

        useOAuthSwitch = (SwitchCompat) findViewById(R.id.useOauth2Switch);

        validateClientButton = (Button) findViewById(R.id.setOIDCClientButton);
        validateClientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveClientConfigurationForm(v);
            }
        });
        flowTypeSpinner = (Spinner) findViewById(R.id.flowTypeSpinner);
        FlowTypesAdapter adapter = new FlowTypesAdapter(this, R.layout.spinner_selected_item, OIDCRequestManager.Flows.values());
        flowTypeSpinner.setAdapter(adapter);

        clientIdInputLayout = (TextInputLayout) findViewById(R.id.clientIdInputLayout);
        clientIdInputLayout.getEditText().addTextChangedListener(new OIDCOptionsTextWatcher(clientIdInputLayout));
        clientIdEdit = (EditText) findViewById(R.id.clientIdEditText);

        clientSecretInputLayout = (TextInputLayout) findViewById(R.id.clientSecretInputLayout);
        clientSecretInputLayout.getEditText().addTextChangedListener(new OIDCOptionsTextWatcher(clientSecretInputLayout));
        clientSecretEdit = (EditText) findViewById(R.id.clientSecretEditText);

        redirectUriInputLayout = (TextInputLayout) findViewById(R.id.redirectUriInputLayout);
        redirectUriInputLayout.getEditText().addTextChangedListener(new OIDCOptionsTextWatcher(redirectUriInputLayout));
        redirectUriEdit = (EditText) findViewById(R.id.redirectUriEditText);

        issuerInputLayout = (TextInputLayout) findViewById(R.id.issuerInputLayout);
        issuerInputLayout.getEditText().addTextChangedListener(new OIDCOptionsTextWatcher(issuerInputLayout));
        issuerEdit = (EditText) findViewById(R.id.issuerEditText);

        scopesInputLayout = (TextInputLayout) findViewById(R.id.scopesInputLayout);
        scopesInputLayout.getEditText().addTextChangedListener(new OIDCOptionsTextWatcher(scopesInputLayout));
        scopesEdit = (EditText) findViewById(R.id.scopesEditText);
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

    //region ClientConfiguration form validation

    public void loadClientConfigurationForm() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("oidc_clientconf", Context.MODE_PRIVATE);
        boolean loadFromPrefs = sharedPreferences.getBoolean("oidc_loadfromprefs", false);

        boolean useOauth2;
        String clientId;
        String clientSecret;
        String redirectUrl;
        String issuerId;
        String scopes = "";
        String flowTypeName;
        if (loadFromPrefs) {
            useOauth2 = sharedPreferences.getBoolean("oidc_oauth2only", false);
            clientId = sharedPreferences.getString("oidc_clientId", null);
            clientSecret = sharedPreferences.getString("oidc_clientSecret", null);
            redirectUrl = sharedPreferences.getString("oidc_redirectUrl", null);
            issuerId = sharedPreferences.getString("oidc_issuerId", null);
            scopes = sharedPreferences.getString("oidc_scopes", null);
            flowTypeName =sharedPreferences.getString("oidc_flowType", null);
        } else {
            useOauth2 = getResources().getBoolean(R.bool.oidc_oauth2only);
            clientId = getString(R.string.oidc_clientId);
            clientSecret = getString(R.string.oidc_clientSecret);
            redirectUrl = getString(R.string.oidc_redirectUrl);
            String[] scopesArray = getResources().getStringArray(R.array.oidc_scopes);
            if (scopesArray != null && scopesArray.length > 0) {
                for (String scope : scopesArray) {
                    scopes += " "+scope;
                }
                scopes = scopes.trim();
            }
            flowTypeName = getString(R.string.oidc_flowType);
            issuerId = getString(R.string.oidc_issuerId);
            //extras = parseStringArray(this.context.getResources().getStringArray(R.array.oidc_authextras));
        }

        useOAuthSwitch.setChecked(useOauth2);
        clientIdEdit.setText(clientId);
        clientSecretEdit.setText(clientSecret);
        redirectUriEdit.setText(redirectUrl);
        issuerEdit.setText(issuerId);
        scopesEdit.setText(scopes);

        int index = 0;
        for (OIDCRequestManager.Flows flow : OIDCRequestManager.Flows.values()) {
            if (flow.name().equalsIgnoreCase(flowTypeName)) {
                flowTypeSpinner.setSelection(index);
                break;
            }
            index++;
        }
    }

    public void saveClientConfigurationForm(View view) {
        boolean useOauth2 = useOAuthSwitch.isChecked();
        String clientId = clientIdEdit.getText().toString();
        String clientSecret = clientSecretEdit.getText().toString();
        String redirectUrl = redirectUriEdit.getText().toString().toLowerCase();
        String issuerId = issuerEdit.getText().toString().toLowerCase();
        String scopes = scopesEdit.getText().toString();
        OIDCRequestManager.Flows flowType = (OIDCRequestManager.Flows) flowTypeSpinner.getSelectedItem();

        SharedPreferences sharedPreferences = this.getSharedPreferences("oidc_clientconf", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (checkClientConfigurationForm(clientId, clientSecret, redirectUrl, scopes)) {
            editor.putBoolean("oidc_loadfromprefs", true);
            editor.putBoolean("oidc_oauth2only", useOauth2);
            editor.putString("oidc_clientId", clientId);
            editor.putString("oidc_clientSecret", clientSecret);
            editor.putString("oidc_redirectUrl", redirectUrl);
            editor.putString("oidc_scopes", scopes);
            editor.putString("oidc_flowType", flowType.name());
            editor.putString("oidc_issuerId", issuerId);
            //TODO: snackbar ok
        } else {
            //TODO: snackbar error
            editor.putBoolean("oidc_loadfromprefs", false);
        }
        editor.apply();
        this.finish();
    }

    private boolean checkClientConfigurationForm(String clientId, String secret, String redirectUrl, String scopes) {
        boolean isOk = true;
        if (TextUtils.isEmpty(clientId)){
            clientIdInputLayout.setError(getString(R.string.OIDCOptionsMandatoryError));
            clientIdInputLayout.setErrorEnabled(true);
            isOk = false;
        }
        if (TextUtils.isEmpty(secret)){
            clientSecretInputLayout.setError(getString(R.string.OIDCOptionsMandatoryError));
            clientSecretInputLayout.setErrorEnabled(true);
            isOk = false;
        }
        if (TextUtils.isEmpty(redirectUrl)){
            redirectUriInputLayout.setError(getString(R.string.OIDCOptionsMandatoryError));
            redirectUriInputLayout.setErrorEnabled(true);
            isOk = false;
        }

        String[] scopesArray;
        if (TextUtils.isEmpty(scopes)) {
            scopesArray = null;
        } else {
            scopesArray = scopes.split(" ");
        }

        if (scopesArray == null || scopesArray.length == 0){
            scopesInputLayout.setError(getString(R.string.OIDCOptionsMandatoryError));
            scopesInputLayout.setErrorEnabled(true);
            isOk = false;
        }

        return  isOk;
    }

    //endregion

    //region Adapters

    private class FlowTypesAdapter extends ArrayAdapter<OIDCRequestManager.Flows> {
        public FlowTypesAdapter(Context context, int resource, OIDCRequestManager.Flows[] objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.spinner_selected_item, parent, false);
            }

            OIDCRequestManager.Flows item = getItem(position);
            TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
            textView.setText(String.format(getString(R.string.OIDCFlowTypeOptionHint), item.name()));

            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.spinner_dropdown_item, parent, false);
            }

            OIDCRequestManager.Flows item = getItem(position);
            TextView textView = (TextView) convertView;
            textView.setText(item.name());

            return convertView;
        }
    }

    //endregion
}
