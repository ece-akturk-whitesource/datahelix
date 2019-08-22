package com.scottlogic.deg.generator.walker.rowspec;

import com.google.inject.Inject;
import com.scottlogic.deg.common.util.FlatMappingSpliterator;
import com.scottlogic.deg.generator.decisiontree.DecisionTree;
import com.scottlogic.deg.generator.fieldspecs.RowSpec;
import com.scottlogic.deg.generator.generation.databags.DataBag;
import com.scottlogic.deg.generator.generation.databags.DataBagStream;
import com.scottlogic.deg.generator.generation.databags.RowSpecDataBagGenerator;
import com.scottlogic.deg.generator.walker.DecisionTreeWalker;

import java.util.function.Function;
import java.util.stream.Stream;

public class RowSpecDecisionTreeWalker implements DecisionTreeWalker {

    private final RowSpecTreeSolver rowSpecTreeSolver;
    private final RowSpecDataBagGenerator rowSpecDataBagGenerator;

    @Inject
    public RowSpecDecisionTreeWalker(RowSpecTreeSolver rowSpecTreeSolver, RowSpecDataBagGenerator rowSpecDataBagGenerator) {
        this.rowSpecTreeSolver = rowSpecTreeSolver;
        this.rowSpecDataBagGenerator = rowSpecDataBagGenerator;
    }

    @Override
    public DataBagStream walk(DecisionTree tree) {
        return rowSpecTreeSolver.createRowSpecs(tree)
            .map(rowSpecDataBagGenerator::createDataBags)
            .reduce((a, b) -> new DataBagStream(Stream.concat(a.stream(), b.stream()), a.isUnique() || b.isUnique()))
            .orElse(new DataBagStream(Stream.empty(), false));
    }
}
