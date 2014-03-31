package pt.inesc.ask.domain;

public class AskException extends
        Exception {

    String error;

    public AskException(String error) {
        super();
        this.error = error;
    }


}
