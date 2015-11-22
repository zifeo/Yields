package yields.client.id;

import java.util.Objects;

/**
 * Id used in the App.
 */
public class Id implements IdInterface<Long> {

    private Long mId;

    /**
     * Constructor for the Id taking a long in parameter.
     * @param value The id in Long.
     */
    public Id(long value){
        mId = value;
    }

    /**
     * Return the value of the id.
     * @return
     */
    @Override
    public Long getId() {
        return mId;
    }

    /**
     * Perform equality test between ids.
     * @param other The id we wnat to compare.
     * @return True if they are the same, false otherwise.
     */
    @Override
    public boolean equals(IdInterface<Long> other) {
        return Objects.equals(mId, other.getId());
    }
}
