package com.scottlogic.deg.generator.restrictions;

import com.scottlogic.deg.generator.constraints.IConstraint;
import com.scottlogic.deg.generator.constraints.IsInSetConstraint;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FieldSpecSource {
    final static FieldSpecSource Empty = new FieldSpecSource(Collections.emptySet(), Collections.emptySet());

    private final Set<String> rules;
    private final Set<IConstraint> constraints;

    private FieldSpecSource(Set<String> rules, Set<IConstraint> constraints) {
        this.rules = rules;
        this.constraints = constraints;
    }

    static FieldSpecSource fromConstraint(IConstraint constraint) {
        return new FieldSpecSource(
            Collections.emptySet(),
            Collections.singleton(constraint));
    }

    static FieldSpecSource fromFieldSpecs(FieldSpec left, FieldSpec right) {
        if (left == null){
            return right == null ? FieldSpecSource.Empty : right.getFieldSpecSource();
        }

        if (right == null){
            return left.getFieldSpecSource();
        }

        return left.getFieldSpecSource().combine(right.getFieldSpecSource());
    }

    public String getRule() {
        return this.rules.isEmpty()
            ? null
            : Objects.toString(this.rules);
    }

    public Set<IConstraint> getConstraints() {
        return this.constraints;
    }

    FieldSpecSource combine(FieldSpecSource source) {
        if (this == FieldSpecSource.Empty){
            return source;
        }

        if (source == FieldSpecSource.Empty){
            return this;
        }

        return new FieldSpecSource(
            concat(this.rules, source.rules),
            concat(this.constraints, source.constraints)
        );
    }

    private static <T> Set<T> concat(Collection<T> left, Collection<T> right){
        return Stream.concat(
            left.stream(),
            right.stream()
        ).collect(Collectors.toSet());
    }
}
