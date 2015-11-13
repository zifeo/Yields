package yields.client.serverconnection;

import org.json.JSONException;

import java.io.IOException;

public interface ConnectionSubscriber {

    void updateOn(Response response);

    void updateOnConnectionProblem(IOException exception);

    void updateOnParsingProblem(JSONException exception);
}
