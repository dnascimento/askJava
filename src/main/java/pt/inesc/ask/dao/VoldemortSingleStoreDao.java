package pt.inesc.ask.dao;

import java.util.LinkedList;
import java.util.List;

import org.jboss.logging.Logger;

import pt.inesc.ask.domain.Answer;
import pt.inesc.ask.domain.AskException;
import pt.inesc.ask.domain.Comment;
import pt.inesc.ask.domain.Question;
import pt.inesc.ask.domain.QuestionEntry;
import pt.inesc.ask.proto.AskProto;
import voldemort.undoTracker.SRD;
import voldemort.versioning.Version;
import voldemort.versioning.Versioned;

import com.google.protobuf.Message;

// Data access object: access to database and convertion
public class VoldemortSingleStoreDao
        implements DAO {
    private static final Logger LOG = Logger.getLogger(VoldemortSingleStoreDao.class.getName());

    VoldemortStore<String, Message> store;
    SRD nullRud = new SRD();


    public VoldemortSingleStoreDao(String bootstrapUrl) {
        System.out.println("NEW VOLDEMORT DATABASE ACCESS");
        store = new VoldemortStore<String, Message>("questionStore", bootstrapUrl);
    }

    /*
     * Add a new question to the tag (the dependency is ignored)
     */
    @Override
    public Version saveNew(Question quest, SRD srd) {
        return save(quest, srd);
    }


    @Override
    public Version save(Question quest, SRD srd) {
        return store.put(quest.getId(), cast(quest), srd);
    }

    @Override
    public Version save(Answer answer, SRD srd) {
        // TODO error se a answer já existe
        return store.put(answer.getId(), cast(answer), srd);
    }

    @Override
    public Version save(Comment comment, SRD srd) {
        return store.put(comment.getId(), cast(comment), srd);
    }

    // Delete
    @Override
    public boolean deleteQuestion(String questionId, SRD srd) {
        Question question;
        try {
            question = getQuestion(questionId, srd);
        } catch (AskException e1) {
            // LOG.error("Delete: Question not exists");
            return false;
        }
        boolean found = false;
        boolean q = store.delete(questionId, srd);
        if (!q) {
            // LOG.error("Question not exists:" + questionId);
        }
        return q && found;
    }

    @Override
    public boolean deleteAnswer(String answerId, SRD srd) throws AskException {
        boolean s = store.delete(answerId, srd);
        if (!s) {
            throw new AskException("delete answer: not found " + answerId);
        }
        return s;
    }

    @Override
    public boolean deleteComment(String commentId, SRD srd) throws AskException {
        boolean s = store.delete(commentId, srd);
        if (!s) {
            throw new AskException("delete comment: not found " + commentId);
        }
        return s;
    }

    // Gets
    @Override
    public Question getQuestion(String questionId, SRD srd) throws AskException {
        Versioned<Message> q = store.get(questionId, srd);
        if (q == null) {
            throw new AskException("Question not exists: " + questionId);
        }
        return cast(questionId, (AskProto.Question) q.getValue());
    }

    @Override
    public Answer getAnswer(String answerId, SRD srd) throws AskException {
        Versioned<Message> a = store.get(answerId, srd);
        if (a == null) {
            throw new AskException("Answer not exists: " + answerId);
        }
        return cast(answerId, (AskProto.Answer) a.getValue());
    }

    @Override
    public Comment getComment(String commentId, SRD srd) throws AskException {
        Versioned<Message> c = store.get(commentId, srd);
        if (c == null) {
            throw new AskException("Comment not exists: " + commentId);
        }
        return cast(commentId, (AskProto.Comment) c.getValue());
    }


    @Override
    public List<QuestionEntry> getListQuestions(SRD srd, String tag) throws AskException {
        List<QuestionEntry> result = new LinkedList<QuestionEntry>();
        return result;
    }

    @Override
    public void cleanIndex(SRD srd) {
    }

    // Question
    private AskProto.Question cast(Question q) {
        return q.getData();
    }

    private Question cast(String id, AskProto.Question q) {
        return new Question(id, q);
    }

    // Answer
    private AskProto.Answer cast(Answer q) {
        return q.getData();
    }

    private Answer cast(String id, AskProto.Answer q) {
        return new Answer(id, q);
    }

    // Comment
    private AskProto.Comment cast(Comment q) {
        return q.getData();
    }

    private Comment cast(String id, AskProto.Comment q) {
        return new Comment(id, q);
    }



    @Override
    public List<String> getTags() {
        List<String> list;
        list = new LinkedList<String>();
        return list;
    }
}
