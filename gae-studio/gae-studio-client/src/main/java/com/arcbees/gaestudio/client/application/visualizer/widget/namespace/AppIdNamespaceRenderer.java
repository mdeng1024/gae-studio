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

package com.arcbees.gaestudio.client.application.visualizer.widget.namespace;

import com.arcbees.gaestudio.shared.dto.entity.AppIdNamespaceDto;
import com.google.common.base.Strings;
import com.google.gwt.text.shared.AbstractRenderer;

public class AppIdNamespaceRenderer extends AbstractRenderer<AppIdNamespaceDto> {
    @Override
    public String render(AppIdNamespaceDto object) {
        String namespace;

        if (object == null) {
            namespace = "All namespaces";
        } else if (Strings.isNullOrEmpty(object.getNamespace())) {
            namespace = "<default>";
        } else {
            namespace = object.getNamespace();
        }

        return namespace;
    }
}
