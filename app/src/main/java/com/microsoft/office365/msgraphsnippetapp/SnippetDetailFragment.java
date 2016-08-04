/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package com.microsoft.office365.msgraphsnippetapp;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.ClipboardManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.office365.msgraphsnippetapp.snippet.AbstractSnippet;
import com.microsoft.office365.msgraphsnippetapp.snippet.SnippetContent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Headers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.microsoft.office365.msgraphsnippetapp.R.color.code_1xx;
import static com.microsoft.office365.msgraphsnippetapp.R.color.code_3xx;
import static com.microsoft.office365.msgraphsnippetapp.R.color.code_4xx;
import static com.microsoft.office365.msgraphsnippetapp.R.color.transparent;
import static com.microsoft.office365.msgraphsnippetapp.R.id.btn_run;
import static com.microsoft.office365.msgraphsnippetapp.R.id.progressbar;
import static com.microsoft.office365.msgraphsnippetapp.R.id.txt_desc;
import static com.microsoft.office365.msgraphsnippetapp.R.id.txt_hyperlink;
import static com.microsoft.office365.msgraphsnippetapp.R.id.txt_request_url;
import static com.microsoft.office365.msgraphsnippetapp.R.id.txt_response_body;
import static com.microsoft.office365.msgraphsnippetapp.R.id.txt_response_headers;
import static com.microsoft.office365.msgraphsnippetapp.R.id.txt_status_code;
import static com.microsoft.office365.msgraphsnippetapp.R.id.txt_status_color;
import static com.microsoft.office365.msgraphsnippetapp.R.string.clippy;
import static com.microsoft.office365.msgraphsnippetapp.R.string.req_url;
import static com.microsoft.office365.msgraphsnippetapp.R.string.response_body;
import static com.microsoft.office365.msgraphsnippetapp.R.string.response_headers;

