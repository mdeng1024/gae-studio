/**
 * Copyright (c) 2013 by ArcBees Inc., All rights reserved.
 * This source code, and resulting software, is the confidential and proprietary information
 * ("Proprietary Information") and is the intellectual property ("Intellectual Property")
 * of ArcBees Inc. ("The Company"). You shall not disclose such Proprietary Information and
 * shall use it only in accordance with the terms and conditions of any and all license
 * agreements you have entered into with The Company.
 */

package com.arcbees.gaestudio.client.application.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class RowLockedEvent extends GwtEvent<RowLockedEvent.RowLockedHandler> {
    public interface RowLockedHandler extends EventHandler {
        void onRowLocked(RowLockedEvent rowLockedEvent);
    }

    public static Type<RowLockedHandler> getType() {
        return TYPE;
    }

    private static final Type<RowLockedHandler> TYPE = new Type<RowLockedHandler>();

    @Override
    public Type<RowLockedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(RowLockedHandler handler) {
        handler.onRowLocked(this);
    }
}
