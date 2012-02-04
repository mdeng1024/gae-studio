/**
 * Copyright 2012 ArcBees Inc.  All rights reserved.
 */

package com.arcbees.gaestudio.client.application.profiler;

import com.arcbees.gaestudio.shared.dto.DbOperationRecord;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

import java.util.HashSet;

public class StatisticsPresenter extends PresenterWidget<StatisticsPresenter.MyView>
        implements DbOperationRecordProcessor {

    public interface MyView extends View {
        void updateRequestCount(Integer requestCount);
        void updateStatementCount(Integer statementCount);
        void updateTotalExecutionTimeMs(Integer totalExecutionTimeMs);
    }

    private final DispatchAsync dispatcher;
    
    private final HashSet<Long> knownRequestIds;
    private Integer statementCount;
    private Integer totalExecutionTimeMs;

    @Inject
    public StatisticsPresenter(final EventBus eventBus, final MyView view, final DispatchAsync dispatcher) {
        super(eventBus, view);

        this.dispatcher = dispatcher;

        this.knownRequestIds = new HashSet<Long>();
        this.statementCount = 0;
        this.totalExecutionTimeMs = 0;
    }

    @Override
    public void processDbOperationRecord(DbOperationRecord record) {
        final long requestId = record.getRequestId();
        
        knownRequestIds.add(requestId);
        statementCount++;
        totalExecutionTimeMs += record.getExecutionTimeMs();
    }

    @Override
    public void displayNewDbOperationRecords() {
        getView().updateRequestCount(knownRequestIds.size());
        getView().updateStatementCount(statementCount);
        getView().updateTotalExecutionTimeMs(totalExecutionTimeMs);
    }
}