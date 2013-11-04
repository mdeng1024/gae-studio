/**
 * Copyright (c) 2013 by ArcBees Inc., All rights reserved.
 * This source code, and resulting software, is the confidential and proprietary information
 * ("Proprietary Information") and is the intellectual property ("Intellectual Property")
 * of ArcBees Inc. ("The Company"). You shall not disclose such Proprietary Information and
 * shall use it only in accordance with the terms and conditions of any and all license
 * agreements you have entered into with The Company.
 */

package com.arcbees.gaestudio.client.application.visualizer.widget.entity;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class PropertyEditorCollectionWidget implements TakesValue<Map<String, ?>>, IsWidget {
    private final Map<String, PropertyEditor<?>> propertyEditors;
    private final JSONObject propertyMap;
    private final FlowPanel panel;

    PropertyEditorCollectionWidget(Map<String, PropertyEditor<?>> propertyEditors,
                                   JSONObject propertyMap) {
        this.propertyEditors = propertyEditors;
        this.propertyMap = propertyMap;
        panel = new FlowPanel();

        for (PropertyEditor<?> propertyEditor : propertyEditors.values()) {
            panel.add(propertyEditor);
        }
    }

    public void flush() {
        for (Entry<String, PropertyEditor<?>> entry : propertyEditors.entrySet()) {
            String key = entry.getKey();
            PropertyEditor<?> propertyEditor = entry.getValue();

            JSONValue newJsonValue = propertyEditor.getJsonValue();

            propertyMap.put(key, newJsonValue);
        }
    }

    @Override
    public Widget asWidget() {
        return panel;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setValue(Map<String, ?> value) {
        for (Entry<String, ?> entry : value.entrySet()) {
            String key = entry.getKey();
            if (propertyEditors.containsKey(key)) {
                PropertyEditor<Object> propertyEditor = (PropertyEditor<Object>) propertyEditors.get(key);
                propertyEditor.setValue(entry.getValue());
            }
        }
    }

    @Override
    public Map<String, ?> getValue() {
        Map<String, Object> values = Maps.newHashMap();

        for (Entry<String, ?> entry : propertyEditors.entrySet()) {
            values.put(entry.getKey(), entry.getValue());
        }

        return values;
    }
}