package pt.inesc.ask.dao;

import java.util.LinkedList;
import java.util.List;

import pt.inesc.ask.domain.Answer;
import pt.inesc.ask.domain.AskException;
import pt.inesc.ask.domain.Comment;
import pt.inesc.ask.domain.Question;
import pt.inesc.ask.proto.AskProto;
import voldemort.versioning.Version;
import voldemort.versioning.Versioned;

// Data access object: access to database and convertion
public class VoldemortDAO
        implements DAO {
    VoldemortStore<String, AskProto.Question> questions;
    VoldemortStore<String, AskProto.Answer> answers;
    VoldemortStore<String, AskProto.Comment> comments;
    VoldemortStore<String, AskProto.Index> index;

    public VoldemortDAO() {
        String bootstrapUrl = "tcp://localhost:6666";
        questions = new VoldemortStore<String, AskProto.Question>("questionStore", bootstrapUrl);
        answers = new VoldemortStore<String, AskProto.Answer>("answerStore", bootstrapUrl);
        comments = new VoldemortStore<String, AskProto.Comment>("commentStore", bootstrapUrl);
        index = new VoldemortStore<String, AskProto.Index>("index", bootstrapUrl);
    }

    // Save
    @Override
    public Version saveNew(Question quest, long rid) throws AskException {
        for (String tag : quest.getTags()) {
            Versioned<AskProto.Index> versioned = index.get(tag, rid);
            List<String> list;
            if (versioned == null) {
                list = new LinkedList<String>();
            } else {
                AskProto.Index entry = versioned.getValue();
                list = entry.getEntryList();
                if (list.contains(quest.getId())) {
                    throw new AskException("Save new question with known id" + quest.getId());
                }
            }
            index.put(tag, AskProto.Index.newBuilder().addAllEntry(list).addEntry(quest.getId()).build(), rid);
        }
        return save(quest, rid);
    }


    @Override
    public Version save(Question quest, long rid) {
        return questions.put(quest.getId(), cast(quest), rid);
    }

    @Override
    public Version save(Answer answer, long rid) {
        return answers.put(answer.getId(), cast(answer), rid);
    }

    @Override
    public Version save(Comment comment, long rid) {
        return comments.put(comment.getId(), cast(comment), rid);
    }

    // Delete
    @Override
    public boolean deleteQuestion(String questionId, long rid) {
        Question question;
        try {
            question = getQuestion(questionId, rid);
        } catch (AskException e1) {
            System.err.println("Delete: Question not exists");
            return false;
        }
        boolean found = false;
        for (String tag : question.getTags()) {
            Versioned<AskProto.Index> indexList = index.get(tag, rid);
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
                System.err.println("Question not found in index");
            }
            index.put(tag, b.build(), rid);
        }
        boolean q = questions.delete(questionId, rid);
        if (!q) {
            System.err.println("Question not exists:" + questionId);
        }
        return q && found;
    }

    @Override
    public boolean deleteAnswer(String answerId, long rid) {
        boolean s = answers.delete(answerId, rid);
        if (!s)
            System.err.println("delete answer: not found " + answerId);
        return s;
    }

    @Override
    public boolean deleteComment(String commentId, long rid) {
        boolean s = comments.delete(commentId, rid);
        if (!s)
            System.err.println("delete comment: not found " + commentId);
        return s;
    }

    // Gets
    @Override
    public Question getQuestion(String questionId, long rid) throws AskException {
        Versioned<AskProto.Question> q = questions.get(questionId, rid);
        if (q == null) {
            throw new AskException("Question not exists: " + questionId);
        }
        return cast(questionId, q.getValue());
    }

    @Override
    public Answer getAnswer(String answerId, long rid) throws AskException {
        Versioned<AskProto.Answer> a = answers.get(answerId, rid);
        if (a == null) {
            throw new AskException("Answer not exists: " + answerId);
        }
        return cast(answerId, a.getValue());
    }

    @Override
    public Comment getComment(String commentId, long rid) throws AskException {
        Versioned<AskProto.Comment> c = comments.get(commentId, rid);
        if (c == null) {
            throw new AskException("Comment not exists: " + commentId);
        }
        return cast(commentId, c.getValue());
    }


    @Override
    public List<Question> getListQuestions(long rid, String tag) throws AskException {
        Versioned<AskProto.Index> indexList = index.get(tag, rid);
        if (indexList == null) {
            return new LinkedList<Question>();
        }
        AskProto.Index indexMsg = indexList.getValue();
        List<String> indice = indexMsg.getEntryList();
        LinkedList<Question> list = new LinkedList<Question>();
        for (String str : indice) {
            list.add(getQuestion(str, rid));
        }
        return list;
    }

    @Override
    public void cleanIndex(long rid) {
        index.put("index", AskProto.Index.newBuilder().build(), rid);
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



}
