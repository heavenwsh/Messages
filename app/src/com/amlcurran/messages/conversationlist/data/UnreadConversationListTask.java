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

package com.amlcurran.messages.conversationlist.data;

import android.content.ContentResolver;
import android.provider.Telephony;

import com.amlcurran.messages.core.conversationlist.ConversationListListener;
import com.amlcurran.messages.core.conversationlist.Conversation;
import com.amlcurran.messages.core.data.Sort;
import com.amlcurran.messages.loaders.MessagesCache;
import com.amlcurran.messages.loaders.fudges.ConversationListHelperFactory;

import java.util.List;
import java.util.concurrent.Callable;

class UnreadConversationListTask implements Callable<Object> {

    private final String query;
    private final String[] args;
    private final ConversationListListener loadListener;
    private final MessagesCache cache;
    private final ConversationListLoader conversationListLoader;

    public UnreadConversationListTask(ContentResolver contentResolver, ConversationListListener loadListener) {
        this.query = Telephony.Sms.Inbox.READ + "=0";
        this.args = null;
        this.loadListener = loadListener;
        this.cache = MessagesCache.NO_CACHE;
        conversationListLoader = new ConversationListLoader(contentResolver, Sort.DEFAULT, ConversationListHelperFactory.get());
    }

    @Override
    public Object call() throws Exception {
        List<Conversation> conversations = conversationListLoader.loadList(query, args);
        loadListener.onConversationListLoaded(conversations);
        return null;
    }

}
