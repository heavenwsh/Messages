/*
 * Copyright 2014 Alex Curran
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.amlcurran.messages;

import android.app.ListFragment;
import android.os.Bundle;

import com.amlcurran.messages.core.events.EventSubscriber;
import com.amlcurran.messages.events.BroadcastEventSubscriber;
import com.amlcurran.messages.loaders.MessagesLoader;
import com.amlcurran.messages.loaders.MessagesLoaderProvider;
import com.espian.utils.ProviderHelper;
import com.github.amlcurran.sourcebinder.SourceBinderAdapter;

/**
 * Defines a fragment that uses cursors from the Telephony API and listens to receiving of new messages
 */
public abstract class ListeningCursorListFragment<T> extends ListFragment implements BroadcastEventSubscriber.Listener {
    protected SourceBinderAdapter<T> adapter;
    private MessagesLoader messageLoader;
    private EventSubscriber messageReceiver;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        messageLoader = new ProviderHelper<MessagesLoaderProvider>(MessagesLoaderProvider.class).get(getActivity()).getMessagesLoader();
        messageReceiver = new BroadcastEventSubscriber(getActivity(), this);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadData(messageLoader, false);
        messageReceiver.startListening(getActions());
    }

    public String[] getActions() {
        return new String[0];
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
        loadData(messageLoader, true);
    }

    public abstract void loadData(MessagesLoader loader, boolean isRefresh);

    protected final void onUiThread(Runnable runnable) {
        getActivity().runOnUiThread(runnable);
    }

}