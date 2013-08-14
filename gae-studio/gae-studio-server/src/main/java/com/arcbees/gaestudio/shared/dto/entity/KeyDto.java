/**
 * Copyright (c) 2013 by ArcBees Inc., All rights reserved.
 * This source code, and resulting software, is the confidential and proprietary information
 * ("Proprietary Information") and is the intellectual property ("Intellectual Property")
 * of ArcBees Inc. ("The Company"). You shall not disclose such Proprietary Information and
 * shall use it only in accordance with the terms and conditions of any and all license
 * agreements you have entered into with The Company.
 */

package com.arcbees.gaestudio.shared.dto.entity;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class KeyDto extends ParentKeyDto {
    private ParentKeyDto parentKey;
    private AppIdNamespaceDto appIdNamespace;

    @SuppressWarnings("unused")
    protected KeyDto() {
    }

    @JsonCreator
    public KeyDto(@JsonProperty("kind") String kind,
                  @JsonProperty("id") Long id,
                  @JsonProperty("parentKey") ParentKeyDto parentKey,
                  @JsonProperty("appIdNamespace") AppIdNamespaceDto appIdNamespace) {
        super(kind, id);

        this.parentKey = parentKey;
        this.appIdNamespace = appIdNamespace;
    }

    public ParentKeyDto getParentKey() {
        return parentKey;
    }

    public void setParentKey(ParentKeyDto parentKeyDto) {
        this.parentKey = parentKeyDto;
    }

    public AppIdNamespaceDto getAppIdNamespace() {
        return appIdNamespace;
    }

    public void setAppIdNamespace(AppIdNamespaceDto appIdNamespace) {
        this.appIdNamespace = appIdNamespace;
    }
}