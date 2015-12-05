package yields.client.servicerequest;

import yields.client.id.Id;
import yields.client.node.User;
import yields.client.serverconnection.RequestBuilder;
import yields.client.serverconnection.ServerRequest;

public class UserSearchRequest extends ServiceRequest {

    private final Id mSender;
    private final String mEmail;

    public UserSearchRequest(Id sender, String email) {
        mSender = sender;
        mEmail = email;
    }

    @Override
    public RequestKind getType() {
        return RequestKind.USER_SEARCH;
    }

    @Override
    public ServerRequest parseRequestForServer() {
        return RequestBuilder.userSearchRequest(mSender, mEmail);
    }
}
