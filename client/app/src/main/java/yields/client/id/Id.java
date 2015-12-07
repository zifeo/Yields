package yields.client.id;

/**
 * Id used in the App.
 */
public class Id {

    private Long mId;

    /**
     * Constructor for the Id taking a long in parameter.
     *
     * @param value The id in Long.
     */
    public Id(long value) {
        mId = value;
    }

    /**
     * Return the value of the id.
     *
     * @return The Long value of the id.
     */
    public Long getId() {
        return mId;
    }

    /**
     * Performs equality test between ids.
     *
     * @param other The id we want to compare.
     * @return True if they are the same, false otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Id)) {
            return false;
        } else {
            return ((Id) other).getId().equals(this.getId());
        }
    }
}
