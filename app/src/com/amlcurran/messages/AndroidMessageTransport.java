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

import com.amlcurran.messages.core.threads.InFlightSmsMessage;
import com.amlcurran.messages.core.threads.MessageTransport;
import com.amlcurran.messages.telephony.SmsSender;

public class AndroidMessageTransport implements MessageTransport {
    private final MessagesApp messagesApp;

    public AndroidMessageTransport(MessagesApp messagesApp) {
        this.messagesApp = messagesApp;
    }

    @Override
    public void send(InFlightSmsMessage message) {
        com.amlcurran.messages.data.InFlightSmsMessage newInFlightSms = new com.amlcurran.messages.data.InFlightSmsMessage(message.getNumber(),
                message.getBody().toString(), message.getTimestamp());
        messagesApp.startService(SmsSender.sendMessageIntent(messagesApp, newInFlightSms));
    }

    @Override
    public void listenToThread(String threadId, TransportCallbacks transportCallbacks) {

    }

    @Override
    public void stopListeningToThread(String threadId) {

    }
}
