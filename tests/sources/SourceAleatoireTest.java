package sources;

import information.InformationNonConforme;
import org.junit.Test;
import static org.junit.Assert.*;

public class SourceAleatoireTest {

    @Test
    public final void sAleGen() throws InformationNonConforme {

        SourceAleatoire st1, st2, st3,st4;

        st1 = new SourceAleatoire(1000,000);
        st2 = new SourceAleatoire(1000,000);
        assertEquals(1000,st1.informationGeneree.nbElements());
        assertEquals(st2.informationGeneree,st1.informationGeneree );
        st3 = new SourceAleatoire(1000,111);
        st4 = new SourceAleatoire(1000,222);
        assertNotEquals(st4.informationGeneree,st3.informationGeneree );
        st1 = new SourceAleatoire(null);
        st2 = new SourceAleatoire(null);
        assertEquals(100,st1.informationGeneree.nbElements());
        assertNotEquals(st1.informationGeneree,st2.informationGeneree );

    }

}