package tde;

import javafx.application.Application;

/**
 * Start the application using the main method of this class.
  */
public final class TDE {
    private TDE() { } // prevent instantiation
    static void main(final String[] args) {
        Application.launch(TDEApplication.class, args);
    }
}
