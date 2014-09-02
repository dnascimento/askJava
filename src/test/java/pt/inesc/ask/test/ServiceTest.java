package pt.inesc.ask.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import pt.inesc.ask.dao.DAO;
import pt.inesc.ask.domain.Answer;
import pt.inesc.ask.domain.AskException;
import pt.inesc.ask.domain.Comment;
import pt.inesc.ask.domain.Question;
import pt.inesc.ask.domain.QuestionEntry;
import pt.inesc.ask.servlet.AskService;
import voldemort.undoTracker.SRD;

public class ServiceTest {
    AskService s;
    String questionId = "title";
    String text = "questionText";
    String author = "author";
    ArrayList<String> tags = new ArrayList<String>(Arrays.asList("testTag", "nice"));
    DAO dao;
    SRD t = new SRD(69L);

    public ServiceTest() {
        s = new AskService();
        this.dao = s.getDao();
    }


    @Test
    public void questionService() throws AskException {
        try {
            s.deleteQuestion(questionId, t);
        } catch (Exception e) {

        }
        s.newQuestion(questionId, text, tags, author, "1", "1", t, null);
        assertNotNull(exists());
        // Test question list
        List<QuestionEntry> list = s.getListQuestions(t, tags.get(0));
        boolean found = false;
        for (QuestionEntry q : list) {
            if (q.getTitle().equals(questionId)) {
                found = true;
            }
        }
        assertTrue(found);
        // delete
        s.deleteQuestion(questionId, t);
        assertNull(exists());
    }


    private Question exists() throws AskException {
        try {
            Map<String, Object> attributes = s.getQuestionData(questionId, t);
            Question q = (Question) attributes.get("questionData");
            if (q == null)
                return null;
            if (q.getUrl().equals(questionId) && q.getTags().equals(tags)) {
                for (String ans : q.getAnswersIDs()) {
                    Answer a = dao.getAnswer(ans, t);
                    a.getText().equals(text);
                    a.getAuthor().equals(author);
                }
                return q;
            } else {
                return null;
            }
        } catch (AskException e) {
            return null;
        }
    }

    @Test
    public void answer() throws AskException {
        String author = "answerAuthor";
        String text1 = "_1answerText";
        String text2 = "_2AnswerText";
        String text3 = "_3AnswerText";
        String newText1 = "new1";
        String newText2 = "new2";
        String newText3 = "new3";
        try {
            s.deleteQuestion(questionId, t);
        } catch (AskException e) {
            // clean old if exists
        }
        s.newQuestion(questionId, text1, new ArrayList<String>(), author, "1", "1", t, null);
        // Test get
        Answer a1 = getQuestionText();
        assertNotNull(a1);
        assertEquals(a1.getAuthor(), author);
        assertEquals(a1.getText(), text1);
        assertEquals(a1.getIsQuestion(), true);
        assertEquals(a1.getVotes(), 0);
        String a1Id = a1.getId();
        // Test add 1 answer
        String a2Id = s.newAnswer(questionId, author, text2, t, null);
        assertNotNull(getQuestionText());
        assertNotNull(getAnswer(author, text2));

        // Test add +1 answer
        String a3Id = s.newAnswer(questionId, author, text3, t, null);
        assertNotNull(getQuestionText());
        assertNotNull(getAnswer(author, text2));
        assertNotNull(getAnswer(author, text3));
        // Test update
        s.updateAnswer(a1Id, newText1, t);
        s.updateAnswer(a2Id, newText2, t);
        s.updateAnswer(a3Id, newText3, t);
        assertNull(getAnswer(author, text2));
        assertNull(getAnswer(author, text3));
        assertEquals(getQuestionText().getText(), newText1);
        assertNotNull(getAnswer(author, newText2));
        assertNotNull(getAnswer(author, newText3));

        // Votes
        s.voteUp(questionId, a1Id, t);
        assertEquals(getQuestionText().getVotes(), 1);
        s.voteDown(questionId, a1Id, t);
        s.voteDown(questionId, a1Id, t);
        assertEquals(getQuestionText().getVotes(), -1);

        s.voteUp(questionId, a3Id, t);
        assertEquals(getAnswer(author, newText3).getVotes(), 1);
        assertEquals(getQuestionText().getVotes(), -1);
        s.voteDown(questionId, a3Id, t);
        assertEquals(getAnswer(author, newText3).getVotes(), 0);
        assertEquals(getQuestionText().getVotes(), -1);

        // Delete
        s.deleteAnswer(questionId, a2Id, t);
        assertNull(getAnswer(author, newText2));
        s.deleteAnswer(questionId, a3Id, t);
        assertNull(getAnswer(author, newText3));
        // delete question
        s.deleteQuestion(questionId, t);
        try {
            dao.getAnswer(a1Id, t);
            fail("After delete question, the text is still there");
        } catch (AskException e) {

        }
    }

