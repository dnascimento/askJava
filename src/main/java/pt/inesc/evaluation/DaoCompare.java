package pt.inesc.evaluation;

import java.util.ArrayList;
import java.util.List;

import pt.inesc.ask.dao.VoldemortDAO;
import pt.inesc.ask.domain.Answer;
import pt.inesc.ask.domain.AskException;
import pt.inesc.ask.domain.Question;
import pt.inesc.ask.domain.QuestionEntry;
import voldemort.undoTracker.SRD;

public class DaoCompare {

    public static String DATABASE_SERVER = "localhost";

    private static final SRD srdOldServer = new SRD(System.currentTimeMillis(), 1, false);
    private static final SRD srdNewServer = new SRD(System.currentTimeMillis(), 0, false);

    public static void main(String[] args) throws AskException {
        compare(6666, 7666);
    }



    public static void compare(Integer portOld, Integer portNew) throws AskException {
        String oldServer = "tcp://" + DATABASE_SERVER + ":" + portOld;
        String newServer = "tcp://" + DATABASE_SERVER + ":" + portNew;

        // old exec
        VoldemortDAO d1 = new VoldemortDAO(oldServer);
        // new exec
        VoldemortDAO d2 = new VoldemortDAO(newServer);

        // Compare tags
        List<String> d1Tags = new ArrayList<String>(d1.getTags());
        List<String> d2Tags = new ArrayList<String>(d2.getTags());
        List<String> tagsComp = new ListCompare<String>(d1Tags, d2Tags, "tags").compare();

        for (String tag : tagsComp) {
            List<QuestionEntry> questionList1 = d1.getListQuestions(srdOldServer, tag);
            List<QuestionEntry> questionList2 = d2.getListQuestions(srdNewServer, tag);

            List<QuestionEntry> questionComp = new ListCompare<QuestionEntry>(questionList1, questionList2, "questions").compare();


            for (QuestionEntry questionEntry : questionComp) {
                Question q1 = d1.getQuestion(questionEntry.id, srdOldServer);
                Question q2 = d2.getQuestion(questionEntry.id, srdNewServer);
                List<String> p1 = new ArrayList<String>(q1.getAnswersIDs());
                List<String> p2 = new ArrayList<String>(q2.getAnswersIDs());

                List<String> commonAnswers = new ListCompare<String>(p1, p2, "answers").compare();

                for (String answerId : commonAnswers) {
                    Answer a1 = d1.getAnswer(answerId, srdOldServer);
                    Answer a2 = d2.getAnswer(answerId, srdNewServer);

                    if (!a1.getText().equals(a2.getText())) {
                        System.out.println("Different answer with same id");
                    }
                    List<String> c1 = new ArrayList<String>(a1.getCommentsIds());
                    List<String> c2 = new ArrayList<String>(a2.getCommentsIds());

                    List<String> commonComments = new ListCompare<String>(c1, c2, "comments").compare();
                }
            }
        }
        System.out.println("Done");
    }
}
