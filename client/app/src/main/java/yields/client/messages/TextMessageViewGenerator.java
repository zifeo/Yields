package yields.client.messages;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.TextView;

import yields.client.node.User;
import yields.client.yieldsapplication.YieldsApplication;

public class TextMessageViewGenerator implements MessageViewGenerator {

    public TextMessageViewGenerator(){};

    @Override
    public  MessageView generateMessageView(Message message) throws MessageViewGenerationException{
        MessageView messageView;
        messageView = new TextMessageView(YieldsApplication.getApplicationContext(), message);
        return messageView;
    }

    private class TextMessageView extends MessageView {

        private User mSender;
        private String mContent;

        public TextMessageView(Context context, Message message) throws MessageViewGenerationException{
            super(context);
            if(message == null || message.getContent().getType() != "text"){
                throw new MessageViewGenerationException();
            }
            mSender = message.getSender();
            mContent = ((TextContent) message.getContent()).getText();
        }
    }
}