    private Answer getQuestionText() throws AskException {
        Map<String, Object> a = s.getQuestionData(questionId, t);
        return (Answer) a.get("question");
    }

    @SuppressWarnings("unchecked")
    private Answer getAnswer(String author, String textAnswer) throws AskException {
        Map<String, Object> a = s.getQuestionData(questionId, t);
        List<Answer> ansList = (List<Answer>) a.get("answers");
        for (Answer ans : ansList) {
            if (ans.getAuthor().equals(author) && ans.getText().equals(textAnswer)) {
                return ans;
            }
        }
        return null;
    }

    @Test
    public void comment() throws AskException {
        String author = "answerAuthor";
        String cText1 = "comment1";
        String cText2 = "comment2";
        try {
            s.deleteQuestion(questionId, t);
        } catch (AskException e) {
            // clean previous questions
        }
        s.newQuestion(questionId, "text", new ArrayList<String>(), "author", "1", "1", t, null);
        String answerId = s.newAnswer(questionId, "authorAnswer", "answerText", t, null);
        String cId1 = s.newComment(questionId, answerId, cText1, author, t);
        // Exists:
        assertNotNull(getComment(answerId, cId1));
        // New
        String cId2 = s.newComment(questionId, answerId, cText2, author, t);

        // Exists:
        assertNotNull(getComment(answerId, cId1));
        assertNotNull(getComment(answerId, cId2));

        // Test content
        Comment c = getComment(answerId, cId1);
        assertEquals(c.getAuthor(), (author));
        assertEquals(c.getText(), (cText1));
        Comment c2 = getComment(answerId, cId2);
        assertEquals(c2.getAuthor(), (author));
        assertEquals(c2.getText(), (cText2));

        // Update
        cText2 = "commentNewText";
        s.updateComment(questionId, answerId, cId2, cText2, t);
        assertNotNull((c2 = getComment(answerId, cId2)));
        assertEquals(c2.getText(), cText2);
        // remain equal
        c = getComment(answerId, cId1);
        assertEquals(c.getAuthor(), (author));
        assertEquals(c.getText(), (cText1));

        // Delete comment
        s.deleteComment(cId1, answerId, t);
        assertNull(getComment(answerId, cId1));
        assertNotNull(getComment(answerId, cId2));

        // Delete answer
        s.deleteAnswer(questionId, answerId, t);
        assertNull(getComment(answerId, cId1));
        assertNull(getComment(answerId, cId2));
        try {
            assertNull(dao.getComment(cId1, t));
            fail("comment still exists");
        } catch (AskException e) {

        }
        try {
            assertNull(dao.getComment(cId2, t));
            fail("comment still exists");
        } catch (AskException e) {

        }

        // Get question text, comment and delete question
        Question q = dao.getQuestion(questionId, t);
        String qTextId = q.getAnswersIDs().get(0);

        String mainTextComment = "Comment in main text";
        String qTextCommentId = s.newComment(questionId, qTextId, mainTextComment, author, t);
        c = getComment(qTextId, qTextCommentId);
        assertNotNull(c);
        assertEquals(c.getAuthor(), author);
        assertEquals(c.getText(), (mainTextComment));

        // Delete all
        s.deleteQuestion(questionId, t);
        try {
            dao.getComment(qTextCommentId, t);
            fail("comment still exists");
        } catch (AskException e) {

        }
        try {
            dao.getAnswer(qTextCommentId, t);
            fail("answer still exists");
        } catch (AskException e) {

        }
        try {
            dao.getQuestion(questionId, t);
            fail("question still exists");
        } catch (AskException e) {

        }

    }


    @SuppressWarnings("unchecked")
    public Comment getComment(String answerId, String commentId) throws AskException {
        Map<String, Object> a = s.getQuestionData(questionId, t);
        List<Answer> ansList = (List<Answer>) a.get("answers");
        ansList.add((Answer) a.get("question"));
        for (Answer ans : ansList) {
            if (ans.getId().equals(answerId)) {
                if (!ans.getCommentsIds().contains(commentId))
                    return null;
                for (Comment c : ans.getComments()) {
                    if (c.getId().equals(commentId))
                        return c;
                }
                return null;
            }
        }
        return null;
    }

}
