/**
 * Copyright (c) 2013 by ArcBees Inc., All rights reserved.
 * This source code, and resulting software, is the confidential and proprietary information
 * ("Proprietary Information") and is the intellectual property ("Intellectual Property")
 * of ArcBees Inc. ("The Company"). You shall not disclose such Proprietary Information and
 * shall use it only in accordance with the terms and conditions of any and all license
 * agreements you have entered into with The Company.
 */

package com.arcbees.gaestudio.client.application.widget.message;

import com.arcbees.gaestudio.client.ui.MessageWidget;
import com.arcbees.gaestudio.client.ui.MessageWidgetFactory;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;

public class MessagesView extends ViewImpl implements MessagesPresenter.MyView {
    interface Binder extends UiBinder<Widget, MessagesView> {
    }

    @UiField
    HTMLPanel messages;

    private final MessageWidgetFactory messageWidgetFactory;

    @Inject
    MessagesView(Binder binder,
                 MessageWidgetFactory messageWidgetFactory) {
        this.messageWidgetFactory = messageWidgetFactory;

        initWidget(binder.createAndBindUi(this));
    }

    @Override
    public void addMessage(Message message) {
        MessageWidget messageWidget = messageWidgetFactory.createMessage(message);
        messages.add(messageWidget);
    }
}