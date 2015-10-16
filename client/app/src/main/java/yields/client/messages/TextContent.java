package yields.client.messages;

public class TextContent implements Content {

    private String mText;

    public TextContent(String text){
        //TODO Check for safe content
        mText = text;
    }

    public String getText(){return mText;};

    @Override
    public String getType() {
        return "text";
    }
}