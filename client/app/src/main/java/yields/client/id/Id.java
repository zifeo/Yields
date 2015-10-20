package yields.client.id;

/**
 * Id used in the App.
 */
public class Id implements IdInterface<Long> {

    private Long mId;

    public Id(long value){
        mId = value;
    }

    @Override
    public Long getId() {
        return mId;
    }

    @Override
    public boolean equals(IdInterface<Long> other) {
        return mId.equals(other.getId());
    }
}
