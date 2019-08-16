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

package com.scottlogic.deg.generator.generation.fieldvaluesources.datetime;

import com.scottlogic.deg.common.profile.constraintdetail.Timescale;
import com.scottlogic.deg.generator.generation.fieldvaluesources.FieldValueSource;
import com.scottlogic.deg.generator.restrictions.DateTimeRestrictions;
import com.scottlogic.deg.generator.utils.FilteringIterator;
import com.scottlogic.deg.generator.utils.RandomNumberGenerator;
import com.scottlogic.deg.generator.utils.UpCastingIterator;

import java.time.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

public class DateTimeFieldValueSource implements FieldValueSource {

    public static final OffsetDateTime ISO_MAX_DATE = OffsetDateTime.of(9999, 12, 31, 23, 59, 59, 999_999_999, ZoneOffset.UTC);
    public static final OffsetDateTime ISO_MIN_DATE = OffsetDateTime.of(1, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);

    private final Timescale granularity;
    private final DateTimeRestrictions restrictions;
    private final Set<Object> blacklist;
    private final OffsetDateTime inclusiveLower;
    private final OffsetDateTime exclusiveUpper;

    public DateTimeFieldValueSource(
        DateTimeRestrictions restrictions,
        Set<Object> blacklist) {

        this.restrictions = restrictions;
        this.granularity = this.restrictions.getGranularity();

        this.inclusiveLower = getInclusiveLowerBounds(restrictions);
        this.exclusiveUpper = getExclusiveUpperBound(restrictions);

        this.blacklist = blacklist;
    }

    @Override
    public Iterable<Object> generateAllValues() {
        return () -> new UpCastingIterator<>(
            new FilteringIterator<>(
                new SequentialDateIterator(
                    inclusiveLower != null ? inclusiveLower : ISO_MIN_DATE,
                    exclusiveUpper != null ? exclusiveUpper : ISO_MAX_DATE,
                    granularity),
                i -> !blacklist.contains(i)));
    }

    @Override
    public Iterable<Object> generateInterestingValues() {

        ArrayList<Object> interestingValues = new ArrayList<>();

        if (restrictions.min != null && restrictions.min.getLimit() != null) {
            OffsetDateTime min = restrictions.min.getLimit();
            interestingValues.add(restrictions.min.isInclusive() ? min : min.plusNanos(1_000_000));
        } else {
            interestingValues.add(OffsetDateTime.of(
                LocalDate.of(1900, 01, 01),
                LocalTime.MIDNIGHT,
                ZoneOffset.UTC));
        }

        if (restrictions.max != null && restrictions.max.getLimit() != null) {
            OffsetDateTime max = restrictions.max.getLimit();
            interestingValues.add(restrictions.max.isInclusive() ? max : max.minusNanos(1_000_000));
        } else {
            interestingValues.add(OffsetDateTime.of(
                LocalDate.of(2100, 01, 01),
                LocalTime.MIDNIGHT,
                ZoneOffset.UTC));
        }

        return () -> new UpCastingIterator<>(
            new FilteringIterator<>(interestingValues.iterator(),
                i -> !blacklist.contains(i)));
    }

    @Override
    public Iterable<Object> generateRandomValues(RandomNumberGenerator randomNumberGenerator) {

        OffsetDateTime lower = inclusiveLower != null
            ? inclusiveLower
            : ISO_MIN_DATE;


        OffsetDateTime upper = exclusiveUpper != null
            ? exclusiveUpper
            : ISO_MAX_DATE.plusNanos(1_000_000);


        return () -> new UpCastingIterator<>(
            new FilteringIterator<>(new RandomDateIterator(lower, upper, randomNumberGenerator, granularity),
                i -> !blacklist.contains(i)));

    }

    private OffsetDateTime getExclusiveUpperBound(DateTimeRestrictions upper) {
        if (upper.max == null || upper.max.getLimit() == null) return null;
        return upper.max.isInclusive() ? upper.max.getLimit().plusNanos(1_000_000) : upper.max.getLimit();
    }

    private OffsetDateTime getInclusiveLowerBounds(DateTimeRestrictions lower) {
        if (lower.min == null || lower.min.getLimit() == null) return null;
        return lower.min.isInclusive() ? lower.min.getLimit() : lower.min.getLimit().plusNanos(1_000_000);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        DateTimeFieldValueSource otherSource = (DateTimeFieldValueSource) obj;
        return restrictions.equals(otherSource.restrictions) &&
            blacklist.equals(otherSource.blacklist) &&
            equals(inclusiveLower, otherSource.inclusiveLower) &&
            equals(exclusiveUpper, otherSource.exclusiveUpper);
    }

    private static boolean equals(OffsetDateTime x, OffsetDateTime y) {
        if (x == null && y == null) {
            return true;
        }

        if (x == null || y == null) {
            return false; //either x OR y is null, but not both (XOR)
        }

        return x.equals(y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(restrictions, blacklist, inclusiveLower, exclusiveUpper);
    }
}
