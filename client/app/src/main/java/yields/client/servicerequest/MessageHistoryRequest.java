package yields.client.servicerequest;

import android.app.Service;

import java.util.Date;

import yields.client.id.Id;
import yields.client.serverconnection.Request;
import yields.client.serverconnection.Response;

public class MessageHistoryRequest extends ServiceRequest{

    Id mGroupId;
    Date mFurthestDate;


    public MessageHistoryRequest(Id groupId, Date furthestDate){
        super();
        mFurthestDate = new Date(furthestDate.getTime());
        mGroupId = groupId;
    }

    @Override
    public String getType(){
        return "GetHistory";
    }

    @Override
    public Request parseRequestForServer(){
        //TODO request build
        return null;
    }

    @Override
    public void serviceActionOnResponse(Service service, Response response) {

    }

    public Date getDate(){
        return new Date(mFurthestDate.getTime());
    }

    public Id getId(){
        return mGroupId;
    }
}
