/**
 * Copyright 2013 ArcBees Inc.
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

package com.arcbees.gaestudio.server.dispatch;

import com.google.apphosting.api.ApiProxy;

// TODO externalize magic strings
public class DispatchHelper {
    @SuppressWarnings("unused")
    private DispatchHelper() {
    }

    /**
     * Disable API hooks for the current request.  This is useful to prevent activity originated
     * by GAE Studio itself from showing in the profiler.
     */
    public static void disableApiHooks() {
        ApiProxy.getCurrentEnvironment().getAttributes().put("GaeStudio.disableApiHooks", true);
    }
}
