package pt.inesc.ask.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.Test;

import pt.inesc.ask.dao.VoldemortStore;
import pt.inesc.ask.domain.AskException;
import pt.inesc.ask.domain.Question;
import pt.inesc.ask.proto.AskProto;
import voldemort.undoTracker.RUD;
import voldemort.versioning.Versioned;

public class SimpleClientTest {
    VoldemortStore<String, AskProto.Question> questions;
    LinkedList<String> tags = new LinkedList<String>(Arrays.asList("novo"));
    String questionTitle = "key1";
    RUD rud = new RUD(69L);

    public SimpleClientTest() {
        String bootstrapUrl = "tcp://localhost:6666";
        questions = new VoldemortStore<String, AskProto.Question>("questionStore", bootstrapUrl);
    }

    @Test
    public void putGet() throws AskException {
        String text1 = "dario";
        Question q = new Question(questionTitle, tags, "1", "1", text1);
        questions.put(questionTitle, q.getData(), rud);
        System.out.println("put done");
        questions.put(questionTitle, q.getData(), rud);
        System.out.println("put done");
        Versioned<AskProto.Question> q2 = questions.get(questionTitle, rud);
        assertEquals(questionTitle, q2.getValue().getTitle());
        Versioned<AskProto.Question> q3 = questions.get(questionTitle, rud);
        assertEquals(questionTitle, q3.getValue().getTitle());
        questions.delete(questionTitle, rud);
    }
}
