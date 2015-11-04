package yields.client.id;

import java.util.Objects;

/**
 * Id used in the App.
 */
public class Id implements IdInterface<String> {

    private String mId;

    public Id(long value){
        mId = Long.toString(value);
    }

    public Id(String value){
        mId = value;
    }

    @Override
    public String getId() {
        return mId;
    }

    @Override
    public boolean equals(IdInterface<String> other) {
        return Objects.equals(mId, other);
    }
}
