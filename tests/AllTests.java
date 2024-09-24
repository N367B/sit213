//package tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import sources.*;
import transmetteurs.*;
import destinations.*;
import information.*;
import simulateur.*;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    SourceAleatoireTest.class,
    SourceFixeTest.class,
    TransmetteurParfaitTest.class,
    DestinationFinaleTest.class,
    InformationTest.class,
    SimulateurTest.class,
    EmetteurTest.class,
    TransmetteurAnalogiqueParfaitTest.class,
    RecepteurTest.class,
    TransmetteurAnalogiqueBruiteTest.class
})
public class AllTests {
}
