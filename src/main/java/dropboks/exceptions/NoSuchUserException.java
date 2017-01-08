package dropboks.exceptions;

/**
 * Created by miwas on 08.01.17.
 */
public class NoSuchUserException extends RuntimeException {

    public NoSuchUserException(NumberFormatException ex) {
        super(ex);
    }

    public NoSuchUserException(Exception ex) {
        super(ex);
    }


}
