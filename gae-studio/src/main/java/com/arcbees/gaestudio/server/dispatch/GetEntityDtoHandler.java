package com.arcbees.gaestudio.server.dispatch;

import javax.inject.Inject;

import com.arcbees.gaestudio.server.dto.mapper.EntityMapper;
import com.arcbees.gaestudio.shared.dispatch.GetEntityDtoAction;
import com.arcbees.gaestudio.shared.dispatch.GetEntityDtoResult;
import com.arcbees.gaestudio.shared.dto.entity.EntityDto;
import com.arcbees.gaestudio.shared.dto.entity.KeyDto;
import com.arcbees.gaestudio.shared.dto.entity.ParentKeyDto;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.shared.ActionException;

public class GetEntityDtoHandler extends AbstractActionHandler<GetEntityDtoAction, GetEntityDtoResult> {

    @Inject
    public GetEntityDtoHandler() {
        super(GetEntityDtoAction.class);
    }

    @Override
    public GetEntityDtoResult execute(GetEntityDtoAction action, ExecutionContext context) throws ActionException {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

        KeyDto keyDto = action.getKeyDto();
        ParentKeyDto parentKeyDto = keyDto.getParentKey();

        Key key;

        if (parentKeyDto != null) {
            Key parentKey = KeyFactory.createKey(parentKeyDto.getKind(), parentKeyDto.getId());

            key = KeyFactory.createKey(parentKey, keyDto.getKind(), keyDto.getId());
        } else {
            key = KeyFactory.createKey(keyDto.getKind(), keyDto.getId());
        }

        Entity entity;

        try {
            entity = datastoreService.get(key);
        } catch (EntityNotFoundException e) {
            throw new ActionException(e);
        }

        GetEntityDtoResult result = new GetEntityDtoResult();
        EntityDto entityDto = EntityMapper.mapDTO(entity);
        result.setEntityDto(entityDto);

        return result;
    }
}
