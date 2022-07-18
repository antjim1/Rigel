package ch.epfl.rigel.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.time.ZonedDateTime;

/**
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public final class UseTimeAnimator extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ZonedDateTime simulatedStart =
                ZonedDateTime.parse("2020-06-01T23:55:00+01:00");
        TimeAccelerator accelerator =
                 NamedTimeAccelerator.TIMES_3000.getAccelerator(); // Test 1 Separation expected: 50s, we get 48s
                // NamedTimeAccelerator.TIMES_30.getAccelerator(); // Test 2 Separation expected: 0.5s, we get 0.48s
                // NamedTimeAccelerator.TIMES_300.getAccelerator(); // Test 3 Separation expected: 5s, we get 4.8s
                //  NamedTimeAccelerator.DAY.getAccelerator();
                //  NamedTimeAccelerator.SIDEREAL_DAY.getAccelerator();

        DateTimeBean dateTimeB = new DateTimeBean();
        dateTimeB.setZonedDateTime(simulatedStart);

        TimeAnimator timeAnimator = new TimeAnimator(dateTimeB);
        timeAnimator.setAccelerator(accelerator);

        dateTimeB.dateProperty().addListener((p, o, n) -> {
            System.err.printf(" Nouvelle date : %s%n", n);
            Platform.exit();
        });
        dateTimeB.timeProperty().addListener((p, o, n) -> {
            System.err.printf("Nouvelle heure : %s%n", n);
        });

        timeAnimator.start();
    }
}
