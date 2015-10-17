package yields.client.serverconnection;

public interface CommunicationChannel {

    ProtocolMessage sendRequest(ProtocolMessage request);
}
