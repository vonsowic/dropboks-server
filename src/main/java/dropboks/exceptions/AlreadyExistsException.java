package dropboks.exceptions;

/**
 * Created by miwas on 19.01.17.
 */
public class AlreadyExistsException extends RuntimeException {
    public AlreadyExistsException(String message) {
        super(message);
    }
}
