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

package com.arcbees.gaestudio.client.application.entity.editor;

import java.util.Date;

import javax.inject.Inject;

import com.arcbees.gaestudio.shared.Constants;
import com.arcbees.gaestudio.shared.PropertyType;
import com.google.common.base.Strings;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;
import com.google.inject.assistedinject.Assisted;

import static com.arcbees.gaestudio.client.application.entity.editor.PropertyUtil.parseJsonValueWithMetadata;
import static com.google.gwt.query.client.GQuery.$;

public class DatePropertyEditor extends AbstractPropertyEditor<Date> {
    private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat(Constants.JSON_DATE_FORMAT);

    private final DateBox dateBox;
    private final JSONValue property;

    @Inject
    DatePropertyEditor(
            @Assisted String key,
            @Assisted JSONValue property) {
        super(key);

        this.property = property;

        dateBox = new DateBox();
        dateBox.setFormat(new DefaultFormat(DATE_FORMAT));
        // TODO: Override class .dateBoxPopup
        dateBox.getDatePicker().getElement().getParentElement().getParentElement().getStyle().setZIndex(150);

        initFormWidget(dateBox);

        JSONValue propertyValue = PropertyUtil.getPropertyValue(property);
        if (propertyValue == null || propertyValue.isNull() != null) {
            setValue(null);
        } else {
            setValue(parseDate(propertyValue.isString().stringValue()));
        }
    }

    @Override
    public JSONValue getJsonValue() {
        String formattedDate = getFormattedDate();
        JSONValue value = Strings.isNullOrEmpty(formattedDate) ? JSONNull.getInstance() : new JSONString(formattedDate);
        return parseJsonValueWithMetadata(value, PropertyType.DATE, PropertyUtil.isPropertyIndexed(property));
    }

    @Override
    protected boolean validate() {
        return !$(dateBox).hasClass("dateBoxFormatError");
    }

    private Date getValue() {
        return dateBox.getValue();
    }

    private void setValue(Date value) {
        dateBox.setValue(value);
    }

    private String getFormattedDate() {
        String formattedDate = "";
        if (getValue() != null) {
            formattedDate = DATE_FORMAT.format(getValue());
        }

        return formattedDate;
    }

    private Date parseDate(String parsedDate) {
        Date date = new Date();

        if (!Strings.isNullOrEmpty(parsedDate)) {
            try {
                date = DATE_FORMAT.parse(parsedDate);
            } catch (IllegalArgumentException ignored) {
            }
        }

        return date;
    }
}
