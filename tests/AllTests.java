//package tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import sources.SourceAleatoireTest;
import sources.SourceFixeTest;
import transmetteurs.TransmetteurParfaitTest;
import destinations.DestinationFinaleTest;
import information.InformationTest;
import simulateur.SimulateurTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    SourceAleatoireTest.class,
    SourceFixeTest.class,
    TransmetteurParfaitTest.class,
    DestinationFinaleTest.class,
    InformationTest.class,
    SimulateurTest.class
})
public class AllTests {
}
