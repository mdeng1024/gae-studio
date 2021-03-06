/**
 * Copyright 2015 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.arcbees.gaestudio.client.application;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.arcbees.gaestudio.client.application.version.VersionPresenter;

import static org.mockito.Mockito.verify;

@RunWith(JukitoRunner.class)
public class ApplicationPresenterTest {
    @Inject
    Provider<ApplicationPresenter> provider;

    @Test
    public void onBind_anytime_setsVersionInSlot(ApplicationPresenter.MyView view, VersionPresenter versionPresenter) {
        // when
        provider.get();

        // then
        verify(view).setInSlot(ApplicationPresenter.SLOT_VERSION, versionPresenter);
    }
}
