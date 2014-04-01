package pt.inesc.ask.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.inesc.ask.dao.VoldemortDAO;
import pt.inesc.ask.domain.Answer;
import pt.inesc.ask.domain.AskException;
import pt.inesc.ask.domain.Comment;
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
        dao.save(q, "0");
        Question q2 = dao.getQuestion(q.id, "1");
        assertEquals(q, q2);
    }

    @Test
    public void saveAnswer() throws AskException {
        Answer a = new Answer("title", "dario", "test", true);
        dao.save(a, "0");
        Answer a2 = dao.getAnswer(a.id, "1");
        assertEquals(a, a2);
    }

    @Test
    public void saveComment() throws AskException {
        Comment c = new Comment("answer1", "commentText", "dario");
        dao.save(c, "0");
        Comment c2 = dao.getComment(c.id, "1");
        assertEquals(c, c2);
    }

    @Test
    public void testIndex() throws AskException {
        Question q = new Question("title", new LinkedList<String>(), "dario");
        dao.saveNew(q, "0");
        List<Question> list = dao.getListQuestions("1");
        assertTrue(list.contains(q));
    }
}
