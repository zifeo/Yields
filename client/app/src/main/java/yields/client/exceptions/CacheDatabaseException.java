package yields.client.exceptions;

public class CacheDatabaseException extends Exception {

    public CacheDatabaseException(){
        super();
    }

    public CacheDatabaseException(String message){
        super(message);
    }

    public CacheDatabaseException(Throwable th){
        super(th);
    }
}
