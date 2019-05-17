/* Made by Filip Adamik on 17/05/2019 */

package database;

/**
 * Database exception that is thrown when a record is not found in the database.
 */
public class DatabaseException extends Exception {
    public DatabaseException(String message) {
        super(message);
    }
}
