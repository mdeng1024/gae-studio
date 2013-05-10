/**
 * Copyright (c) 2013 by ArcBees Inc., All rights reserved.
 * This source code, and resulting software, is the confidential and proprietary information
 * ("Proprietary Information") and is the intellectual property ("Intellectual Property")
 * of ArcBees Inc. ("The Company"). You shall not disclose such Proprietary Information and
 * shall use it only in accordance with the terms and conditions of any and all license
 * agreements you have entered into with The Company.
 */

package com.arcbees.gaestudio.client.application;

import com.arcbees.gaestudio.client.application.widget.HeaderPresenter;
import com.arcbees.gaestudio.client.application.widget.message.MessagesPresenter;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.client.proxy.RevealRootLayoutContentEvent;

public class ApplicationPresenter extends Presenter<ApplicationPresenter.MyView, ApplicationPresenter.MyProxy> {
    interface MyView extends View {
    }

    @ContentSlot
    public static final Type<RevealContentHandler<?>> TYPE_SetMainContent = new Type<RevealContentHandler<?>>();

    public static final Object TYPE_SetHeaderContent = new Object();
    public static final Object TYPE_SetMessagesContent = new Object();

    @ProxyStandard
    interface MyProxy extends Proxy<ApplicationPresenter> {
    }

    private final HeaderPresenter headerPresenter;
    private final MessagesPresenter messagesPresenter;

    @Inject
    ApplicationPresenter(EventBus eventBus,
                         MyView view,
                         MyProxy proxy,
                         HeaderPresenter headerPresenter,
                         MessagesPresenter messagesPresenter) {
        super(eventBus, view, proxy);

        this.headerPresenter = headerPresenter;
        this.messagesPresenter = messagesPresenter;
    }

    @Override
    protected void revealInParent() {
        RevealRootLayoutContentEvent.fire(this, this);
    }

    @Override
    protected void onBind() {
        super.onBind();

        setInSlot(TYPE_SetHeaderContent, headerPresenter);
        setInSlot(TYPE_SetMessagesContent, messagesPresenter);
    }
}
