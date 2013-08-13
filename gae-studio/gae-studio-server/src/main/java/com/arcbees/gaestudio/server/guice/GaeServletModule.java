/**
 * Copyright (c) 2013 by ArcBees Inc., All rights reserved.
 * This source code, and resulting software, is the confidential and proprietary information
 * ("Proprietary Information") and is the intellectual property ("Intellectual Property")
 * of ArcBees Inc. ("The Company"). You shall not disclose such Proprietary Information and
 * shall use it only in accordance with the terms and conditions of any and all license
 * agreements you have entered into with The Company.
 */

package com.arcbees.gaestudio.server.guice;

import com.arcbees.gaestudio.server.analytic.AnalyticModule;
import com.arcbees.gaestudio.server.recorder.GaeStudioRecorderModule;
import com.arcbees.gaestudio.server.rest.RestModule;
import com.arcbees.gaestudio.server.velocity.VelocityModule;
import com.arcbees.gaestudio.shared.BaseRestPath;
import com.arcbees.gaestudio.shared.rest.EndPoints;
import com.google.inject.servlet.ServletModule;

public class GaeServletModule extends ServletModule {
    private final String restPath;

    GaeServletModule(String restPath) {
        this.restPath = restPath.replace("//", "/");
    }

    @Override
    protected void configureServlets() {
        install(new GaeStudioRecorderModule());
        install(new VelocityModule());
        install(new RestModule());
        install(new AnalyticModule());

        bindConstant().annotatedWith(BaseRestPath.class).to(restPath);

        String baseRestPath = restPath == null ? "/" : "/" + restPath + "/";
        String fullRestPath = (baseRestPath + EndPoints.REST_PATH).replace("//", "/");

        filter(fullRestPath + "*").through(GuiceRestEasyFilterDispatcher.class);
    }
}
