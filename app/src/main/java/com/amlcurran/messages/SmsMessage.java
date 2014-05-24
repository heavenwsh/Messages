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

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Telephony;

import com.amlcurran.messages.adapters.CursorHelper;

public class SmsMessage implements Parcelable {

    private static final int IS_FROM_OTHER = 1;
    private static final int IS_FROM_ME = 0;
    private final String address;
    private final String body;
    private final long timestamp;
    private final boolean isFromMe;

    public SmsMessage(String address, String body, long timestamp, boolean isFromMe) {
        this.address = address;
        this.body = body;
        this.timestamp = timestamp;
        this.isFromMe = isFromMe;
    }

    private SmsMessage(Parcel in) {
        this.address = in.readString();
        this.body = in.readString();
        this.timestamp = in.readLong();
        this.isFromMe = in.readInt() == IS_FROM_ME;
    }

    public static SmsMessage fromDeliverBroadcast(android.telephony.SmsMessage[] messages) {
        if (messages.length == 0) {
            throw new NullPointerException("Creating SMS message from empty array");
        }
        String address = messages[0].getDisplayOriginatingAddress();
        String body = createBody(messages);
        long timestamp = messages[0].getTimestampMillis();
        return new SmsMessage(address, body, timestamp, false);
    }

    public static SmsMessage fromCursor(Cursor cursor) {
        String body = CursorHelper.asString(cursor, Telephony.Sms.BODY);
        long timestamp = CursorHelper.asLong(cursor, Telephony.Sms.DATE);
        boolean isFromMe = CursorHelper.asInt(cursor, Telephony.Sms.TYPE) == Telephony.Sms.MESSAGE_TYPE_SENT;
        String address = CursorHelper.asString(cursor, Telephony.Sms.ADDRESS);
        return new SmsMessage(address, body, timestamp, isFromMe);
    }

    private static String createBody(android.telephony.SmsMessage[] messages) {
        String result = "";
        for (android.telephony.SmsMessage message : messages) {
            result += message.getDisplayMessageBody();
        }
        return result;
    }

    public String getAddress() {
        return address;
    }

    public String getBody() {
        return body;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isFromMe() {
        return isFromMe;
    }

    @Override
    public String toString() {
        return String.format("SMS with address: %1$s\ntimestamp: %2$d\nbody: %3$s", address, timestamp, body);
    }

    public static final Parcelable.Creator<SmsMessage> CREATOR = new Parcelable.Creator<SmsMessage>() {

        public SmsMessage createFromParcel(Parcel in) {
            return new SmsMessage(in);
        }

        public SmsMessage[] newArray(int size) {
            return new SmsMessage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(body);
        dest.writeLong(timestamp);
        dest.writeInt(isFromMe ? IS_FROM_ME : IS_FROM_OTHER);
    }

    public ContentValues toContentValues(int messageTypeSent) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Telephony.Sms.Inbox.BODY, body);
        contentValues.put(Telephony.Sms.Inbox.ADDRESS, address);
        contentValues.put(Telephony.Sms.Inbox.DATE, timestamp);
        contentValues.put(Telephony.Sms.Inbox.DATE_SENT, timestamp);
        contentValues.put(Telephony.Sms.Inbox.TYPE, messageTypeSent);
        return contentValues;
    }
}
