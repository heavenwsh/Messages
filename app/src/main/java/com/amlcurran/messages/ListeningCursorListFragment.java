package com.amlcurran.messages;

import android.app.ListFragment;
import android.database.Cursor;
import android.os.Bundle;

import com.amlcurran.messages.adapters.AdaptiveCursorSource;
import com.amlcurran.messages.loaders.CursorLoadListener;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.loaders.MessagesLoaderProvider;
import com.espian.utils.ProviderHelper;
import com.espian.utils.SourceBinderAdapter;

/**
 * Defines a fragment that uses cursors from the Telephony API and listens to receiving of new messages
 */
public abstract class ListeningCursorListFragment<T> extends ListFragment implements LocalMessageReceiver.Listener {
    protected SourceBinderAdapter<T> adapter;
    protected AdaptiveCursorSource<T> source;
    private MessagesLoader messageLoader;
    private LocalMessageReceiver messageReceiver;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        messageLoader = new ProviderHelper<MessagesLoaderProvider>(MessagesLoaderProvider.class).get(getActivity()).getMessagesLoader();
        messageReceiver = new LocalMessageReceiver(getActivity(), this);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadData(messageLoader);
        messageReceiver.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        messageReceiver.stopListening();
    }

    public MessagesLoader getMessageLoader() {
        return messageLoader;
    }

    @Override
    public void onMessageReceived() {
        loadData(messageLoader);
    }

    public abstract void loadData(MessagesLoader loader);

}
