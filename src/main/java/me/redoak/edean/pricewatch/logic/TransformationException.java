package me.redoak.edean.pricewatch.logic;

/**
 * Exception indicating an error while transforming an URL.
 */
public class TransformationException extends RuntimeException {

    /**
     * Creates a {@link TransformationException} with given message.
     *
     * @param msg The message to be used.
     *
     * @see RuntimeException#RuntimeException(String)
     */
    public TransformationException(String msg) {
        super(msg);
    }

    /**
     * Creates a {@link TransformationException} with given message and cause.
     *
     * @param msg The message to be used.
     * @param cause The cause of the exception.
     *
     * @see RuntimeException#RuntimeException(String, Throwable)
     */
    public TransformationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
