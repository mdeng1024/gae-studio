/**
 * Copyright (c) 2014 by ArcBees Inc., All rights reserved.
 * This source code, and resulting software, is the confidential and proprietary information
 * ("Proprietary Information") and is the intellectual property ("Intellectual Property")
 * of ArcBees Inc. ("The Company"). You shall not disclose such Proprietary Information and
 * shall use it only in accordance with the terms and conditions of any and all license
 * agreements you have entered into with The Company.
 */

package com.arcbees.gaestudio.client.application.entity.editor;

import javax.inject.Inject;

import com.arcbees.gaestudio.shared.PropertyType;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.TextBox;
import com.google.inject.assistedinject.Assisted;

import static com.arcbees.gaestudio.client.application.entity.editor.PropertyUtil.parseJsonValueWithMetadata;

public class StringPropertyEditor extends AbstractPropertyEditor<String> {
    private final TextBox textBox;
    private final JSONValue property;

    @Inject
    StringPropertyEditor(@Assisted String key,
                         @Assisted JSONValue property) {
        super(key);

        this.property = property;
        textBox = new TextBox();

        initFormWidget(textBox);
        setValue(PropertyUtil.getPropertyValue(property).isString().stringValue());
    }

    @Override
    public JSONValue getJsonValue() {
        JSONString value = new JSONString(getValue());
        Boolean isIndexed = PropertyUtil.isPropertyIndexed(property);
        PropertyType propertyType = PropertyUtil.getPropertyType(property);

        return parseJsonValueWithMetadata(value, propertyType, isIndexed);
    }

    private void setValue(String value) {
        textBox.getElement().setAttribute("value", value);
    }

    private String getValue() {
        return textBox.getValue();
    }
}