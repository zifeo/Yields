package yields.client.serverconnection;

public class RequestBuilderException extends Exception {

    public RequestBuilderException(){
        super();
    }

    public RequestBuilderException(Exception e){
        super(e);
    }

    public RequestBuilderException(String message){
        super(message);
    }
}
