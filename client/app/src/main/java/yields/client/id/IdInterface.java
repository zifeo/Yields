package yields.client.id;

/**
 * Interface for the Id.
 * @param <T> Type of the Id.
 */
public interface IdInterface<T> {
    T getId();
    boolean equals(IdInterface<T> other);
}
