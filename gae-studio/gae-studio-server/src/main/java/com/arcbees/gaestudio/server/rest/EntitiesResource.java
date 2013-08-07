/**
 * Copyright (c) 2013 by ArcBees Inc., All rights reserved.
 * This source code, and resulting software, is the confidential and proprietary information
 * ("Proprietary Information") and is the intellectual property ("Intellectual Property")
 * of ArcBees Inc. ("The Company"). You shall not disclose such Proprietary Information and
 * shall use it only in accordance with the terms and conditions of any and all license
 * agreements you have entered into with The Company.
 */

package com.arcbees.gaestudio.server.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.arcbees.gaestudio.server.DatastoreHelper;
import com.arcbees.gaestudio.server.GaConstants;
import com.arcbees.gaestudio.server.dispatch.DispatchHelper;
import com.arcbees.gaestudio.server.dto.entity.AppIdNamespaceDto;
import com.arcbees.gaestudio.server.dto.entity.EntityDto;
import com.arcbees.gaestudio.server.dto.entity.KeyDto;
import com.arcbees.gaestudio.server.dto.entity.ParentKeyDto;
import com.arcbees.gaestudio.server.dto.mapper.EntityMapper;
import com.arcbees.gaestudio.shared.rest.EndPoints;
import com.arcbees.gaestudio.shared.rest.UrlParameters;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.GsonDatastoreFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.common.base.Strings;
import com.google.gson.Gson;

@Path(EndPoints.ENTITIES)
public class EntitiesResource extends GoogleAnalyticResource {
    private static final String GET_ENTITIES_BY_KIND = "Get Entities By Kind";
    private static final String GET_ENTITY_COUNT_BY_KIND = "Get Entity Count By Kind";
    private static final String GET_EMPTY_KIND_ENTITY = "Get Empty Kind Entity";
    private static final String UPDATE_ENTITY = "Update Entity";
    private static final String DELETE_ENTITY = "Delete Entity";
    private static final String GET_ENTITY_DTO = "Get Entity Dto";

    private final DatastoreHelper datastoreHelper;
    private final Logger logger;

    @Inject
    EntitiesResource(DatastoreHelper datastoreHelper,
                     Logger logger) {
        this.datastoreHelper = datastoreHelper;
        this.logger = logger;
    }

    @GET
    public List<EntityDto> getEntities(@QueryParam(UrlParameters.KIND) String kind,
                                       @QueryParam(UrlParameters.OFFSET) Integer offset,
                                       @QueryParam(UrlParameters.LIMIT) Integer limit) {
        googleAnalytic.trackEvent(GaConstants.CAT_SERVER_CALL, GET_ENTITIES_BY_KIND);

        DispatchHelper.disableApiHooks();

        FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();
        if (offset != null) {
            fetchOptions.offset(offset);
        }
        if (limit != null) {
            fetchOptions.limit(limit);
        }

        Query query = new Query(kind);
        Iterable<Entity> results = datastoreHelper.queryOnAllNamespaces(query, fetchOptions);

        List<EntityDto> entities = new ArrayList<EntityDto>();
        for (Entity dbEntity : results) {
            entities.add(EntityMapper.mapDTO(dbEntity));
        }

        return entities;
    }

    @POST
    public EntityDto createEntity(String kind) {
        googleAnalytic.trackEvent(GaConstants.CAT_SERVER_CALL, GET_EMPTY_KIND_ENTITY);

        DispatchHelper.disableApiHooks();
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity emptyEntity = new Entity(kind);
        try {
            Query query = new Query(kind);
            FetchOptions fetchOptions = FetchOptions.Builder.withOffset(0).limit(1);
            Entity entity = datastore.prepare(query).asList(fetchOptions).get(0);
            emptyEntity = setEmptiedProperties(emptyEntity, entity.getProperties());
        } catch (Exception e) {
            // TODO: Be able to generate entity base schema from the pojo that haven't been save yet tot the datastore
            // We will need to create an implementation to support Objectify, Twig persist, etc.
            // For objectify we can use : ObjectifyService.factory().getMetadataForEntity(String kind);
            // And call method metadata.toEntity
        }

        return EntityMapper.mapDTO(emptyEntity);
    }

