/**
 * Copyright (c) 2014 by ArcBees Inc., All rights reserved.
 * This source code, and resulting software, is the confidential and proprietary information
 * ("Proprietary Information") and is the intellectual property ("Intellectual Property")
 * of ArcBees Inc. ("The Company"). You shall not disclose such Proprietary Information and
 * shall use it only in accordance with the terms and conditions of any and all license
 * agreements you have entered into with The Company.
 */

package com.arcbees.gaestudio.client.application.visualizer.columnfilter;

import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class ColumnFilterModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        bind(ColumnNamePresenter.MyView.class).to(ColumnNameView.class);
        bind(ColumnFilterPresenter.MyView.class).to(ColumnFilterView.class);
        install(new GinFactoryModuleBuilder().build(ColumnNamePresenterFactory.class));
    }
}