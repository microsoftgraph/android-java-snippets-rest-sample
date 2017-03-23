package com.lnikkila.oidc.minsdkcompat;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by CQX342 on 12/06/2015.
 * @see "http://stackoverflow.com/a/12081355"
 */
public class CompatUri {
    /**
     * Error message presented when a user tries to treat an opaque URI as
     * hierarchical.
     */
    private static final String NOT_HIERARCHICAL
            = "This isn't a hierarchical URI.";

    /**
     * Returns a set of the unique names of all query parameters. Iterating
     * over the set will return the names in order of their first occurrence.
     * Extracted from Uri#getQueryParameterNames() API 22.
     *
     * @throws UnsupportedOperationException if this isn't a hierarchical URI
     * @see Uri#getQueryParameterNames()
     *
     * @return a set of decoded names
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static Set<String> getQueryParameterNames(Uri uri) {
        if (uri.isOpaque()) {
            throw new UnsupportedOperationException(NOT_HIERARCHICAL);
        }

        String query = uri.getEncodedQuery();
        if (query == null) {
            return Collections.emptySet();
        }

        Set<String> names = new LinkedHashSet<>();
        int start = 0;
        do {
            int next = query.indexOf('&', start);
            int end = (next == -1) ? query.length() : next;

            int separator = query.indexOf('=', start);
            if (separator > end || separator == -1) {
                separator = end;
            }

            String name = query.substring(start, separator);
            names.add(Uri.decode(name));

            // Move start to end of name.
            start = end + 1;
        } while (start < query.length());

        return Collections.unmodifiableSet(names);
    }
}
