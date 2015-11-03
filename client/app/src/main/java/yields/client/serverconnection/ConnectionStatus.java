package yields.client.serverconnection;

/**
 * Gives the status of a connection.
 */
public interface ConnectionStatus {
    /**
     * @return The state of the connection
     */
    boolean working();
}
