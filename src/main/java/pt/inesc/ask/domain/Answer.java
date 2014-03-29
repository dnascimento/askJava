package pt.inesc.ask.domain;

import java.util.List;

public class Answer {
    private String id;
    private String author;
    private String text;
    private Boolean isQuestion;
    private int votes;
    private List<String> commentsIds;

    /**
     * m = hashlib.md5()
     * m.update(text+author+answerID)
     * self._id = "com_"+m.hexdigest()
     */
}
