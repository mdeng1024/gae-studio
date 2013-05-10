/**
 * Copyright (c) 2013 by ArcBees Inc., All rights reserved.
 * This source code, and resulting software, is the confidential and proprietary information
 * ("Proprietary Information") and is the intellectual property ("Intellectual Property")
 * of ArcBees Inc. ("The Company"). You shall not disclose such Proprietary Information and
 * shall use it only in accordance with the terms and conditions of any and all license
 * agreements you have entered into with The Company.
 */

package com.arcbees.gaestudio.client.application.visualizer.event;

import com.arcbees.gaestudio.shared.dto.entity.EntityDto;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class EntityDeletedEvent extends GwtEvent<EntityDeletedEvent.EntityDeletedHandler> {
    private EntityDto entityDTO;

    protected EntityDeletedEvent() {
        // Possibly for serialization.
    }

    public EntityDeletedEvent(EntityDto entityDTO) {
        this.entityDTO = entityDTO;
    }

    public static void fire(HasHandlers source, EntityDto entityDTO) {
        EntityDeletedEvent eventInstance = new EntityDeletedEvent(entityDTO);
        source.fireEvent(eventInstance);
    }

    public static void fire(HasHandlers source, EntityDeletedEvent eventInstance) {
        source.fireEvent(eventInstance);
    }

    public interface HasEntityDeletedHandlers extends HasHandlers {
        HandlerRegistration addEntityDeletedHandler(EntityDeletedHandler handler);
    }

    public interface EntityDeletedHandler extends EventHandler {
        public void onEntityDeleted(EntityDeletedEvent event);
    }

    private static final Type<EntityDeletedHandler> TYPE = new Type<EntityDeletedHandler>();

    public static Type<EntityDeletedHandler> getType() {
        return TYPE;
    }

    @Override
    public Type<EntityDeletedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(EntityDeletedHandler handler) {
        handler.onEntityDeleted(this);
    }

    public EntityDto getEntityDTO() {
        return entityDTO;
    }
}
