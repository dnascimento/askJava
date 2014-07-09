package pt.inesc.ask.dao;

import java.util.LinkedList;
import java.util.List;

import  org.jboss.logging.Logger;

import pt.inesc.ask.domain.Answer;
import pt.inesc.ask.domain.AskException;
import pt.inesc.ask.domain.Comment;
import pt.inesc.ask.domain.Question;
import pt.inesc.ask.domain.QuestionEntry;
import pt.inesc.ask.proto.AskProto;
import voldemort.undoTracker.RUD;
import voldemort.versioning.Version;
import voldemort.versioning.Versioned;

// Data access object: access to database and convertion
public class VoldemortDAO
        implements DAO {
    private static final Logger log = Logger.getLogger(VoldemortDAO.class.getName());

    VoldemortStore<String, AskProto.Question> questions;
    VoldemortStore<String, AskProto.Answer> answers;
    VoldemortStore<String, AskProto.Comment> comments;
    VoldemortStore<String, AskProto.Index> index;
    String TAG_LIST = "TAG_LIST";


    public VoldemortDAO(String bootstrapUrl) {
        questions = new VoldemortStore<String, AskProto.Question>("questionStore", bootstrapUrl);
        answers = new VoldemortStore<String, AskProto.Answer>("answerStore", bootstrapUrl);
        comments = new VoldemortStore<String, AskProto.Comment>("commentStore", bootstrapUrl);
        index = new VoldemortStore<String, AskProto.Index>("index", bootstrapUrl);
    }

    // Save
    @Override
    public Version saveNew(Question quest, RUD rud) throws AskException {
        for (String tag : quest.getTags()) {
            Versioned<AskProto.Index> versioned = index.get(tag, rud);
            List<String> list;
            if (versioned == null) {
                list = new LinkedList<String>();
                newTag(tag);
            } else {
                AskProto.Index entry = versioned.getValue();
                list = entry.getEntryList();
            }
            if (!list.contains(quest.getId())) {
                index.put(tag, AskProto.Index.newBuilder().addAllEntry(list).addEntry(quest.getId()).build(), rud);
            }
        }
        return save(quest, rud);
    }


    /**
     * Add a new tag
     * 
     * @param tag
     */
    private void newTag(String tag) {
        // ignore depenencies
        RUD rud = new RUD();
        Versioned<AskProto.Index> versioned = index.get(TAG_LIST, rud);
        List<String> list;
        if (versioned == null) {
            list = new LinkedList<String>();
        } else {
            AskProto.Index entry = versioned.getValue();
            list = entry.getEntryList();
            if (list.contains(tag)) {
                return;
            }
        }
        index.put(TAG_LIST, AskProto.Index.newBuilder().addAllEntry(list).addEntry(tag).build(), rud);
    }

    @Override
    public Version save(Question quest, RUD rud) {
        return questions.put(quest.getId(), cast(quest), rud);
    }

    @Override
    public Version save(Answer answer, RUD rud) {
        return answers.put(answer.getId(), cast(answer), rud);
    }

    @Override
    public Version save(Comment comment, RUD rud) {
        return comments.put(comment.getId(), cast(comment), rud);
    }

    // Delete
    @Override
    public boolean deleteQuestion(String questionId, RUD rud) {
        Question question;
        try {
            question = getQuestion(questionId, rud);
        } catch (AskException e1) {
            log.error("Delete: Question not exists");
            return false;
        }
        boolean found = false;
        for (String tag : question.getTags()) {
            Versioned<AskProto.Index> indexList = index.get(tag, rud);
            AskProto.Index indexMsg = indexList.getValue();
            AskProto.Index.Builder b = AskProto.Index.newBuilder();
            for (String e : indexMsg.getEntryList()) {
                if (!e.equals(questionId)) {
                    b.addEntry(e);
                } else {
                    found = true;
                }
            }
            if (!found) {
                log.error("Question not found in index");
            }
            index.put(tag, b.build(), rud);
        }
        boolean q = questions.delete(questionId, rud);
        if (!q) {
            log.error("Question not exists:" + questionId);
        }
        return q && found;
    }

    @Override
    public boolean deleteAnswer(String answerId, RUD rud) {
        boolean s = answers.delete(answerId, rud);
        if (!s)
            log.error("delete answer: not found " + answerId);
        return s;
    }

    @Override
    public boolean deleteComment(String commentId, RUD rud) {
        boolean s = comments.delete(commentId, rud);
        if (!s)
            log.error("delete comment: not found " + commentId);
        return s;
    }

    // Gets
    @Override
    public Question getQuestion(String questionId, RUD rud) throws AskException {
        Versioned<AskProto.Question> q = questions.get(questionId, rud);
        if (q == null) {
            throw new AskException("Question not exists: " + questionId);
        }
        return cast(questionId, q.getValue());
    }

    @Override
    public Answer getAnswer(String answerId, RUD rud) throws AskException {
        Versioned<AskProto.Answer> a = answers.get(answerId, rud);
        if (a == null) {
            throw new AskException("Answer not exists: " + answerId);
        }
        return cast(answerId, a.getValue());
    }

    @Override
    public Comment getComment(String commentId, RUD rud) throws AskException {
        Versioned<AskProto.Comment> c = comments.get(commentId, rud);
        if (c == null) {
            throw new AskException("Comment not exists: " + commentId);
        }
        return cast(commentId, c.getValue());
    }


    @Override
    public List<QuestionEntry> getListQuestions(RUD rud, String tag) throws AskException {
        Versioned<AskProto.Index> indexList = index.get(tag, rud);
        if (indexList == null) {
            return new LinkedList<QuestionEntry>();
        }
        AskProto.Index indexMsg = indexList.getValue();
        List<String> list = indexMsg.getEntryList();
        LinkedList<QuestionEntry> result = new LinkedList<QuestionEntry>();
        for (String str : list) {
            result.add(new QuestionEntry(str));
        }
        return result;
    }

    @Override
    public void cleanIndex(RUD rud) {
        index.put("index", AskProto.Index.newBuilder().build(), rud);
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
        Versioned<AskProto.Index> versioned = index.get(TAG_LIST, new RUD());
        List<String> list;
        if (versioned == null) {
            list = new LinkedList<String>();
        } else {
            AskProto.Index entry = versioned.getValue();
            list = entry.getEntryList();
        }
        return list;
    }
}
