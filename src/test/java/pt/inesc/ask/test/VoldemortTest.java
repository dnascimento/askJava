package pt.inesc.ask.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
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
import pt.inesc.ask.domain.QuestionEntry;
import voldemort.undoTracker.SRD;

public class VoldemortTest {
    VoldemortDAO dao = new VoldemortDAO("tcp://localhost:6666");
    LinkedList<String> tags = new LinkedList<String>(Arrays.asList("novo"));
    String questionTitle = "title";
    SRD t = new SRD(69L);

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void question() throws AskException {
        String text1 = "dario";
        String text2 = "surf";
        dao.cleanIndex(t);

        Question q = new Question(questionTitle, tags, "1", "1", text1);
        dao.saveNew(q, t);
        Question q2 = dao.getQuestion(q.getId(), t);
        assertEquals(q, q2);
        // test update
        Question q3 = new Question(questionTitle, tags, "1", "1", text2);
        dao.save(q3, t);
        Question q4 = dao.getQuestion(q.getId(), t);
        assertEquals(q3, q4);
        // delete
        assertTrue(dao.deleteQuestion(q.getId(), t));
        try {
            dao.getQuestion(q.getId(), t);
            fail("Question still exists");
        } catch (AskException e) {
        }
    }


    @Test
    public void answer() throws AskException {
        String author = "dario";
        String text1 = "snowboard";
        String text2 = "surf";
        Answer a = new Answer(questionTitle, author, text1, true, null);
        dao.save(a, t);
        Answer a2 = dao.getAnswer(a.getId(), t);
        assertEquals(a, a2);
        // test update
        a2.setText(text2);
        dao.save(a2, t);
        Answer a4 = dao.getAnswer(a.getId(), t);
        assertEquals(a2, a4);
        assertEquals(a2.getText(), text2);
        // delete
        assertTrue(dao.deleteAnswer(a.getId(), t));
        try {
            dao.getAnswer(a.getId(), t);
            fail("Answer still exists");
        } catch (AskException e) {

        }
    }

    @Test
    public void comment() throws AskException {
        String author = "dario";
        String text1 = "snowboard";
        String text2 = "surf";
        Comment a = new Comment("answerId", text1, author);
        dao.save(a, t);
        Comment a2 = dao.getComment(a.getId(), t);
        assertEquals(a, a2);
        // test update
        a2.setText(text2);
        dao.save(a2, t);
        Comment a4 = dao.getComment(a.getId(), t);
        assertEquals(a2, a4);
        assertEquals(a2.getText(), text2);
        // delete
        assertTrue(dao.deleteComment(a.getId(), t));
        try {
            dao.getComment(a.getId(), t);
            fail("Comment still exists");
        } catch (AskException e) {
        }
    }

    // @Test
    // public void testIndex() throws AskException {
    // Question q = new Question(questionTitle, tags, "1", "1", "dario");
    // dao.saveNew(q, t);
    // for (String tag : tags) {
    // List<QuestionEntry> list = dao.getListQuestions(t, tag);
    // assertTrue(questionExists(list, q.getTitle()));
    // }
    // dao.deleteQuestion(q.getId(), t);
    // }

    private boolean questionExists(List<QuestionEntry> list, String questionTitle) {
        for (QuestionEntry e : list) {
            if (e.getTitle().equals(questionTitle))
                return true;
        }
        return false;
    }
}
