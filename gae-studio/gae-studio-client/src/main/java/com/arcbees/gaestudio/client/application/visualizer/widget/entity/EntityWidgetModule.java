/**
 * Copyright (c) 2013 by ArcBees Inc., All rights reserved.
 * This source code, and resulting software, is the confidential and proprietary information
 * ("Proprietary Information") and is the intellectual property ("Intellectual Property")
 * of ArcBees Inc. ("The Company"). You shall not disclose such Proprietary Information and
 * shall use it only in accordance with the terms and conditions of any and all license
 * agreements you have entered into with The Company.
 */

package com.arcbees.gaestudio.client.application.visualizer.widget.entity;

import java.util.Date;

import com.arcbees.gaestudio.client.application.visualizer.widget.entity.EntityEditorPresenter.MyView;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

import static com.arcbees.gaestudio.shared.PropertyType.CATEGORY;
import static com.arcbees.gaestudio.shared.PropertyType.EMAIL;
import static com.arcbees.gaestudio.shared.PropertyType.LINK;
import static com.arcbees.gaestudio.shared.PropertyType.PHONE_NUMBER;
import static com.arcbees.gaestudio.shared.PropertyType.POSTAL_ADDRESS;

public class EntityWidgetModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        // TODO: Create additional implementations (with data validation?) for the named editors

        install(new GinFactoryModuleBuilder()
                .implement(new TypeLiteral<PropertyEditor<String>>() {}, StringPropertyEditor.class)
                .implement(new TypeLiteral<PropertyEditor<String>>() {}, Names.named(POSTAL_ADDRESS.name()),
                        StringPropertyEditor.class)
                .implement(new TypeLiteral<PropertyEditor<String>>() {}, Names.named(CATEGORY.name()),
                        StringPropertyEditor.class)
                .implement(new TypeLiteral<PropertyEditor<String>>() {}, Names.named(LINK.name()),
                        StringPropertyEditor.class)
                .implement(new TypeLiteral<PropertyEditor<String>>() {}, Names.named(EMAIL.name()),
                        StringPropertyEditor.class)
                .implement(new TypeLiteral<PropertyEditor<String>>() {}, Names.named(PHONE_NUMBER.name()),
                        StringPropertyEditor.class)
                .implement(new TypeLiteral<PropertyEditor<Long>>() {}, LongPropertyEditor.class)
                .implement(new TypeLiteral<PropertyEditor<Long>>() {}, Names.named("RATING"), LongPropertyEditor.class)
                .implement(new TypeLiteral<PropertyEditor<Double>>() {}, DoublePropertyEditor.class)
                .implement(new TypeLiteral<PropertyEditor<Boolean>>() {}, BooleanPropertyEditor.class)
                .implement(new TypeLiteral<PropertyEditor<Date>>() {}, DatePropertyEditor.class)
                .implement(new TypeLiteral<PropertyEditor<?>>() {}, RawPropertyEditor.class)
                .build(PropertyEditorFactory.class));

        install(new GinFactoryModuleBuilder().build(EntityEditorFactory.class));

        bindSharedView(MyView.class, EntityEditorView.class);
    }
}
