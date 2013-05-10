/**
 * Copyright (c) 2013 by ArcBees Inc., All rights reserved.
 * This source code, and resulting software, is the confidential and proprietary information
 * ("Proprietary Information") and is the intellectual property ("Intellectual Property")
 * of ArcBees Inc. ("The Company"). You shall not disclose such Proprietary Information and
 * shall use it only in accordance with the terms and conditions of any and all license
 * agreements you have entered into with The Company.
 */

package com.arcbees.gaestudio.client.application.widget.message;

public enum MessageCloseDelay {
    NEVER(0),
    DEFAULT(5000),
    LONG(20000);

    private int delay;

    private MessageCloseDelay(int delay) {
        this.delay = delay;
    }

    public int getDelay() {
        return delay;
    }
}
