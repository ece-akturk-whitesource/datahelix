/*
 * Copyright 2019 Scott Logic Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.scottlogic.datahelix.generator.orchestrator.violation;

import com.scottlogic.datahelix.generator.core.profile.constraints.atomic.ShorterThanConstraint;
import com.scottlogic.datahelix.generator.core.profile.constraints.atomic.OfLengthConstraint;
import com.scottlogic.datahelix.generator.core.violations.filters.ConstraintTypeViolationFilter;
import com.scottlogic.datahelix.generator.core.violations.filters.ViolationFilter;
import com.scottlogic.datahelix.generator.orchestrator.violate.AtomicConstraintTypeMapper;
import com.scottlogic.datahelix.generator.orchestrator.violate.ViolateConfigSource;
import com.scottlogic.datahelix.generator.orchestrator.violate.ViolationFiltersProvider;
import com.scottlogic.datahelix.generator.profile.dtos.constraints.ConstraintType;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ViolationFiltersProviderTest {
    @Test
    void whenNullConstraintsToViolate_ReturnEmptyListOfViolationFilters() {
        ViolateConfigSource configSource = mock(ViolateConfigSource.class);
        when(configSource.getConstraintsToNotViolate()).thenReturn(null);
        ViolationFiltersProvider provider = new ViolationFiltersProvider(configSource, new AtomicConstraintTypeMapper());

        assertThat(provider.get(), is(empty()));
    }

    @Test
    void whenEmptyConstraintsToViolate_ReturnEmptyListOfViolationFilters() {
        ViolateConfigSource configSource = mock(ViolateConfigSource.class);
        when(configSource.getConstraintsToNotViolate()).thenReturn(Collections.emptyList());
        ViolationFiltersProvider provider = new ViolationFiltersProvider(configSource, new AtomicConstraintTypeMapper());

        assertThat(provider.get(), is(empty()));
    }

    @Test
    void hasLengthConstraintsToViolate_ReturnsOneFilter_ThatDoesNotAcceptHasLengthConstraints() {
        ViolateConfigSource configSource = mock(ViolateConfigSource.class);
        when(configSource.getConstraintsToNotViolate())
            .thenReturn(Arrays.asList(ConstraintType.OF_LENGTH));
        ViolationFiltersProvider provider =
            new ViolationFiltersProvider(configSource, new AtomicConstraintTypeMapper());

        List<ViolationFilter> filters = provider.get();
        assertThat(filters, hasSize(1));
        assertThat(filters.get(0), instanceOf(ConstraintTypeViolationFilter.class));
        ConstraintTypeViolationFilter filter = (ConstraintTypeViolationFilter) filters.get(0);


        assertThat(filter.canViolate(
            new OfLengthConstraint(null,2)),
            is(false));

        assertThat(filter.canViolate(
            new ShorterThanConstraint(null, 5)),
            is(true));
    }

    @Test
    void twoConstraintsToViolate_ReturnListWithTwoFilter() {
        ViolateConfigSource configSource = mock(ViolateConfigSource.class);
        when(configSource.getConstraintsToNotViolate())
            .thenReturn(Arrays.asList(ConstraintType.OF_LENGTH, ConstraintType.IN_SET));
        ViolationFiltersProvider provider =
            new ViolationFiltersProvider(configSource, new AtomicConstraintTypeMapper());

        assertThat(provider.get(), hasSize(2));
    }
}