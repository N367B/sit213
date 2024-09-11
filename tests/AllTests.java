package tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import tests.sources.*;

@RunWith(Suite.class)
@SuiteClasses({ 
    SourceFixeTest.class,
    SourceAleatoireTest.class 
})
public class AllTests {
}