package yields.client.id;

import java.util.Objects;

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
        return Objects.equals(mId, other);
    }
}
