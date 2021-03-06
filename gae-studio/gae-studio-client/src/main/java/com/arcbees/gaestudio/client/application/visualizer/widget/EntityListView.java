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

package com.arcbees.gaestudio.client.application.visualizer.widget;

import java.util.List;

import com.arcbees.analytics.shared.Analytics;
import com.arcbees.gaestudio.client.application.visualizer.ParsedEntity;
import com.arcbees.gaestudio.client.application.visualizer.columnfilter.ColumnVisibilityConfigHelper;
import com.arcbees.gaestudio.client.application.visualizer.columnfilter.ColumnVisibilityTooltip;
import com.arcbees.gaestudio.client.resources.AppResources;
import com.arcbees.gaestudio.client.resources.CellTableResource;
import com.arcbees.gaestudio.client.resources.PagerResources;
import com.arcbees.gaestudio.client.resources.VisualizerResources;
import com.arcbees.gaestudio.shared.dto.entity.EntityDto;
import com.arcbees.gaestudio.shared.dto.entity.KeyDto;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.query.client.Function;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RowCountChangeEvent;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

import static com.arcbees.gaestudio.client.application.analytics.EventCategories.UI_ELEMENTS;
import static com.google.gwt.query.client.GQuery.$;

public class EntityListView extends ViewWithUiHandlers<EntityListUiHandlers>
        implements EntityListPresenter.MyView, CellPreviewEvent.Handler<ParsedEntity> {
    interface Binder extends UiBinder<Widget, EntityListView> {
    }

    private static final int PAGE_SIZE = 25;
    private static final Range DEFAULT_RANGE = new Range(0, PAGE_SIZE);

    @UiField
    HTMLPanel panel;
    @UiField(provided = true)
    SimplePager pager;
    @UiField(provided = true)
    EntityCellTable<ParsedEntity> entityTable;
    @UiField
    DivElement refresh;
    @UiField
    DivElement byGql;
    @UiField
    TextArea formQuery;
    @UiField
    DivElement formQueryHolder;
    @UiField
    Button runQueryButton;
    @UiField
    Element column;

    private final VisualizerResources visualizerResources;
    private final String pagerButtons;
    private final ParsedEntityColumnCreator columnCreator;
    private final MultiSelectionModel<ParsedEntity> selectionModel;
    private final ColumnVisibilityTooltip columnVisibilityTooltip;
    private final Analytics analytics;

    private HandlerRegistration firstLoadHandlerRegistration;
    private boolean gwtBound;

    @Inject
    EntityListView(
            Binder uiBinder,
            CellTableResource cellTableResource,
            PagerResources pagerResources,
            AppResources appResources,
            VisualizerResources visualizerResources,
            ParsedEntityColumnCreator columnCreator,
            Analytics analytics,
            ColumnVisibilityConfigHelper columnVisibilityConfigHelper,
            ColumnVisibilityTooltip columnVisibilityTooltip) {
        this.columnCreator = columnCreator;
        this.visualizerResources = visualizerResources;
        this.analytics = analytics;
        this.columnVisibilityTooltip = columnVisibilityTooltip;

        pager = new SimplePager(SimplePager.TextLocation.CENTER, pagerResources, false, 1000, true);

        pagerButtons = "." + appResources.styles().pager() + " tbody tr td img";

        entityTable = new EntityCellTable<>(PAGE_SIZE, cellTableResource, appResources, columnVisibilityConfigHelper);
        entityTable.addAttachHandler(new AttachEvent.Handler() {
            @Override
            public void onAttachOrDetach(AttachEvent event) {
                onEditTableAttachedOrDetached(event.isAttached());
            }
        });

        selectionModel = new MultiSelectionModel<>();
        entityTable.setSelectionModel(selectionModel, this);
        entityTable.setLoadingIndicator(null);

        initWidget(uiBinder.createAndBindUi(this));

        pager.setPageSize(PAGE_SIZE);
        pager.setDisplay(entityTable);

        columnCreator.initializeTable(entityTable);

        formQuery.getElement().setPropertyString("placeholder", "(e.g. SELECT * FROM Car WHERE model = 'Honda')");
    }

    @Override
    public void setInSlot(Object slot, IsWidget content) {
        if (slot == EntityListPresenter.SLOT_COLUMN_CONFIG_TOOLTIP) {
            columnVisibilityTooltip.bind($("thead", entityTable), "contextmenu", content);
            columnVisibilityTooltip.bind($(column), "click", content);
        }
    }

    @Override
    public void setTableDataProvider(AsyncDataProvider<ParsedEntity> dataProvider) {
        dataProvider.addDataDisplay(entityTable);
    }

    @Override
    public void setRowCount(Integer count) {
        entityTable.setRowCount(count);
    }

    @Override
    public void setNewKind(String currentKind) {
        panel.setVisible(true);
        entityTable.setVisibleRangeAndClearData(DEFAULT_RANGE, true);
    }

    @Override
    public void hideList() {
        panel.setVisible(false);
    }

    @Override
    public void setData(Range range, List<ParsedEntity> parsedEntities) {
        entityTable.setRowData(range.getStart(), parsedEntities);
    }

    @Override
    public void blockSendingNewRequests() {
        runQueryButton.setEnabled(false);
    }

    @Override
    public void allowSendingNewRequests() {
        runQueryButton.setEnabled(true);
    }

    @Override
    public void addOrReplaceEntities(List<EntityDto> entities) {
        final List<ParsedEntity> selectedEntities = Lists.newArrayList();
        List<ParsedEntity> newParsedEntities = Lists.newArrayList(entityTable.getVisibleItems());

        for (EntityDto entity : entities) {
            int rowIndex = getRowIndex(entity);

            ParsedEntity parsedEntity = new ParsedEntity(entity);
            if (rowIndex == -1) {
                newParsedEntities.add(0, parsedEntity);
            } else {
                newParsedEntities.set(rowIndex, parsedEntity);
            }

            selectedEntities.add(parsedEntity);
        }

        Range range = entityTable.getVisibleRange();
        entityTable.setRowData(range.getStart(), newParsedEntities);

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                selectRows(selectedEntities);
            }
        });
    }

    @Override
    public void setKind(String appId, String namespace, String kind) {
        entityTable.setKind(appId, namespace, kind);
    }

    @Override
    public void updateColumnVisibility() {
        entityTable.updateCellVisibility();
    }

    @Override
    public void removeEntity(EntityDto entityDTO) {
        int rowIndex = getRowIndex(entityDTO);

        if (rowIndex >= 0) {
            Range range = entityTable.getVisibleRange();
            entityTable.setVisibleRangeAndClearData(range, true);
        }
    }

    @Override
    public void addProperty(String propertyName) {
        columnCreator.addPropertyColumn(entityTable, propertyName);
    }

    @Override
    public void redraw() {
        entityTable.redraw();
    }

    @Override
    public void removeKindSpecificColumns() {
        while (entityTable.getColumnCount() > ParsedEntityColumnCreator.getDefaultColumnCount()) {
            removeLastColumn(entityTable);
        }
    }

    @Override
    public void unselectRows() {
        selectionModel.clear();

        getUiHandlers().onRowUnlock();
    }

    @Override
    public void setRowSelected(final String encodedKey) {
        if (!$(entityTable.getTableLoadingSection()).isVisible()) {
            $(entityTable).delay(1, new Function() {
                @Override
                public void f() {
                    doSetRowSelected(encodedKey);
                }
            });
        } else {
            final RowCountChangeEvent.Handler handler = new RowCountChangeEvent.Handler() {
                @Override
                public void onRowCountChange(RowCountChangeEvent event) {
                    doSetRowSelected(encodedKey);
                    firstLoadHandlerRegistration.removeHandler();
                }
            };
            firstLoadHandlerRegistration = entityTable.addRowCountChangeHandler(handler);
        }
    }

    @UiHandler("runQueryButton")
    public void runGqlQuery(ClickEvent event) {
        getUiHandlers().runGqlQuery(formQuery.getText());

        analytics.sendEvent(UI_ELEMENTS, "click")
                .eventLabel("Visualizer -> List View -> Run GQL Query")
                .go();
    }

    @Override
    public void onCellPreview(CellPreviewEvent<ParsedEntity> event) {
        if (BrowserEvents.CLICK.equals(event.getNativeEvent().getType())) {
            ParsedEntity parsedEntity = event.getValue();
            if (event.getNativeEvent().getCtrlKey() || event.getNativeEvent().getShiftKey()) {
                if (selectionModel.isSelected(parsedEntity)) {
                    unselectRow(parsedEntity);
                } else {
                    selectRow(parsedEntity);
                }
            } else {
                if (selectionModel.isSelected(parsedEntity)) {
                    unselectRows();
                } else {
                    unselectRows();
                    selectRow(parsedEntity);
                }
            }
        }
    }

    private void doSetRowSelected(String encodedKey) {
        for (int i = 0; i < entityTable.getVisibleItems().size(); i++) {
            ParsedEntity parsedEntity = entityTable.getVisibleItem(i);
            KeyDto key = parsedEntity.getKey();
            if (key.getEncodedKey().equals(encodedKey)) {
                selectRow(parsedEntity);
                return;
            }
        }
    }

    private void removeLastColumn(CellTable<ParsedEntity> entityTable) {
        int lastColumnIndex = entityTable.getColumnCount() - 1;

        entityTable.removeColumn(lastColumnIndex);
    }

    private int getRowIndex(EntityDto entityDTO) {
        List<ParsedEntity> visibleParsedEntities = entityTable.getVisibleItems();
        int rowIndex = -1;

        boolean isReplace = false;
        int i = 0;
        while (!isReplace && i < visibleParsedEntities.size()) {
            ParsedEntity parsedEntity = visibleParsedEntities.get(i);
            if (parsedEntity.getKey().getEncodedKey().equals(entityDTO.getKey().getEncodedKey())) {
                isReplace = true;
                rowIndex = i;
                parsedEntity.setEntityDto(entityDTO);
            }

            i++;
        }

        return rowIndex;
    }

    private void onEditTableAttachedOrDetached(boolean attached) {
        if (attached && !gwtBound) {
            bindGwtQuery();
            gwtBound = true;
        }
    }

    private void bindGwtQuery() {
        $(pagerButtons).click(new Function() {
            @Override
            public void f() {
                unselectRows();
            }
        });

        $(refresh).click(new Function() {
            @Override
            public void f() {
                refresh();
            }
        });

        $(byGql).click(new Function() {
            @Override
            public void f() {
                toggleGQL();
            }
        });

        $(formQueryHolder).slideUp(0);
    }

    private void refresh() {
        getUiHandlers().refresh();
        unselectRows();

        entityTable.setVisibleRangeAndClearData(DEFAULT_RANGE, true);

        analytics.sendEvent(UI_ELEMENTS, "click")
                .eventLabel("Visualizer -> List View -> Refresh")
                .go();
    }

    private void selectRows(List<ParsedEntity> parsedEntities) {
        selectionModel.clear();

        for (ParsedEntity parsedEntity : parsedEntities) {
            selectionModel.setSelected(parsedEntity, true);
        }

        getUiHandlers().onEntitySelected(selectionModel.getSelectedSet());
    }

    private void selectRow(ParsedEntity parsedEntity) {
        selectionModel.setSelected(parsedEntity, true);

        getUiHandlers().onEntitySelected(selectionModel.getSelectedSet());

        analytics.sendEvent(UI_ELEMENTS, "select")
                .eventLabel("Visualizer -> List View -> Entity Row")
                .go();
    }

    private void unselectRow(ParsedEntity parsedEntity) {
        selectionModel.setSelected(parsedEntity, false);

        if (selectionModel.getSelectedSet().isEmpty()) {
            unselectRows();
        } else {
            getUiHandlers().onEntitySelected(selectionModel.getSelectedSet());
        }
    }

    private void toggleGQL() {
        VisualizerResources.EntityList styles = visualizerResources.entityList();
        $(byGql).toggleClass(styles.open());

        boolean isByGql = $(byGql).hasClass(styles.open());
        if (isByGql) {
            $(formQueryHolder).slideDown(100);
            analytics.sendEvent(UI_ELEMENTS, "open")
                    .eventLabel("Visualizer -> List View -> GQL Query Textarea")
                    .go();
        } else {
            $(formQueryHolder).slideUp(100);

            analytics.sendEvent(UI_ELEMENTS, "close")
                    .eventLabel("Visualizer -> List View -> GQL Query Textarea")
                    .go();
        }

        getUiHandlers().setUseGql(isByGql);
    }
}
