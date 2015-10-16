package yields.client.messages;

public class TextContent implements Content {

    private String mText;

    public TextContent(String text){
        //TODO Check for safe content
        mText = text;
    }

    @Override
    public String getType() {
        return "text";
    }
}