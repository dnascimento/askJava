package pt.inesc.ask.test;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.inesc.ask.dao.VoldemortDAO;
import pt.inesc.ask.domain.AskException;
import pt.inesc.ask.domain.Question;

public class VoldemortTest {
    VoldemortDAO dao;

    public VoldemortTest() {

    }

    @Before
    public void setUp() throws Exception {
        dao = new VoldemortDAO();
    }

    @After
    public void tearDown() throws Exception {
        // TODO flush the stores (invoke command line :( )
    }

    @Test
    public void saveQuestion() throws AskException {
        Question q = new Question("title", new LinkedList<String>(), "dario");
        long t = System.currentTimeMillis();
        dao.save(q, t);
        Question q2 = dao.getQuestion(q.id, t);
        assertEquals(q, q2);
    }

    // @Test
    // public void saveAnswer() throws AskException {
    // Answer a = new Answer("title", "dario", "test", true);
    // long t = System.currentTimeMillis();
    // dao.save(a, t);
    // Answer a2 = dao.getAnswer(a.id, t);
    // assertEquals(a, a2);
    // }
    //
    // @Test
    // public void saveComment() throws AskException {
    // Comment c = new Comment("answer1", "commentText", "dario");
    // long t = System.currentTimeMillis();
    // dao.save(c, t);
    // Comment c2 = dao.getComment(c.id, t);
    // assertEquals(c, c2);
    // }
    //
    // @Test
    // public void testIndex() throws AskException {
    // Question q = new Question("title", new LinkedList<String>(), "dario");
    // long t = System.currentTimeMillis();
    // dao.saveNew(q, t);
    // List<Question> list = dao.getListQuestions(t);
    // assertTrue(list.contains(q));
    // }
}
