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

package com.amlcurran.messages.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;

import com.amlcurran.messages.R;
import com.amlcurran.messages.core.data.Conversation;
import com.amlcurran.messages.data.InFlightSmsMessage;
import com.amlcurran.messages.preferences.PreferenceStore;
import com.amlcurran.messages.telephony.SmsSender;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class NotificationBuilder {
    private static final long[] VIBRATE_PATTERN = new long[]{0, 200};
    private final NotificationIntentFactory notificationIntentFactory;
    private final StyledTextFactory styledTextFactory;
    private Context context;
    private final PreferenceStore preferenceStore;

    public NotificationBuilder(Context context, PreferenceStore preferenceStore) {
        this.context = context;
        this.preferenceStore = preferenceStore;
        this.notificationIntentFactory = new NotificationIntentFactory(context);
        this.styledTextFactory = new StyledTextFactory();
    }

    public Notification buildUnreadNotification(List<Conversation> conversations, Bitmap photo, boolean fromNewMessage) {
        if (conversations.size() == 1) {
            return buildSingleUnreadNotification(conversations.get(0), photo, fromNewMessage);
        } else {
            return buildMultipleUnreadNotification(conversations, fromNewMessage);
        }
    }

    private Notification buildMultipleUnreadNotification(List<Conversation> conversations, boolean fromNewMessage) {
        long timestampMillis = Calendar.getInstance().getTimeInMillis();
        CharSequence ticker = fromNewMessage ? styledTextFactory.buildTicker(conversations.get(0)) : styledTextFactory.buildListSummary(context, conversations);
        return getDefaultBuilder(fromNewMessage)
                .setTicker(ticker)
                .setStyle(buildInboxStyle(conversations))
                .setContentText(styledTextFactory.buildSenderList(conversations))
                .setContentTitle(styledTextFactory.buildListSummary(context, conversations))
                .setWhen(timestampMillis)
                .build();
    }

    private NotificationCompat.Style buildInboxStyle(List<Conversation> conversations) {
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        for (Conversation conversation : conversations) {
            inboxStyle.addLine(styledTextFactory.getInboxLine(conversation));
        }
        return inboxStyle;
    }

    private Notification buildSingleUnreadNotification(Conversation conversation, Bitmap photo, boolean fromNewMessage) {
        long timestampMillis = Calendar.getInstance().getTimeInMillis();
        CharSequence tickerText = fromNewMessage ? styledTextFactory.buildTicker(conversation) : styledTextFactory.buildListSummary(context, Collections.singletonList(conversation));
        NotificationCompat.Action voiceInputAction = buildReplyAction(conversation);
        return getDefaultBuilder(fromNewMessage)
                .setTicker(tickerText)
                .setContentTitle(conversation.getContact().getDisplayName())
                .setLargeIcon(photo)
                .setContentIntent(notificationIntentFactory.createViewConversationIntent(conversation))
                .setContentText(conversation.getBody())
                .setStyle(buildBigStyle(conversation))
                .setWhen(timestampMillis)
                .extend(new NotificationCompat.WearableExtender().addAction(voiceInputAction))
                .build();
    }

    private NotificationCompat.Action buildReplyAction(Conversation conversation) {
        RemoteInput remoteInput = new RemoteInput.Builder(SmsSender.EXTRA_VOICE_REPLY)
                .setLabel(context.getString(R.string.reply))
                .build();

        Intent replyIntent = new Intent(context, SmsSender.class);
        replyIntent.setAction(SmsSender.ACTION_SEND_REQUEST);
        replyIntent.putExtra(SmsSender.FROM_WEAR, true);
        replyIntent.putExtra(SmsSender.EXTRA_NUMBER, conversation.getAddress());
        PendingIntent replyPendingIntent = PendingIntent.getService(context, 0, replyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Action.Builder(R.drawable.ic_wear_reply,
                 context.getString(R.string.reply), replyPendingIntent)
                .addRemoteInput(remoteInput)
                .build();
    }

    private static NotificationCompat.Style buildBigStyle(Conversation conversation) {
        return new NotificationCompat.BigTextStyle()
                .bigText(conversation.getBody())
                .setBigContentTitle(conversation.getContact().getDisplayName());
    }

    private NotificationCompat.Builder getDefaultBuilder() {
        return getDefaultBuilder(true);
    }

    private NotificationCompat.Builder getDefaultBuilder(boolean shouldSoundAndVibrate) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.context)
                .setContentIntent(notificationIntentFactory.createLaunchActivityIntent())
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notify_sms)
                .setDefaults(Notification.DEFAULT_LIGHTS);

        if (shouldSoundAndVibrate) {
            builder.setSound(preferenceStore.getRingtoneUri());
            builder.setVibrate(VIBRATE_PATTERN);
        }

        return builder;
    }

    public Notification buildFailureToSendNotification(InFlightSmsMessage message) {
        return getDefaultBuilder()
                .setContentTitle(string(R.string.failed_to_send_message))
                .setTicker(string(R.string.failed_to_send_message))
                .setContentText(context.getString(R.string.couldnt_send_to, message.getPhoneNumber().toString()))
                .addAction(R.drawable.ic_action_send_holo, string(R.string.resend), notificationIntentFactory.createResendIntent(message))
                .setSmallIcon(R.drawable.ic_notify_error)
                .build();
    }

    private String string(int resId) {
        return context.getString(resId);
    }

    public Notification buildMmsErrorNotification() {
        return getDefaultBuilder()
                .setContentTitle(string(R.string.mms_error_title))
                .setTicker(string(R.string.mms_error_title))
                .setContentText(string(R.string.mms_error_text))
                .setSmallIcon(R.drawable.ic_notify_error)
                .build();
    }
}
