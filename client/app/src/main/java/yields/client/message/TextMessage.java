package yields.client.message;

public class TextMessage implements Message {
    String message;

    public TextMessage(String text) {
        this.message = text;
    }

    public String toSring(){
        return message;
    }
}
