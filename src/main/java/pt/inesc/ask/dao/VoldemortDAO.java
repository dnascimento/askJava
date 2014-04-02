package pt.inesc.ask.dao;

import java.util.LinkedList;
import java.util.List;

import pt.inesc.ask.domain.Answer;
import pt.inesc.ask.domain.AskException;
import pt.inesc.ask.domain.Comment;
import pt.inesc.ask.domain.Question;
import pt.inesc.ask.proto.AskProto;
import voldemort.versioning.Versioned;

// Data access object: access to database and convertion
public class VoldemortDAO
        implements DAO {
    VoldemortStore<AskProto.Question> questions;
    VoldemortStore<AskProto.Answer> answers;
    VoldemortStore<AskProto.Comment> comments;
    VoldemortStore<AskProto.Index> index;

    public VoldemortDAO() {
        String bootstrapUrl = "tcp://localhost:6666";

        questions = new VoldemortStore<AskProto.Question>("questionStore", bootstrapUrl);
        answers = new VoldemortStore<AskProto.Answer>("answerStore", bootstrapUrl);
        comments = new VoldemortStore<AskProto.Comment>("commentStore", bootstrapUrl);
        index = new VoldemortStore<AskProto.Index>("index", bootstrapUrl);
    }

    // Save
    @Override
    public void saveNew(Question quest, long rid) {
        Versioned<AskProto.Index> versioned = index.get("index", rid);
        List<String> list;
        if (versioned == null) {
            list = new LinkedList<String>();
        } else {
            AskProto.Index entry = versioned.getValue();
            list = entry.getEntryList();
            if (list.contains(quest.id))
                return;
        }
        index.put("index", AskProto.Index.newBuilder().addAllEntry(list).addEntry(quest.id).build(), rid);
        save(quest, rid);
    }

    @Override
    public void save(Question quest, long rid) {
        questions.put(quest.id, cast(quest), rid);
    }

    @Override
    public void save(Answer answer, long rid) {
        answers.put(answer.id, cast(answer), rid);
    }

    @Override
    public void save(Comment comment, long rid) {
        comments.put(comment.id, cast(comment), rid);
    }

    // Delete
    @Override
    public void deleteQuestion(String questionId, long rid) throws AskException {
        Versioned<AskProto.Index> indexList = index.get("index", rid);
        AskProto.Index indexMsg = indexList.getValue();
        List<String> list;
        list = indexMsg.getEntryList();
        list.remove(questionId);
        index.put("index", AskProto.Index.newBuilder().addAllEntry(list).build(), rid);
        boolean q = questions.delete(questionId, rid);
        if (q == false) {
            throw new AskException("Question not exists:" + questionId);
        }
    }

    @Override
    public void deleteAnswer(String answerId, long rid) throws AskException {
        boolean a = answers.delete(answerId, rid);
        if (a == false) {
            throw new AskException("Answer not exists:" + answerId);
        }
    }

    @Override
    public void deleteComment(String commentId, long rid) throws AskException {
        boolean c = comments.delete(commentId, rid);
        if (c == false) {
            throw new AskException("Comment not exists:" + commentId);
        }
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
    public List<Question> getListQuestions(long rid) throws AskException {
        Versioned<AskProto.Index> indexList = index.get("index", rid);
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

    // Question
    private AskProto.Question cast(Question q) {
        return AskProto.Question.newBuilder()
                                .setTitle(q.title)
                                .addAllAnswerIds(q.answersIDs)
                                .addAllTags(q.tags)
                                .build();
    }

    private Question cast(String id, AskProto.Question q) {
        LinkedList<String> ids = new LinkedList<String>(q.getAnswerIdsList());
        return new Question(q.getTitle(), q.getTagsList(), ids);
    }

    // Answer
    private AskProto.Answer cast(Answer q) {
        return AskProto.Answer.newBuilder()
                              .setAuthor(q.author)
                              .setText(q.text)
                              .setVotes(q.votes)
                              .setIsQuestion(q.isQuestion)
                              .addAllCommentIds(q.commentsIds)
                              .build();
    }

    private Answer cast(String id, AskProto.Answer q) {
        LinkedList<String> ids = new LinkedList<String>(q.getCommentIdsList());
        return new Answer(id, q.getAuthor(), q.getText(), q.getIsQuestion(), q.getVotes(), ids);
    }

    // Comment
    private AskProto.Comment cast(Comment q) {
        return AskProto.Comment.newBuilder().setAuthor(q.getAuthor()).setText(q.text).build();
    }

    private Comment cast(String id, AskProto.Comment q) {
        Comment c = new Comment("", q.getText(), q.getAuthor());
        c.id = id;
        return c;
    }


}