    @GET
    @Path(EndPoints.ID)
    public EntityDto getEntity(@PathParam(UrlParameters.ID) Long id,
                               @QueryParam(UrlParameters.NAMESPACE) String namespace,
                               @QueryParam(UrlParameters.APPID) String appId,
                               @QueryParam(UrlParameters.KIND) String kind,
                               @QueryParam(UrlParameters.PARENT_ID) Long parentId,
                               @QueryParam(UrlParameters.PARENT_KIND) String parentKind)
            throws EntityNotFoundException {
        googleAnalytic.trackEvent(GaConstants.CAT_SERVER_CALL, GET_ENTITY_DTO);

        ParentKeyDto parentKeyDto = null;
        if (parentId != null && !Strings.isNullOrEmpty(parentKind)) {
            parentKeyDto = new ParentKeyDto(parentKind, parentId);
        }

        KeyDto keyDto = new KeyDto(kind, id, parentKeyDto, new AppIdNamespaceDto(appId, namespace));

        Entity entity = datastoreHelper.get(keyDto);

        return EntityMapper.mapDTO(entity);
    }

    @PUT
    @Path(EndPoints.ID)
    public EntityDto updateEntity(@PathParam(UrlParameters.ID) Long id,
                                  EntityDto entityDto) {
        googleAnalytic.trackEvent(GaConstants.CAT_SERVER_CALL, UPDATE_ENTITY);

        DispatchHelper.disableApiHooks();
        Entity dbEntity;

        Gson gson = GsonDatastoreFactory.create();
        dbEntity = gson.fromJson(entityDto.getJson(), Entity.class);
        dbEntity.getKey();

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(dbEntity);
        logger.info("Entity saved");

        return EntityMapper.mapDTO(dbEntity);
    }

    @DELETE
    @Path(EndPoints.ID)
    public Response deleteEntity(@PathParam(UrlParameters.ID) Long id,
                                 KeyDto keyDto) {
        googleAnalytic.trackEvent(GaConstants.CAT_SERVER_CALL, DELETE_ENTITY);

        DispatchHelper.disableApiHooks();

        AppIdNamespaceDto namespaceDto = keyDto.getAppIdNamespaceDto();
        Key key = KeyFactory.createKey(keyDto.getKind(), keyDto.getId());

        datastoreHelper.delete(key, namespaceDto.getNamespace());

        return Response.ok().build();
    }

    @GET
    @Path(EndPoints.COUNT)
    public Integer getCount(@QueryParam(UrlParameters.KIND) String kind) {
        googleAnalytic.trackEvent(GaConstants.CAT_SERVER_CALL, GET_ENTITY_COUNT_BY_KIND);

        DispatchHelper.disableApiHooks();

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Query query = new Query(kind);
        FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();
        Integer count = datastore.prepare(query).countEntities(fetchOptions);

        return count;
    }

    private Entity setEmptiedProperties(Entity entity,
                                        Map<String, Object> properties) {
        for (Map.Entry<String, Object> property : properties.entrySet()) {
            Object value = property.getValue();

            if (value instanceof Key) {
                value = createEmptyKey((Key) value);
            } else {
                value = createEmptyArbitraryObject(property);
            }
            entity.setProperty(property.getKey(), value);
        }

        return entity;
    }

    private Object createEmptyKey(Key key) {
        return KeyFactory.createKey(key.getKind(), " ");
    }

    private Object createEmptyArbitraryObject(Map.Entry<String, Object> property) {
        try {
            // Reset all property objects in the datastore with a no-args constructor
            return property.getValue().getClass().newInstance();
        } catch (Exception e) {
            // Otherwise set null
            return null;
        }
    }
}
