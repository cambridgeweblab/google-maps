package ucles.weblab.common.googlemaps;

/**
 * Thrown when the server encounters an error connecting to the google maps services.
 */
public class GoogleConnectionException extends Exception {
    public GoogleConnectionException(Throwable cause) {
        super(cause);
    }
}
