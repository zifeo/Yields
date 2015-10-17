package yields.client.serverconnection;

public abstract class CommunicationChannel {

    public abstract ProtocolMessage sendRequest(ProtocolMessage request);
}
