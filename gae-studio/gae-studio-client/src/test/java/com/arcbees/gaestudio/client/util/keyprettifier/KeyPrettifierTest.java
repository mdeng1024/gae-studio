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

package com.arcbees.gaestudio.client.util.keyprettifier;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.arcbees.gaestudio.client.resources.AppMessages;
import com.arcbees.gaestudio.shared.dto.entity.KeyDto;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;

public class KeyPrettifierTest {
    private final AppMessages appMessages = mock(AppMessages.class);
    private final KeyPrettifier keyPrettifier = new KeyPrettifier(appMessages);

    @Before
    public void before() {
        given(appMessages.keyPrettifyTemplate(anyString(), anyString())).willAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return args[0] + " (" + args[1] + ")";
            }
        });

        given(appMessages.keyPrettifyChildToken()).willReturn(" > ");
    }

    @Test
    public void writeParentKeys_nullKey() {
        // When
        String prettifiedKey = keyPrettifier.prettifyKey(null);

        // Then
        assertEquals("", prettifiedKey);
    }

    @Test
    public void writeParentKeys_noParentKey() {
        // Given
        KeyDto keyDto = mock(KeyDto.class);
        given(keyDto.getParentKey()).willReturn(null);
        given(keyDto.getKind()).willReturn("some_kind");
        given(keyDto.getId()).willReturn(1L);

        // When
        String prettifiedKey = keyPrettifier.prettifyKey(keyDto);

        // Then
        assertEquals("some_kind (1)", prettifiedKey);
    }

    @Test
    public void writeParentKeys_withParentKey() {
        // Given
        KeyDto keyDto = mock(KeyDto.class);
        given(keyDto.getKind()).willReturn("child_kind");
        given(keyDto.getId()).willReturn(2L);

        KeyDto parentKeyDto = mock(KeyDto.class);
        given(parentKeyDto.getKind()).willReturn("parent_kind");
        given(parentKeyDto.getId()).willReturn(1L);

        given(keyDto.getParentKey()).willReturn(parentKeyDto);

        // When
        String prettifiedKey = keyPrettifier.prettifyKey(keyDto);

        // Then
        assertEquals("parent_kind (1) > child_kind (2)", prettifiedKey);
    }

    @Test
    public void prettifyKey_withTwoLevelParentKey() {
        // Given
        KeyDto childKeyDto = mock(KeyDto.class);
        given(childKeyDto.getKind()).willReturn("child_kind");
        given(childKeyDto.getId()).willReturn(3L);

        KeyDto parentKeyDto = mock(KeyDto.class);
        given(parentKeyDto.getKind()).willReturn("parent_kind");
        given(parentKeyDto.getId()).willReturn(1L);

        KeyDto grandParentKeyDto = mock(KeyDto.class);
        given(grandParentKeyDto.getKind()).willReturn("grandparent_kind");
        given(grandParentKeyDto.getName()).willReturn("some_name");

        given(childKeyDto.getParentKey()).willReturn(parentKeyDto);
        given(parentKeyDto.getParentKey()).willReturn(grandParentKeyDto);

        // When
        String prettifiedKey = keyPrettifier.prettifyKey(childKeyDto);

        // Then
        assertEquals("grandparent_kind (some_name) > parent_kind (1) > child_kind (3)", prettifiedKey);
    }
}
