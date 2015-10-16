package yields.client.messages;

import java.util.ArrayList;

public interface MessageViewGenerator {

    /**
     * Returns a {@code MessageView} that corresponds to the {@code Message} passed
     * as parameter.
     * @param message The {@code Message} from which the {@code MessageView} is
     *                generated.
     * @return The {@code MessageView} that was generated from the message.
     */
    MessageView generateMessageView(Message message);
}
