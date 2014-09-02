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

package com.amlcurran.messages.ui.control;

import android.app.Activity;

public interface ViewController {
    boolean backPressed();

    void hideSecondary();

    void showSecondary();

    void setContentView(Activity activity);

    int getMasterFrameId();

    int getSecondaryFrameId();

    boolean shouldPlaceOnBackStack();

    public interface ViewCallback {
        void secondaryVisible();

        void secondaryHidden();

        void defaultsBannerPressed();

        void secondarySliding(float slideOffset);

        void newMessageButtonClicked();
    }
}
