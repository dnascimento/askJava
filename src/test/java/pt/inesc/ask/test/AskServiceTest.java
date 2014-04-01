package pt.inesc.ask.test;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.inesc.ask.domain.AskException;
import pt.inesc.ask.servlet.AskService;

public class AskServiceTest {
    AskService s;

    public AskServiceTest() {

    }

    @Before
    public void setUp() throws Exception {
        s = new AskService();
    }

    @After
    public void tearDown() throws Exception {
        // TODO flush the stores (invoke command line :( )
    }

    @Test
    public void saveQuestion() throws AskException {
        // TODO
        assertTrue(true);
    }

}
