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

package com.arcbees.gaestudio.server.api.visualizer;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.arcbees.gaestudio.server.dto.mapper.EntityMapper;
import com.arcbees.gaestudio.server.guice.GaeStudioResource;
import com.arcbees.gaestudio.server.service.visualizer.GqlService;
import com.arcbees.gaestudio.shared.dto.entity.EntityDto;
import com.arcbees.gaestudio.shared.rest.EndPoints;
import com.arcbees.gaestudio.shared.rest.UrlParameters;
import com.google.appengine.api.datastore.Entity;

@Path(EndPoints.GQL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@GaeStudioResource
public class GqlResource {
    private final GqlService gqlService;
    private final EntityMapper entityMapper;

    @Inject
    GqlResource(
            GqlService gqlService,
            EntityMapper entityMapper) {
        this.gqlService = gqlService;
        this.entityMapper = entityMapper;
    }

    @GET
    public Response executeGqlRequest(@QueryParam(UrlParameters.QUERY) String gqlRequest,
            @QueryParam(UrlParameters.OFFSET) Integer offset,
            @QueryParam(UrlParameters.LIMIT) Integer limit) {
        Iterable<Entity> result = gqlService.executeGqlRequest(gqlRequest, offset, limit);

        List<EntityDto> entitiesDtos = entityMapper.mapEntitiesToDtos(result);

        return Response.ok(entitiesDtos).build();
    }

    @Path(EndPoints.COUNT)
    @GET
    public Response getRequestCount(@QueryParam(UrlParameters.QUERY) String gqlRequest) {
        Iterable<Entity> result = gqlService.executeGqlRequest(gqlRequest, null, null);

        List<EntityDto> entitiesDtos = entityMapper.mapEntitiesToDtos(result);

        return Response.ok(entitiesDtos.size()).build();
    }
}
