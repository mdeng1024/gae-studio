package com.arcbees.gaestudio.client.application.profiler;

import com.arcbees.core.client.mvp.ViewWithUiHandlers;
import com.arcbees.core.client.mvp.uihandlers.UiHandlersStrategy;
import com.arcbees.gaestudio.client.Resources;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import java.util.HashMap;

// TODO see if I can factor out some of the common logic in statement and request view
public class RequestView extends ViewWithUiHandlers<RequestUiHandlers> implements RequestPresenter.MyView {

    public interface Binder extends UiBinder<Widget, RequestView> {
    }

    @UiField
    HTMLPanel requestList;

    @UiField(provided = true)
    Resources resources;

    private final HashMap<Long, String> requestElementIds = new HashMap<Long, String>();
    private final NumberFormat numberFormat = NumberFormat.getFormat("0.000s");// TODO externalize this

    @Inject
    public RequestView(final Binder uiBinder, final UiHandlersStrategy<RequestUiHandlers> uiHandlersStrategy, final Resources resources) {
        super(uiHandlersStrategy);
        this.resources = resources;
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void updateRequests(Iterable<RequestPresenter.RequestStatistics> requestStatistics) {
        for (final RequestPresenter.RequestStatistics request : requestStatistics) {
            StringBuilder builder = new StringBuilder();
            final Long requestId = request.getRequestId();

            builder.append("Request #");
            builder.append(requestId);
            builder.append(" - ");
            builder.append(numberFormat.format(request.getExecutionTimeMs() / 1000.0));
            builder.append(" [");
            builder.append(request.getStatementCount());
            builder.append("]");

            if (requestElementIds.containsKey(requestId)) {
                String elementId = requestElementIds.get(requestId);
                HTML html = createRequestElement(builder.toString(), requestId, elementId);
                requestList.addAndReplaceElement(html, elementId);
            } else {
                String elementId = HTMLPanel.createUniqueId();
                HTML html = createRequestElement(builder.toString(), requestId, elementId);
                requestElementIds.put(requestId, elementId);
                requestList.add(html);
            }
        }
    }

    private HTML createRequestElement(String content, final Long requestId, String elementId) {
        HTML html = new HTML(content);

        html.getElement().setId(elementId);
        html.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                getUiHandlers().onRequestClicked(requestId);
            }
        });

        return html;
    }

}