public class SnippetDetailFragment<T, Result>
        extends BaseFragment implements Callback<Result> {

    public static final String ARG_ITEM_ID = "item_id";

    private static final int UNSET = -1;
    private static final String STATUS_COLOR = "STATUS_COLOR";

    private AbstractSnippet<T, Result> mItem;

    //
    // UI component bindings
    //

    /**
     * Displays the status code of the service call
     */
    @InjectView(txt_status_code)
    protected TextView mStatusCode;

    /**
     * Displays the status code as color 'stoplight'
     */
    @InjectView(txt_status_color)
    protected View mStatusColor;

    /**
     * On-screen description of the current snippet
     */
    @InjectView(txt_desc)
    protected TextView mSnippetDescription;

    /**
     * The request url of the current snippet
     */
    @InjectView(txt_request_url)
    protected TextView mRequestUrl;

    /**
     * The response headers of the current snippet's request
     */
    @InjectView(txt_response_headers)
    protected TextView mResponseHeaders;

    /**
     * The response body of the snippet's request
     */
    @InjectView(txt_response_body)
    protected TextView mResponseBody;

    /**
     * Barber's pole progress bar (indeterminate)
     */
    @InjectView(progressbar)
    protected ProgressBar mProgressbar;

    /**
     * The 'run-snippet' button
     */
    @InjectView(btn_run)
    protected Button mRunButton;

    /**
     * Fragment default constructor
     */
    public SnippetDetailFragment() {
        // unimplemented
    }

    //
    // UI event bindings
    //
    @OnClick(txt_request_url)
    public void onRequestUrlClicked(TextView tv) {
        // copy to clip
        clipboard(tv);
    }

    @OnClick(txt_response_headers)
    public void onResponseHeadersClicked(TextView tv) {
        // copy to clip
        clipboard(tv);
    }

    @OnClick(txt_response_body)
    public void onResponseBodyClicked(TextView tv) {
        // copy to clip
        clipboard(tv);
    }

    @OnClick(btn_run)
    public void onRunClicked(Button btn) {
        // disable the button while the snippet is running
        mRunButton.setEnabled(false);

        // clear the old request url
        mRequestUrl.setText("");

        // clear any old headers
        mResponseHeaders.setText("");

        // clear any old response body
        mResponseBody.setText("");

        // reset the status 'stoplight'
        displayStatusCode("",
                getResources()
                        .getColor(transparent)
        );

        // show the indeterminate spinner
        mProgressbar.setVisibility(VISIBLE);

        // actually make the request
        mItem.request(mItem.mService, this);
    }

    @OnClick(txt_hyperlink)
    public void onDocsLinkClicked(TextView textView) {
        launchUrl(Uri.parse(mItem.getUrl()));
    }

    //
    // Lifecycle hooks
    //
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItem = (AbstractSnippet<T, Result>)
                    SnippetContent.ITEMS.get(getArguments().getInt(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_snippet_detail, container, false);
        ButterKnife.inject(this, rootView);
        mSnippetDescription.setText(mItem.getDescription());
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != mStatusColor.getTag()) {
            outState.putInt(STATUS_COLOR, (Integer) mStatusColor.getTag());
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (null != getActivity() && getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (null != activity.getSupportActionBar()) {
                activity.getSupportActionBar().setTitle(mItem.getName());
            }
        }
        if (null != savedInstanceState && savedInstanceState.containsKey(STATUS_COLOR)) {
            int statusColor = savedInstanceState.getInt(STATUS_COLOR, UNSET);
            if (UNSET != statusColor) {
                mStatusColor.setBackgroundColor(statusColor);
                mStatusColor.setTag(statusColor);
            }
        }
    }

    //
    // Custom event bindings
    //
    @Override
    public void onResponse(Call<Result> call, Response<Result> response) {
        if (response.isSuccessful()) {
            if (!isAdded()) {
                // the user has left...
                return;
            }
            mRunButton.setEnabled(true);
            mProgressbar.setVisibility(GONE);
            displayResponse(response);
        } else {
            Timber.e(response.errorBody().toString(), this);
            mRunButton.setEnabled(true);
            mProgressbar.setVisibility(GONE);

            if (null != response.errorBody()) {
                displayResponse(response);
            }
        }
    }

    @Override
    public void onFailure(Call<Result> call, Throwable t) {
        Timber.e(t.getMessage(), this);
        displayThrowable(t);
    }

    //
    // Private methods
    //
    private void clipboard(TextView tv) {
        // which view are we copying to the clipboard?
        int which;

        switch (tv.getId()) {
            case txt_request_url: // the url field
                which = req_url;
                break;

            case txt_response_headers: // the display headers
                which = response_headers;
                break;

            case txt_response_body: // the response body
                which = response_body;
                break;

            default:
                which = UNSET; // don't assign a prefix
        }

        // if we know which view we're copying, prefix it with useful info
        String what = which == UNSET ? "" : getString(which) + " ";

        // concat the clipboard data to this String
        what += getString(clippy);

        // inform the user that data was added to the clipboard
        Toast.makeText(
                getActivity(),
                what,
                Toast.LENGTH_SHORT
        ).show();

        // depending on our API, do it one way or another...
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            // old way
            ClipboardManager clipboardManager = (ClipboardManager)
                    getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setText(tv.getText());
        } else {
            clipboard11(tv);
        }
    }

    @TargetApi(11)
    private void clipboard11(TextView tv) {
        android.content.ClipboardManager clipboardManager =
                (android.content.ClipboardManager) getActivity()
                        .getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("RESTSnippets", tv.getText());
        clipboardManager.setPrimaryClip(clipData);
    }

    private void launchUrl(Uri uri) {
        Intent viewDocs = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(viewDocs);
    }

    private void displayResponse(Response response) {
        int color = getColor(response);
        displayStatusCode(Integer.toString(response.code()), getResources().getColor(color));
        displayRequestUrl(response);
        maybeDisplayResponseHeaders(response);
        maybeDisplayResponseBody(response);
    }

    private void maybeDisplayResponseBody(Response response) {
        if (null != response.body()) {
            String body = null;
            try {
                body = ((ResponseBody) response.body()).string();
                String formattedJson = new JSONObject(body).toString(2);
                mResponseBody.setText(formattedJson);
            } catch (JSONException e) {
                if (null != body) {
                    // body wasn't JSON
                    mResponseBody.setText(body);
                } else {
                    // set the stack trace as the response body
                    displayThrowable(e);
                }
            } catch (IOException ioe) {
                displayThrowable(ioe);
            }
        }
    }

    private void maybeDisplayResponseHeaders(Response response) {
        if (null != response.headers()) {
            Headers headers = response.headers();
            String headerText = "";

            for(int i = 0; i < headers.size(); i++){
                headerText += headers.name(i) + " : " + headers.value(i) + "\n";
            }

            mResponseHeaders.setText(headerText);
        }
    }

    private void displayRequestUrl(Response response) {
        String requestUrl = response.raw().request().url().toString();
        mRequestUrl.setText(requestUrl);
    }

    private void displayStatusCode(String text, int color) {
        mStatusCode.setText(text);
        mStatusColor.setBackgroundColor(color);
        mStatusColor.setTag(color);
    }

    private void displayThrowable(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        String trace = sw.toString();
        mResponseBody.setText(trace);
    }

    private int getColor(Response response) {
        int color;
        switch (response.code() / 100) {
            case 1:
            case 2:
                color = code_1xx;
                break;
            case 3:
                color = code_3xx;
                break;
            case 4:
            case 5:
                color = code_4xx;
                break;
            default:
                color = transparent;
        }
        return color;
    }
}
