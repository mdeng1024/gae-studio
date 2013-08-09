/**
 * Copyright (c) 2013 by ArcBees Inc., All rights reserved.
 * This source code, and resulting software, is the confidential and proprietary information
 * ("Proprietary Information") and is the intellectual property ("Intellectual Property")
 * of ArcBees Inc. ("The Company"). You shall not disclose such Proprietary Information and
 * shall use it only in accordance with the terms and conditions of any and all license
 * agreements you have entered into with The Company.
 */

package com.arcbees.gaestudio.shared.dto;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import com.arcbees.gaestudio.shared.dto.stacktrace.StackTraceElementDto;
import com.google.gwt.user.client.rpc.IsSerializable;

@JsonSubTypes({@JsonSubTypes.Type(value = DeleteRecordDto.class, name = "delete"),
        @JsonSubTypes.Type(value = GetRecordDto.class, name = "get"),
        @JsonSubTypes.Type(value = PutRecordDto.class, name = "put")})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class DbOperationRecordDto implements IsSerializable {
    private StackTraceElementDto callerStackTraceElement;
    private Long requestId;
    private Long statementId;
    private Integer executionTimeMs;

    @SuppressWarnings("unused")
    protected DbOperationRecordDto() {
        this.requestId = -1L;
        this.statementId = -1L;
        this.executionTimeMs = -1;
    }

    protected DbOperationRecordDto(StackTraceElementDto callerStackTraceElement,
                                   Long requestId,
                                   Long statementId,
                                   Integer executionTimeMs) {
        this.callerStackTraceElement = callerStackTraceElement;
        this.requestId = requestId;
        this.statementId = statementId;
        this.executionTimeMs = executionTimeMs;
    }

    public StackTraceElementDto getCallerStackTraceElement() {
        return callerStackTraceElement;
    }

    public Long getRequestId() {
        return requestId;
    }

    public Long getStatementId() {
        return statementId;
    }

    public Integer getExecutionTimeMs() {
        return executionTimeMs;
    }

    void setCallerStackTraceElement(StackTraceElementDto callerStackTraceElement) {
        this.callerStackTraceElement = callerStackTraceElement;
    }

    void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    void setStatementId(Long statementId) {
        this.statementId = statementId;
    }

    void setExecutionTimeMs(Integer executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }
}
