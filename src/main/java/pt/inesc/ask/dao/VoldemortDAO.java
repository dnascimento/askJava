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

// Data access object: access to database and convertion
public class VoldemortDAO
        implements DAO {
    private static final Logger LOG = Logger.getLogger(VoldemortDAO.class.getName());

    VoldemortStore<String, AskProto.Question> questions;
    VoldemortStore<String, AskProto.Answer> answers;
    VoldemortStore<String, AskProto.Comment> comments;
    // VoldemortStore<String, AskProto.Index> index;
    private static final String TAG_LIST = "TAG_LIST";
    SRD nullRud = new SRD();


    public VoldemortDAO(String bootstrapUrl) {
        System.out.println("NEW VOLDEMORT DATABASE ACCESS");
        questions = new VoldemortStore<String, AskProto.Question>("questionStore", bootstrapUrl);
        answers = new VoldemortStore<String, AskProto.Answer>("answerStore", bootstrapUrl);
        comments = new VoldemortStore<String, AskProto.Comment>("commentStore", bootstrapUrl);
        // index = new VoldemortStore<String, AskProto.Index>("index", bootstrapUrl);
    }

    /*
     * Add a new question to the tag (the dependency is ignored)
     */
    @Override
    public Version saveNew(Question quest, SRD srd) {
        for (String tag : quest.getTags()) {
            // Versioned<AskProto.Index> versioned = index.get(tag, nullRud);
            // List<String> list;
            // if (versioned == null) {
            // list = new LinkedList<String>();
            // add tag to index of tags
            // newTag(tag);
            // } else {
            // AskProto.Index entry = versioned.getValue();
            // list = entry.getEntryList();
            // }
            // LOG.info("Get Tag entries: " + list);
            // if (!list.contains(quest.getId())) {
            // // LOG.info("Question did not exist in tag, add it and put");
            // try {
            // index.put(tag,
            // AskProto.Index.newBuilder().addAllEntry(list).addEntry(quest.getId()).build(),
            // nullRud);
            // } catch (Exception e) {
            // System.out.println("Index store: " + e.getMessage());
            // }
            // }
        }
        return save(quest, srd);
    }

    /**
     * Add a new tag to the index of tags, ignored by redo
     * 
     * @param tag
     */
    private void newTag(String tag) {
        // ignore depenencies
        // TODO problem: ObselentException
        // Versioned<AskProto.Index> versioned = index.get(TAG_LIST, nullRud);
        // List<String> list;
        // if (versioned == null) {
        // list = new LinkedList<String>();
        // } else {
        // AskProto.Index entry = versioned.getValue();
        // list = entry.getEntryList();
        // if (list.contains(tag)) {
        // return;
        // }
        // }
        //
        // versioned.setObject(AskProto.Index.newBuilder().addAllEntry(list).addEntry(tag).build());
        //
        // index.put(TAG_LIST, versioned, nullRud);
    }

    @Override
    public Version save(Question quest, SRD srd) {
        return questions.put(quest.getId(), cast(quest), srd);
    }

    @Override
    public Version save(Answer answer, SRD srd) {
        // TODO error se a answer já existe
        return answers.put(answer.getId(), cast(answer), srd);
    }

    @Override
    public Version save(Comment comment, SRD srd) {
        return comments.put(comment.getId(), cast(comment), srd);
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
        // for (String tag : question.getTags()) {
        // Versioned<AskProto.Index> indexList = index.get(tag, nullRud);
        // AskProto.Index indexMsg = indexList.getValue();
        // AskProto.Index.Builder b = AskProto.Index.newBuilder();
        // for (String e : indexMsg.getEntryList()) {
        // if (!e.equals(questionId)) {
        // b.addEntry(e);
        // } else {
        // found = true;
        // }
        // }
        // if (!found) {
        // // LOG.error("Question not found in index");
        // }
        // index.put(tag, b.build(), nullRud);
        // }
        boolean q = questions.delete(questionId, srd);
        if (!q) {
            // LOG.error("Question not exists:" + questionId);
        }
        return q && found;
    }

    @Override
    public boolean deleteAnswer(String answerId, SRD srd) throws AskException {
        boolean s = answers.delete(answerId, srd);
        if (!s) {
            throw new AskException("delete answer: not found " + answerId);
        }
        return s;
    }

    @Override
    public boolean deleteComment(String commentId, SRD srd) throws AskException {
        boolean s = comments.delete(commentId, srd);
        if (!s) {
            throw new AskException("delete comment: not found " + commentId);
        }
        return s;
    }

    // Gets
    @Override
    public Question getQuestion(String questionId, SRD srd) throws AskException {
        Versioned<AskProto.Question> q = questions.get(questionId, srd);
        if (q == null) {
            throw new AskException("Question not exists: " + questionId);
        }
        return cast(questionId, q.getValue());
    }

    @Override
    public Answer getAnswer(String answerId, SRD srd) throws AskException {
        Versioned<AskProto.Answer> a = answers.get(answerId, srd);
        if (a == null) {
            throw new AskException("Answer not exists: " + answerId);
        }
        return cast(answerId, a.getValue());
    }

    @Override
    public Comment getComment(String commentId, SRD srd) throws AskException {
        Versioned<AskProto.Comment> c = comments.get(commentId, srd);
        if (c == null) {
            throw new AskException("Comment not exists: " + commentId);
        }
        return cast(commentId, c.getValue());
    }


    @Override
    public List<QuestionEntry> getListQuestions(SRD srd, String tag) throws AskException {
        // Versioned<AskProto.Index> indexList = index.get(tag, nullRud);
        // // LOG.info("getListQuestions: " + indexList);
        // if (indexList == null) {
        // return new LinkedList<QuestionEntry>();
        // }
        // AskProto.Index indexMsg = indexList.getValue();
        // List<String> list = indexMsg.getEntryList();
        List<QuestionEntry> result = new LinkedList<QuestionEntry>();
        // for (String str : list) {
        // result.add(new QuestionEntry(str));
        // }
        return result;
    }

    @Override
    public void cleanIndex(SRD srd) {
        // index.put("index", AskProto.Index.newBuilder().build(), nullRud);
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
        // Versioned<AskProto.Index> versioned = index.get(TAG_LIST, nullRud);
        List<String> list;
        // if (versioned == null) {
        list = new LinkedList<String>();
        // } else {
        // AskProto.Index entry = versioned.getValue();
        // list = entry.getEntryList();
        // }
        return list;
    }
}
