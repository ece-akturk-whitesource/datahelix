package com.scottlogic.deg.generator.cucumber.steps;

import com.fasterxml.jackson.core.JsonParseException;
import com.scottlogic.deg.generator.cucumber.utils.*;
import com.scottlogic.deg.generator.generation.GenerationConfig;
import com.scottlogic.deg.generator.inputs.InvalidProfileException;
import cucumber.api.java.Before;
import cucumber.api.java.en.*;
import org.hamcrest.Matcher;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;

public class GeneralTestStep {

    private final CucumberTestState state;
    private CucumberTestHelper cucumberTestHelper;

    public GeneralTestStep(CucumberTestState state){
        this.state = state;
    }

    @Before
    public void BeforeEach() {
        this.state.clearState();
        this.cucumberTestHelper = new CucumberTestHelper(state);
    }

    @Given("there is a field (.+)$")
    public void thereIsAField(String fieldName) {
        this.state.addField(fieldName);
    }

    @Given("^the following fields exist:$")
    public void thereAreFields(List<String> fields) {
        fields.forEach(this::thereIsAField);
    }

    @When("the generation strategy is {generationStrategy}")
    public void setTheGenerationStrategy(GenerationConfig.DataGenerationType strategy) {
        this.state.dataGenerationType = strategy;
    }

    @When("the combination strategy is {combinationStrategy}")
    public void setTheCombinationStrategy(GenerationConfig.CombinationStrategyType strategy) {
        this.state.combinationStrategyType = strategy;
    }

    @When("the walker type is {walkerType}")
    public void setTheCombinationStrategy(GenerationConfig.TreeWalkerType walkerType) {
        this.state.walkerType = walkerType;
    }

    @Given("the data requested is {generationMode}")
    public void setTheGenerationMode(CucumberGenerationMode generationMode) {
        switch (generationMode) {
            case VIOLATING:
                state.shouldViolate = true;
                break;
            case VALIDATING:
                state.shouldViolate = false;
                break;
            default:
                throw new IllegalArgumentException("Specified generation mode not supported");
        }
    }

    @And("^(.+) is null$")
    public void fieldIsNull(String fieldName) throws Exception{
        this.state.addConstraint(fieldName, "null", null);
    }

    @And("^(.+) is anything but null$")
    public void fieldIsNotNull(String fieldName) throws Exception{
        this.state.addNotConstraint(fieldName, "null", null);
    }

    @Then("^the profile is invalid$")
    public void theProfileIsInvalid() {
        cucumberTestHelper.generateAndGetData();

        Assert.assertThat(
            "Expected invalid profile",
            this.cucumberTestHelper.getThrownExceptions(),
            hasItem(
                either((Matcher)isA(InvalidProfileException.class))
                    .or(isA(JsonParseException.class))
                    .or(isA(IllegalArgumentException.class))
                    .or(isA(ClassCastException.class))));
    }

    @But("^the profile is invalid because \"(.+)\"$")
    public void fieldIsInvalidWithError(String error) {
        cucumberTestHelper.generateAndGetData();

        List<Exception> thrownExceptions = new ArrayList<>(this.cucumberTestHelper.getThrownExceptions());
        Assert.assertThat(
            "Expected invalid profile",
            thrownExceptions,
            hasItem(isA(InvalidProfileException.class)));

        Assert.assertThat(
            thrownExceptions.get(0).getMessage(),
            is(equalTo(error))
        );
    }

    @Then("^I am presented with an error message$")
    public void dataGeneratorShouldError() {
        cucumberTestHelper.generateAndGetData();

        Assert.assertThat(cucumberTestHelper.generatorHasThrownException(), is(true));
    }

    @And("^no data is created$")
    public void noDataIsCreated() {
        List<List<Object>> data = cucumberTestHelper.generateAndGetData();

        if (!cucumberTestHelper.hasDataBeenGenerated()){
            return; //pass
        }

        String serialisedData = String.join(
            "\n",
            data
                .stream()
                .map(row ->
                    String.join(
                        ",",
                        row
                            .stream()
                            .map(cell -> cell == null ? "<null>" : cell.toString())
                            .collect(Collectors.toList())))
                .collect(Collectors.toList()));

        Assert.fail("Some data was generated when none was expected:\n" + serialisedData);
    }

    @Then("^the following data should be generated:$")
    public void theFollowingDataShouldBeGenerated(List<Map<String, String>> expectedResultsTable) {
        GeneratedTestData data = getExpectedAndGeneratedData(expectedResultsTable);

        Assert.assertThat(
            "Exceptions thrown during generation",
            cucumberTestHelper.getThrownExceptions(),
            empty());
        Assert.assertThat(data.generatedData, new RowsMatchAnyOrderMatcher(data.expectedData));
    }

    @Then("^the following data should be generated in order:$")
    public void theFollowingDataShouldBeGeneratedInOrder(List<Map<String, String>> expectedResultsTable) {
        GeneratedTestData data = getExpectedAndGeneratedData(expectedResultsTable);

        Assert.assertThat(
            "Exceptions thrown during generation",
            cucumberTestHelper.getThrownExceptions(),
            empty());
        Assert.assertThat(data.generatedData, equalTo(data.expectedData));
    }

    @Then("^the following data should be included in what is generated:$")
    public void theFollowingDataShouldBeContainedInActual(List<Map<String, String>> expectedResultsTable) {
        GeneratedTestData data = getExpectedAndGeneratedData(expectedResultsTable);

        Assert.assertThat(
            "Exceptions thrown during generation",
            cucumberTestHelper.getThrownExceptions(),
            empty());
        Assert.assertThat(data.generatedData, new RowsPresentMatcher(data.expectedData));
    }

    @Then("^the following data should not be included in what is generated:$")
    public void theFollowingDataShouldNotBeContainedInActual(List<Map<String, String>> expectedResultsTable) {
        GeneratedTestData data = getExpectedAndGeneratedData(expectedResultsTable);

        Assert.assertThat(
            "Exceptions thrown during generation",
            cucumberTestHelper.getThrownExceptions(),
            empty());
        Assert.assertThat(data.generatedData, new RowsAbsentMatcher(data.expectedData));
    }

    private List <List<Object>> getComparableExpectedResults(List<Map<String, String>> expectedResultsTable) {
        return expectedResultsTable
            .stream()
            .map(row -> new ArrayList<>(row.values()))
            .map(row -> row.stream().map(cell -> {
                try {
                    return GeneratorTestUtilities.parseExpected(cell);
                } catch (JsonParseException | InvalidProfileException e) {
                    state.addException(e);
                    return "<exception thrown: " + e.getMessage() + ">";
                }
            }).collect(Collectors.toList()))
            .collect(Collectors.toList());
    }

    private GeneratedTestData getExpectedAndGeneratedData(List<Map<String, String>> expectedResultsTable){
        List <List<Object>> expectedRowsOfResults = getComparableExpectedResults(expectedResultsTable);
        List <List<Object>> data = cucumberTestHelper.generateAndGetData();
        return new GeneratedTestData(expectedRowsOfResults, data);
    }

    class GeneratedTestData {
        List <List<Object>> expectedData;
        List <List<Object>> generatedData;

        GeneratedTestData(List <List<Object>> expectedData, List <List<Object>> generatedData){
            this.expectedData = expectedData;
            this.generatedData = generatedData;
        }
    }
}
