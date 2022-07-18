//package ch.epfl.rigel.gui;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//
//import java.time.ZonedDateTime;
//
///**
// * @author Antonio Jimenez (314363)
// * @author Alexis Horner (315780)
// */
//public class TimeAnimatorTest {
//    ZonedDateTime simulatedStart = ZonedDateTime.parse("2020-06-01T23:55:00+01:00");
//    TimeAccelerator accelerator = NamedTimeAccelerator.TIMES_300.getAccelerator();
//    TimeAccelerator acceleratorA = NamedTimeAccelerator.DAY.getAccelerator();
//    TimeAccelerator acceleratorB = NamedTimeAccelerator.SIDEREAL_DAY.getAccelerator();
//    TimeAccelerator acceleratorC = NamedTimeAccelerator.TIMES_1.getAccelerator();
//    TimeAccelerator acceleratorD;
//
//    TimeAnimator animatorA;
//    TimeAnimator animatorB;
//
//    DateTimeBean dateTimeB = new DateTimeBean();
//
//    @Test
//    void startAndStopWorks() {
//        animatorA = new TimeAnimator(dateTimeB);
//
//        Assertions.assertFalse(animatorA.isRunning());
//        animatorA.start();
//        Assertions.assertTrue(animatorA.isRunning());
//        animatorA.stop();
//        Assertions.assertFalse(animatorA.isRunning());
//
//    }
//
//    @Test
//    void AcceleratorWorks() {
//        animatorA = new TimeAnimator(dateTimeB);
//        animatorA.setAccelerator(accelerator);
//
//        Assertions.assertEquals(animatorA.accelerator(), accelerator);
//
//    }
//
//    @Test
//    void handleWorks() {
//        dateTimeB.setZonedDateTime(ZonedDateTime.now());
//        animatorA = new TimeAnimator(dateTimeB);
//        animatorA.setAccelerator(accelerator);
//        animatorA.start();
//        for (int i = 0; i < 50; ++i) {
//            animatorA.handle(System.currentTimeMillis());
//
//          //  System.out.println("Observation time: " + animatorA.getObservationTime());
//          //  System.out.println("Actual time: " + LocalTime.now());
//          //  System.out.println();
//            System.out.println("PreviousTime: " + animatorA.getPreviousTimeNano());
//            System.out.println("Actual time: " + System.currentTimeMillis());
//            System.out.println();
//        }
//        Assertions.assertEquals(animatorA.getPreviousTimeNano(), System.currentTimeMillis());
//
//
//    }
//
//    @Test
//    void handleException() {
//        dateTimeB.setZonedDateTime(simulatedStart);
//        animatorA = new TimeAnimator(dateTimeB);
//        animatorA.setAccelerator(accelerator);
//        Assertions.assertThrows(NullPointerException.class, () -> System.out.println(animatorA.getPreviousTimeNano()));
//    }
//
//    @Test
//    void setAcceleratorWorks() {
//        animatorA = new TimeAnimator(dateTimeB);
//
//        animatorA.setAccelerator(acceleratorA);
//        Assertions.assertEquals(animatorA.accelerator(), acceleratorA);
//
//        animatorA.setAccelerator(acceleratorB);
//        Assertions.assertEquals(animatorA.accelerator(), acceleratorB);
//
//        animatorA.setAccelerator(acceleratorC);
//        Assertions.assertEquals(animatorA.accelerator(), acceleratorC);
//
//        animatorA.setAccelerator(acceleratorD);
//        Assertions.assertEquals(animatorA.accelerator(), acceleratorD);
//    }
//
//    @Test
//    void isRunningWorks() {
//        animatorA = new TimeAnimator(dateTimeB);
//
//        animatorA.setRunning(true);
//        Assertions.assertTrue(animatorA.isRunning());
//
//        animatorA.setRunning(false);
//        Assertions.assertFalse(animatorA.isRunning());
//
//    }
//
//    @Test
//    void runningReadOnlyWorks() {
//        animatorA = new TimeAnimator(dateTimeB);
//        animatorB = new TimeAnimator(dateTimeB);
//
//        animatorA.setRunning(true);
//        animatorA.runningProperty().equals(animatorA.runningProperty());
//        Assertions.assertEquals(animatorA.runningProperty().toString(),
//                                "BooleanProperty [value: " + animatorA.runningProperty().get() + "]");
//        Assertions.assertEquals(animatorA.runningProperty().get(), animatorA.isRunning());
//    }
//
//}
