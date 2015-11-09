package yields.client.id;

import java.util.Objects;

/**
 * Id used in the App.
 */
public class Id implements IdInterface<String> {

    private String mId;

    /**
     * Constructor for the Id taking a long in parameter.
     * @param value The id in Long.
     */
    public Id(long value){
        mId = Long.toString(value);
    }

    /**
     * Constructor for the id taking a string in parameter.
     * @param value The id in a string.
     */
    public Id(String value){
        mId = value;
    }

    /**
     * Return the value of the id.
     * @return
     */
    @Override
    public String getId() {
        return mId;
    }

    /**
     * Perform equality test between ids.
     * @param other The id we wnat to compare.
     * @return True if they are the same, false otherwise.
     */
    @Override
    public boolean equals(IdInterface<String> other) {
        return Objects.equals(mId, other);
    }
}
