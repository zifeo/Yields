package yields.client.activities;

/**
 * Small interface that requires to implement a
 * notifyChange() method, which tells the activity
 * that the data set it holds has changed
 */
public interface NotifiableActivity {

    /**
     * Method that tells the activity
     * that the data set it holds has changed
     */
    void notifyChange();
}
