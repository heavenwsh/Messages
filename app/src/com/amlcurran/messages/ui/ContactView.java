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

package com.amlcurran.messages.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amlcurran.messages.R;
import com.amlcurran.messages.conversationlist.ConversationModalMarshall;
import com.amlcurran.messages.conversationlist.PhotoLoadListener;
import com.amlcurran.messages.core.data.Contact;
import com.amlcurran.messages.loaders.MessagesLoader;

public class ContactView extends LinearLayout {

    private final ImageView contactImageView;
    private final TextView nameTextField;
    private final TextView secondTextField;
    private long contactId = -1;
    protected Contact contact;

    public ContactView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContactView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        inflate(LayoutInflater.from(context));

        contactImageView = (ImageView) findViewById(R.id.image);
        nameTextField = (TextView) findViewById(android.R.id.text1);
        secondTextField = (TextView) findViewById(android.R.id.text2);

    }

    protected void inflate(LayoutInflater inflater) {
        inflater.inflate(R.layout.view_contact, this, true);
    }

    public void setContact(final Contact contact, MessagesLoader loader) {
        this.contact = contact;
        this.contactId = contact.getContactId();
        post(new Runnable() {
            @Override
            public void run() {
                nameTextField.setText(contact.getDisplayName());
                secondTextField.setText(contact.getNumber().flatten());
                contactImageView.setAlpha(0f);
            }
        });
        loader.loadPhoto(contact, new PhotoLoadListener() {
            @Override
            public void photoLoaded(Bitmap photo) {
                setPhoto(photo);
            }

            @Override
            public void photoLoadedFromCache(Bitmap photo) {
                setPhoto(photo);
            }

            @Override
            public void beforePhotoLoad(Contact contact) {

            }

            private void setPhoto(Bitmap photo) {
                if (contactId == contact.getContactId() || contactId == -1) {
                    contactImageView.setImageBitmap(photo);
                    contactImageView.animate().alpha(1f).start();
                }
            }
        });
    }

    public void setClickToView(ConversationModalMarshall.Callback callback, boolean clickToView) {
        if (clickToView) {
            enableClick(callback);
        } else {
            disableClick();
        }
    }

    private void disableClick() {
        setOnClickListener(null);
    }

    private void enableClick(ConversationModalMarshall.Callback callback) {
        setOnClickListener(new ViewContactClickListener(contact, callback));
    }
}
