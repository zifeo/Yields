package yields.client.messages;

import java.io.IOException;
import java.util.Date;

import yields.client.node.Node;
import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

/**
 * Message is a {@code Node} which is shared as a message in a conversation.
 */
public class Message extends Node{

    private final User mSender;
    private final Content mContent;
    private final Date mDate;

    public Message(String nodeName, long nodeID, User sender, Content content, Date date){
        super(nodeName, nodeID);
        this.mSender = sender;
        this.mContent = content;
        this.mDate = new Date(date.getTime());
    }

    public User getSender(){
        return mSender;
    }

    public Content getContent() {
        return mContent;
    }

    public Date getDate(){
        return new Date(mDate.getTime());
    }

    public MessageView getMessageView(boolean showUsername) {
        try {
            return new MessageView(YieldsApplication.getApplicationContext(), this, showUsername);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}