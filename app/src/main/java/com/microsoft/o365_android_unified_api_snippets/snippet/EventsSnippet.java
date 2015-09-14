package com.microsoft.o365_android_unified_api_snippets.snippet;

import com.microsoft.unifiedapi.service.UnifiedEventsService;

import retrofit.*;
import retrofit.Callback;

import static com.microsoft.o365_android_unified_api_snippets.R.array.get_user_events;


public abstract class EventsSnippet<Result> extends AbstractSnippet<UnifiedEventsService, Result> {

    public EventsSnippet(Integer descriptionArray) {
        super(SnippetCategory.eventsSnippetCategory, descriptionArray);
    }

    static EventsSnippet[] getEventsSnippets() {
        return new EventsSnippet[]{
                // Marker element
                new EventsSnippet(null) {

                    @Override
                    public void request(UnifiedEventsService o, Callback callback) {

                    }
                },

                /*
                 * POST a new OneNote page in the section picked by the user
                 * HTTP POST https://www.onenote.com/api/beta/me/notes/sections/{id}/pages
                 * @see http://dev.onenote.com/docs#/reference/post-pages
                 */
                new EventsSnippet<String>(get_user_events) {

                    @Override
                    public void request(UnifiedEventsService unifiedEventsService, retrofit.Callback<String> callback) {
                        unifiedEventsService.getEvents(getVersion(), callback);
                    }


                }
        };
    }


    public abstract void request(UnifiedEventsService unifiedEventsService, Callback<Result> callback);
}
