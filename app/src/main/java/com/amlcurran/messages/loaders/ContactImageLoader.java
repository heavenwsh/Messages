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

package com.amlcurran.messages.loaders;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.ContactsContract;

import com.amlcurran.messages.R;

import java.io.IOException;

public class ContactImageLoader {

    private final ContentResolver contentResolver;

    public ContactImageLoader(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    Bitmap getDefaultImage(Resources resources) {
        return ((BitmapDrawable) resources.getDrawable(R.drawable.ic_contact_picture_unknown)).getBitmap();
    }

    public Bitmap getSmallImage(long photoId) {
        Bitmap result = null;
        Uri photoUri = getUri(photoId);
        Cursor cursor = contentResolver.query(photoUri, new String[]{ContactsContract.CommonDataKinds.Photo.PHOTO}, null, null, null);
        if (cursor.moveToFirst()) {
            try {
                byte[] blob = cursor.getBlob(0);
                result = BitmapFactory.decodeByteArray(blob, 0, blob.length);
            } finally {
                cursor.close();
            }
        }
        return result;
    }

    private Uri getUri(long photoId) {
        return ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, photoId);
    }

    public Bitmap getLargeImage(long photoId) {
        Cursor cursor = contentResolver.query(getUri(photoId), new String[]{ContactsContract.CommonDataKinds.Photo.PHOTO_URI}, null, null, null);
        if (cursor.moveToFirst()) {
            Uri displayPhotoUri = Uri.parse(cursor.getString(0));//Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO);
            cursor.close();
            try {
                AssetFileDescriptor fd = contentResolver.openAssetFileDescriptor(displayPhotoUri, "r");
                return BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor());
            } catch (IOException e) {
                return null;
            }
        }
        cursor.close();
        return null;
    }
}