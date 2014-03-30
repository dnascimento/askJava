package pt.inesc.ask.dao;

import pt.inesc.ask.domain.Question;
import voldemort.client.ClientConfig;
import voldemort.client.SocketStoreClientFactory;
import voldemort.client.StoreClient;
import voldemort.client.StoreClientFactory;
import voldemort.versioning.Versioned;

// Data access object: access to database and convertion
public class QuestionDAO {

    public QuestionDAO() {
        // In real life this stuff would get wired in
        String bootstrapUrl = "tcp://localhost:6666";
        StoreClientFactory factory = new SocketStoreClientFactory(
                new ClientConfig().setBootstrapUrls(bootstrapUrl));

        StoreClient<String, Question> client = factory.getStoreClient("my_store_name");

        // get the value
        Versioned<Question> version = client.get("some_key");

        // modify the value
        // version.setObject(new Question());

        // update the value
        client.put("some_key", version);
    }
}
