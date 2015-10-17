package yields.client.messages;

import android.content.Context;

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

        private Message mMessage;

        public TextMessageView(Context context, Message message) throws MessageViewGenerationException{
            super(context);
            if(mMessage == null || message.getContent().getType() != "text"){
                throw new MessageViewGenerationException();
            }
        }
    }
}
