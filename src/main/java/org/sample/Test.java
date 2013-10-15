package org.sample;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;

public class Test {

	public static void main(String[] args) throws DroolsParserException, IOException {
		// TODO Auto-generated method stub
		RuleBase ruleBase = initialiseDrools();
        WorkingMemory workingMemory = initializeMessageObjects(ruleBase);

        int actualNumberOfRulesFired = workingMemory.fireAllRules();
	}
	
	private static RuleBase initialiseDrools() throws IOException, DroolsParserException {
	        PackageBuilder packageBuilder = readRuleFiles();
	        return addRulesToWorkingMemory(packageBuilder);
	}
	  
	private static PackageBuilder readRuleFiles() throws DroolsParserException, IOException {
        PackageBuilder packageBuilder = new PackageBuilder();

        //give full path name
        String ruleFile = "/home/cyber/drools/HelloWorld/src/main/resources/helloWorld.drl";
        Reader reader = getRuleFileAsReader(ruleFile);
        packageBuilder.addPackageFromDrl(reader);

        return packageBuilder;
    }

    private static Reader getRuleFileAsReader(String ruleFile) throws FileNotFoundException {
    	
       // InputStream resourceAsStream = getClass().getResourceAsStream(ruleFile);
        InputStream resourceAsStream = new BufferedInputStream(new FileInputStream(ruleFile));
        return new InputStreamReader(resourceAsStream);
    }

    private static RuleBase addRulesToWorkingMemory(PackageBuilder packageBuilder) {
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        Package rulesPackage = packageBuilder.getPackage();
        ruleBase.addPackage(rulesPackage);

        return ruleBase;
    }
    private static WorkingMemory initializeMessageObjects(RuleBase ruleBase) {
        WorkingMemory workingMemory = ruleBase.newStatefulSession();

        createHelloWorld(workingMemory);

        return workingMemory;
    }

    private  static void createHelloWorld(WorkingMemory workingMemory) {
        Message helloMessage = new Message();
        helloMessage.setType("Hello");
        workingMemory.insert(helloMessage);
    }
}
