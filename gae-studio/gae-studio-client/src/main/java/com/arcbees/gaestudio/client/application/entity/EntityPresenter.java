/**
 * Copyright (c) 2013 by ArcBees Inc., All rights reserved.
 * This source code, and resulting software, is the confidential and proprietary information
 * ("Proprietary Information") and is the intellectual property ("Intellectual Property")
 * of ArcBees Inc. ("The Company"). You shall not disclose such Proprietary Information and
 * shall use it only in accordance with the terms and conditions of any and all license
 * agreements you have entered into with The Company.
 */

package com.arcbees.gaestudio.client.application.entity;

import javax.inject.Inject;

import com.arcbees.gaestudio.client.application.visualizer.VisualizerPresenter;
import com.arcbees.gaestudio.client.dto.entity.EntityDto;
import com.arcbees.gaestudio.client.place.NameTokens;
import com.arcbees.gaestudio.client.resources.AppConstants;
import com.arcbees.gaestudio.client.rest.EntitiesService;
import com.arcbees.gaestudio.client.util.JsoMethodCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;

import static com.arcbees.gaestudio.client.place.ParameterTokens.APP_ID;
import static com.arcbees.gaestudio.client.place.ParameterTokens.ID;
import static com.arcbees.gaestudio.client.place.ParameterTokens.KIND;
import static com.arcbees.gaestudio.client.place.ParameterTokens.NAMESPACE;
import static com.arcbees.gaestudio.client.place.ParameterTokens.PARENT_ID;
import static com.arcbees.gaestudio.client.place.ParameterTokens.PARENT_KIND;

public class EntityPresenter extends Presenter<EntityPresenter.MyView, EntityPresenter.MyProxy> {
    interface MyView extends View {
        void showEntity(EntityDto entityDto);
    }

    @ProxyStandard
    @NameToken(NameTokens.entity)
    interface MyProxy extends ProxyPlace<EntityPresenter> {
    }

    private final DispatchAsync dispatchAsync;
    private final EntitiesService entitiesService;
    private final AppConstants appConstants;

    @Inject
    EntityPresenter(EventBus eventBus,
                    MyView view,
                    MyProxy proxy,
                    DispatchAsync dispatchAsync,
                    EntitiesService entitiesService,
                    AppConstants appConstants) {
        super(eventBus, view, proxy, VisualizerPresenter.SLOT_ENTITY_DETAILS);

        this.dispatchAsync = dispatchAsync;
        this.entitiesService = entitiesService;
        this.appConstants = appConstants;
    }

    @Override
    public void prepareFromRequest(PlaceRequest request) {
        super.prepareFromRequest(request);

        displayEntityFromPlaceRequest(request);
    }

    private void displayEntityFromPlaceRequest(PlaceRequest request) {
        String kind = request.getParameter(KIND, null);
        String id = request.getParameter(ID, null);
        String parentKind = request.getParameter(PARENT_KIND, null);
        String parentId = request.getParameter(PARENT_ID, null);
        String namespace = request.getParameter(NAMESPACE, null);
        String appId = request.getParameter(APP_ID, null);

        String failureMessage = appConstants.failedGettingEntity();
        JsoMethodCallback<EntityDto> methodCallback = new JsoMethodCallback<EntityDto>(failureMessage) {
            @Override
            public void onSuccessReceived(EntityDto result) {
                displayEntityDto(result);
            }
        };

        if (parentKind != null && parentId != null) {
            entitiesService.getEntityWithParent(id, kind, appId, namespace, parentId, parentKind, methodCallback);
        } else {
            entitiesService.getEntity(id, kind, appId, namespace, methodCallback);
        }
    }

    private void displayEntityDto(EntityDto entityDto) {
        getView().showEntity(entityDto);
    }
}
