/**
 * Copyright (c) 2013 by ArcBees Inc., All rights reserved.
 * This source code, and resulting software, is the confidential and proprietary information
 * ("Proprietary Information") and is the intellectual property ("Intellectual Property")
 * of ArcBees Inc. ("The Company"). You shall not disclose such Proprietary Information and
 * shall use it only in accordance with the terms and conditions of any and all license
 * agreements you have entered into with The Company.
 */


package com.arcbees.gaestudio.client.application.profiler.widget.filter;

import com.arcbees.gaestudio.client.resources.AppConstants;
import com.arcbees.gaestudio.client.resources.AppResources;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class FiltersView extends ViewWithUiHandlers<FiltersUiHandlers> implements FiltersPresenter.MyView,
        ChangeHandler {
    interface Binder extends UiBinder<Widget, FiltersView> {
    }

    @UiField(provided = true)
    AppResources resources;
    @UiField
    SimplePanel request;
    @UiField
    SimplePanel method;
    @UiField
    ListBox filters;
    @UiField
    SimplePanel type;

    private final AppConstants myConstants;
    private Filter currentlySelectedFilter = Filter.REQUEST;

    @Inject
    FiltersView(Binder uiBinder,
                AppResources resources,
                AppConstants myConstants) {
        this.resources = resources;
        this.myConstants = myConstants;

        initWidget(uiBinder.createAndBindUi(this));
        initFilters();
    }

    @Override
    public void onChange(ChangeEvent event) {
        int selectedIndex = filters.getSelectedIndex();
        Filter filter = Filter.valueOf(filters.getValue(selectedIndex));
        selectFilter(filter);
    }

    @Override
    public Filter getCurrentlyDisplayedFilter() {
        return currentlySelectedFilter;
    }

    @Override
    public void setInSlot(Object slot, IsWidget content) {
        if (slot == FiltersPresenter.TYPE_SetRequestFilter) {
            request.setWidget(content);
        } else if (slot == FiltersPresenter.TYPE_MethodFilter) {
            method.setWidget(content);
        } else if (slot == FiltersPresenter.TYPE_TypeFilter) {
            type.setWidget(content);
        }
    }

    private void initFilters() {
        filters.addItem(myConstants.filterByRequest(), Filter.REQUEST.toString());
        filters.addItem(myConstants.filterByMethod(), Filter.METHOD.toString());
        filters.addItem(myConstants.filterByType(), Filter.TYPE.toString());
        filters.addChangeHandler(this);
    }

    private void selectFilter(Filter filter) {
        if (filter != currentlySelectedFilter) {
            getPanelFromFilter(currentlySelectedFilter).setVisible(false);
            currentlySelectedFilter = filter;
            getUiHandlers().changeFilter();
            getPanelFromFilter(filter).setVisible(true);
        }
    }

    private Widget getPanelFromFilter(Filter filter) {
        switch (filter) {
            case METHOD:
                return method;
            case TYPE:
                return type;
            default:
                return request;
        }
    }
}
