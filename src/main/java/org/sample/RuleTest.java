package org.sample;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.DroolsError;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderErrors;
import org.drools.rule.*;
import org.drools.rule.Package;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


public class RuleTest {

    @Test
    public void shouldFireAllRules() throws IOException, DroolsParserException {
        RuleBase ruleBase = initialiseDrools();
        WorkingMemory workingMemory = initializeMessageObjects(ruleBase);
        int expectedNumberOfRulesFired = 1;

        int actualNumberOfRulesFired = workingMemory.fireAllRules();

        assertThat(actualNumberOfRulesFired, is(expectedNumberOfRulesFired));
    }

    private RuleBase initialiseDrools() throws IOException, DroolsParserException {
        PackageBuilder packageBuilder = readRuleFiles();
        return addRulesToWorkingMemory(packageBuilder);
    }

    private PackageBuilder readRuleFiles() throws DroolsParserException, IOException {
        PackageBuilder packageBuilder = new PackageBuilder();

        String[] ruleFiles = {"/home/cyber/drools/HelloWorld/src/main/resources/helloWorld.drl",
                "/home/cyber/drools/HelloWorld/src/main/resources/actionRule.drl"};

        for (String ruleFile : ruleFiles) {
            Reader reader = getRuleFileAsReader(ruleFile);
            packageBuilder.addPackageFromDrl(reader);
        }
        assertNoRuleErrors(packageBuilder);

        return packageBuilder;
    }

    private Reader getRuleFileAsReader(String ruleFile) throws FileNotFoundException {
        //InputStream resourceAsStream = getClass().getResourceAsStream(ruleFile);

    	InputStream resourceAsStream = new BufferedInputStream(new FileInputStream(ruleFile));
        return new InputStreamReader(resourceAsStream);
    }

    private RuleBase addRulesToWorkingMemory(PackageBuilder packageBuilder) {
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        Package rulesPackage = packageBuilder.getPackage();
        ruleBase.addPackage(rulesPackage);

        return ruleBase;
    }

    private void assertNoRuleErrors(PackageBuilder packageBuilder) {
        PackageBuilderErrors errors = packageBuilder.getErrors();

        if (errors.getErrors().length > 0) {
            StringBuilder errorMessages = new StringBuilder();
            errorMessages.append("Found errors in package builder\n");
            for (int i = 0; i < errors.getErrors().length; i++) {
                DroolsError errorMessage = errors.getErrors()[i];
                errorMessages.append(errorMessage);
                errorMessages.append("\n");
            }
            errorMessages.append("Could not parse knowledge");

            throw new IllegalArgumentException(errorMessages.toString());
        }
    }

    private WorkingMemory initializeMessageObjects(RuleBase ruleBase) {
        WorkingMemory workingMemory = ruleBase.newStatefulSession();

        createHelloWorld(workingMemory);
        createHighValue(workingMemory);

        return workingMemory;
    }

    private void createHelloWorld(WorkingMemory workingMemory) {
        Message helloMessage = new Message();
        helloMessage.setType("Hello");
        workingMemory.insert(helloMessage);
    }

    private void createHighValue(WorkingMemory workingMemory) {
        Message highValue = new Message();
        highValue.setType("High value");
        highValue.setMessageValue(42);
        workingMemory.insert(highValue);
    }
}