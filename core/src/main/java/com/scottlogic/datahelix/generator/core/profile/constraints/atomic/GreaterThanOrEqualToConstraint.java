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

package com.scottlogic.datahelix.generator.core.profile.constraints.atomic;

import com.scottlogic.datahelix.generator.common.profile.Field;
import com.scottlogic.datahelix.generator.core.fieldspecs.FieldSpec;
import com.scottlogic.datahelix.generator.core.fieldspecs.FieldSpecFactory;
import com.scottlogic.datahelix.generator.core.restrictions.linear.Limit;
import com.scottlogic.datahelix.generator.core.restrictions.linear.LinearRestrictions;

import java.math.BigDecimal;
import java.util.Objects;

import static com.scottlogic.datahelix.generator.core.restrictions.linear.LinearRestrictionsFactory.createNumericRestrictions;
import static com.scottlogic.datahelix.generator.core.utils.GeneratorDefaults.NUMERIC_MAX_LIMIT;

public class GreaterThanOrEqualToConstraint implements AtomicConstraint {
    public final Field field;
    public final BigDecimal referenceValue;

    public GreaterThanOrEqualToConstraint(Field field, BigDecimal referenceValue) {
        this.referenceValue = referenceValue;
        this.field = field;
    }

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public AtomicConstraint negate() {
        return new LessThanConstraint(field, referenceValue);
    }

    @Override
    public FieldSpec toFieldSpec() {
        LinearRestrictions<BigDecimal> numericRestrictions = createNumericRestrictions(new Limit<>(referenceValue, true), NUMERIC_MAX_LIMIT);
        return FieldSpecFactory.fromRestriction(numericRestrictions);
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o instanceof ViolatedAtomicConstraint) {
            return o.equals(this);
        }
        if (o == null || getClass() != o.getClass()) return false;
        GreaterThanOrEqualToConstraint constraint = (GreaterThanOrEqualToConstraint) o;
        return Objects.equals(field, constraint.field) && Objects.equals(referenceValue, constraint.referenceValue);
    }

    @Override
    public int hashCode(){
        return Objects.hash(field, referenceValue);
    }

    @Override
    public String toString() {
        return String.format("`%s` >= %s", field.getName(), referenceValue);
    }
}
