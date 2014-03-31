package pt.inesc.ask.domain;

public class AskException extends
        Exception {


    public AskException(String error) {
        super(error);
    }

    @Override
    public String toString() {
        return "AskException [" + getMessage() + "]";
    }



}
