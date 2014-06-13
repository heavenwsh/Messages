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

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.amlcurran.messages.core.TextUtils;
import com.amlcurran.messages.core.data.Sort;

public class PreferenceStore {

    public static final String TAG = PreferenceStore.class.getSimpleName();
    private final SharedPreferences preferences;

    public PreferenceStore(Context context) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public Sort getConversationSort() {
        boolean sort = preferences.getBoolean("unread_priority", false);
        return sort ? Sort.UNREAD : Sort.DEFAULT;
    }

    public Uri getRingtoneUri() {
        String ringtone = preferences.getString("ringtone", null);
        Log.d(TAG, "Ringtone: " + ringtone);
        return TextUtils.isEmpty(ringtone) ? null : Uri.parse(ringtone);
    }
}
