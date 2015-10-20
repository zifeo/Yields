package yields.client.serverconnection;

public class ChannelSendException extends Exception {
    public ChannelSendException(Exception e) {
        super(e);
    }

    public ChannelSendException(String message) {
        super(message);
    }
}
