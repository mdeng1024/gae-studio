/**
 * Copyright (c) 2013 by ArcBees Inc., All rights reserved.
 * This source code, and resulting software, is the confidential and proprietary information
 * ("Proprietary Information") and is the intellectual property ("Intellectual Property")
 * of ArcBees Inc. ("The Company"). You shall not disclose such Proprietary Information and
 * shall use it only in accordance with the terms and conditions of any and all license
 * agreements you have entered into with The Company.
 */

package com.google.appengine.api.datastore;

import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.gson.Gson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Testing deserialization of an Entity from Json
 *
 * @link https://developers.google.com/appengine/docs/java/tools/localunittesting for detail on unit testing with
 * appengine
 */
public class EntityDeserializationTest {
    private static final String jsonEntity = "{\n" +
            "  \"key\": {\n" +
            "    \"parentKey\": null,\n" +
            "    \"kind\": \"Complex\",\n" +
            "    \"appId\": null,\n" +
            "    \"id\": 4,\n" +
            "    \"name\": null,\n" +
            "    \"appIdNamespace\": {\n" +
            "      \"appId\": \"gae-studio\",\n" +
            "      \"namespace\": \"\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"propertyMap\": {\n" +
            "    \"embeddedObject.titre\": \"Object #1\",\n" +
            "    \"date\": \"Jun 14, 2012 4:43:27 PM\",\n" +
            "    \"sprocketKey\": {\n" +
            "      \"parentKey\": null,\n" +
            "      \"kind\": \"Sprocket\",\n" +
            "      \"appId\": null,\n" +
            "      \"id\": 3,\n" +
            "      \"name\": null,\n" +
            "      \"appIdNamespace\": {\n" +
            "        \"appId\": \"gae-studio\",\n" +
            "        \"namespace\": \"\"\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Before
    public void setUp() {
        helper.setUp();
    }

    @Test
    public void shouldBeAbleToDeserializeAComplexEntity() {
        // Given
        Gson gson = GsonDatastoreFactory.create();

        // When
        Entity entity = gson.fromJson(jsonEntity, Entity.class);

        // Then
        assertEquals("Complex", entity.getKind());
        assertEquals(4, entity.getKey().getId());
        assertEquals("Object #1", entity.getProperty("embeddedObject.titre"));
        assertEquals("Jun 14, 2012 4:43:27 PM", entity.getProperty("date"));
        assertTrue(entity.getProperty("sprocketKey") instanceof Key);
    }
}