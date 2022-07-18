package ch.epfl.rigel.coordinates;

import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.util.SplittableRandom;
import java.util.function.Supplier;

import static ch.epfl.rigel.coordinates.CoordinateAssertions.assertEquals2;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public class StereographicProjectionTest {
    @Test
    void circleCenterForParallelWorks() {
        HorizontalCoordinates center = HorizontalCoordinates.of(0, 0);
        StereographicProjection projection = new StereographicProjection(center);
        HorizontalCoordinates parallel = HorizontalCoordinates.of(0, 0);
        assertEquals2(CartesianCoordinates.of(0, Double.POSITIVE_INFINITY), projection.circleCenterForParallel(parallel), 1e-9);

        parallel = HorizontalCoordinates.of(3.14159265358, 1.5);
        assertEquals2(CartesianCoordinates.of(0, 1.002511304), projection.circleCenterForParallel(parallel), 1e-9);

        parallel = HorizontalCoordinates.of(0.001050408, -1.5);
        assertEquals2(CartesianCoordinates.of(0, -1.002511304), projection.circleCenterForParallel(parallel), 1e-9);

        parallel = HorizontalCoordinates.of(1.02154, 1.254628991);
        assertEquals2(CartesianCoordinates.of(0, 1.052150833), projection.circleCenterForParallel(parallel), 1e-9);

        center = HorizontalCoordinates.of(3.14159265358, 1.50215);
        projection = new StereographicProjection(center);
        parallel = HorizontalCoordinates.of(0.9548734, 0.014257852);
        assertEquals2(CartesianCoordinates.of(0, 0.067785632), projection.circleCenterForParallel(parallel), 1e-9);

        center = HorizontalCoordinates.of(2.545219642, -0.8525865);
        projection = new StereographicProjection(center);
        parallel = HorizontalCoordinates.of(0.9548734, -1.50500315);
        assertEquals2(CartesianCoordinates.of(0, -0.375845174), projection.circleCenterForParallel(parallel), 1e-9);
    }

    @Test
    void circleRadiusForParallelWorks() {
        HorizontalCoordinates center = HorizontalCoordinates.of(0, 0);
        StereographicProjection projection = new StereographicProjection(center);
        HorizontalCoordinates parallel = HorizontalCoordinates.of(0, 0);
        assertEquals(Double.POSITIVE_INFINITY, projection.circleRadiusForParallel(parallel), 1e-9);

        parallel = HorizontalCoordinates.of(3.14159265358, 1.5);
        assertEquals(0.070914844, projection.circleRadiusForParallel(parallel), 1e-9);

        parallel = HorizontalCoordinates.of(0.001050408, -1.5);
        assertEquals(-0.070914844, projection.circleRadiusForParallel(parallel), 1e-9);

        parallel = HorizontalCoordinates.of(1.02154, 1.254628991);
        assertEquals(0.327141217, projection.circleRadiusForParallel(parallel), 1e-9);

        center = HorizontalCoordinates.of(3.14159265358, 1.50215);
        projection = new StereographicProjection(center);
        parallel = HorizontalCoordinates.of(0.9548734, 0.014257852);
        assertEquals(0.988137414, projection.circleRadiusForParallel(parallel), 1e-9);

        center = HorizontalCoordinates.of(2.545219642, -0.8525865);
        projection = new StereographicProjection(center);
        parallel = HorizontalCoordinates.of(0.9548734, -1.50500315);
        assertEquals(-0.037551359, projection.circleRadiusForParallel(parallel), 1e-9);
    }

    @Test
    void applyAndInverseApplyAreCoherent() {
        double d = 1.5e2;
        SplittableRandom r = TestRandomizer.newRandom();
        Supplier<Double> rLon = () -> r.nextDouble(0, 6.283185307179586);
        Supplier<Double> rLat = () -> r.nextDouble(0, 1.5707963267948966);
        Supplier<Double> rC = () -> r.nextDouble(-d, d);
        Supplier<HorizontalCoordinates> rHor = () -> HorizontalCoordinates.of(rLon.get(), rLat.get());
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; ++i) {
            HorizontalCoordinates center = rHor.get();
            StereographicProjection projection = new StereographicProjection(center);
            for (int j = 0; j < TestRandomizer.RANDOM_ITERATIONS; ++j) {
                HorizontalCoordinates azAlt = rHor.get();
                assertEquals2(azAlt, projection.inverseApply(projection.apply(azAlt)), 1e-9);
                CartesianCoordinates coordinates = CartesianCoordinates.of(rC.get(), rC.get());
                assertEquals2(coordinates, projection.apply(projection.inverseApply(coordinates)), 1e-9);
            }
        }
    }

    @Test
    void applyWorks() {
        HorizontalCoordinates center = HorizontalCoordinates.of(0, 0);
        StereographicProjection projection = new StereographicProjection(center);
        HorizontalCoordinates azAlt = HorizontalCoordinates.of(0, 0);
        assertEquals2(CartesianCoordinates.of(0.0, 0.0), projection.apply(azAlt), 1e-9);

        azAlt = HorizontalCoordinates.of(3.14159265358, 1.5);
        assertEquals2(CartesianCoordinates.of(7.454747877686338e-13, 1.0734261485493775), projection.apply(azAlt), 1e-9);

        azAlt = HorizontalCoordinates.of(0.001050408, -1.5);
        assertEquals2(CartesianCoordinates.of(6.93941626926913e-05, -0.9315964938971256), projection.apply(azAlt), 1e-9);

        azAlt = HorizontalCoordinates.of(1.02154, 1.254628991);
        assertEquals2(CartesianCoordinates.of(0.22815831277444687, 0.8177043360380242), projection.apply(azAlt), 1e-9);

        center = HorizontalCoordinates.of(3.14159265358, 1.50215);
        projection = new StereographicProjection(center);
        azAlt = HorizontalCoordinates.of(0.9548734, 0.014257852);
        assertEquals2(CartesianCoordinates.of(-0.837427342015761, 0.5923149401203371), projection.apply(azAlt), 1e-9);

        center = HorizontalCoordinates.of(2.545219642, -0.8525865);
        projection = new StereographicProjection(center);
        azAlt = HorizontalCoordinates.of(0.9548734, -1.50500315);
        assertEquals2(CartesianCoordinates.of(-0.037550859306933006, -0.37565154082706465), projection.apply(azAlt), 1e-9);
    }

    @Test
    void applyWorks2(){
        HorizontalCoordinates h1 = HorizontalCoordinates.of(Math.PI/4, Math.PI/6);
        HorizontalCoordinates center1 = HorizontalCoordinates.of(0,0);
        StereographicProjection e = new StereographicProjection(center1);
        double p = Math.sqrt(6);
        CartesianCoordinates a1 = CartesianCoordinates.of(p/(4+p), 2/(4+p));
        CartesianCoordinates c1 = e.apply(h1);
        assertEquals(a1.x(), c1.x(), 1e-8);
        assertEquals(a1.y(), c1.y(), 1e-8);

        HorizontalCoordinates h2 = HorizontalCoordinates.of(Math.PI/2, Math.PI/2);
        HorizontalCoordinates center2 = HorizontalCoordinates.of(Math.PI/4, Math.PI/4);
        StereographicProjection e2 = new StereographicProjection(center2);
        double p2 = Math.sqrt(2);
        CartesianCoordinates a2 = CartesianCoordinates.of(0, p2/(2+p2));
        CartesianCoordinates c2 = e2.apply(h2);
        assertEquals(a2.x(), c2.x(), 1e-8);
        assertEquals(a2.y(), c2.y(), 1e-8);
    }

    @Test
    void circleCenterForParallelWorks2(){
        HorizontalCoordinates h1 = HorizontalCoordinates.of(Math.PI/4, Math.PI/6);
        HorizontalCoordinates center1 = HorizontalCoordinates.of(0,0);
        StereographicProjection s = new StereographicProjection(center1);
        CartesianCoordinates a1 = s.circleCenterForParallel(h1);
        assertEquals(0, a1.x(), 1e-10);
        assertEquals(2, a1.y(), 1e-10);
    }

    @Test
    void circleRadiusForParallelWorks2(){
        HorizontalCoordinates h2 = HorizontalCoordinates.of(Math.PI/2, Math.PI/2);
        HorizontalCoordinates center2 = HorizontalCoordinates.of(Math.PI/4, Math.PI/4);
        StereographicProjection e2 = new StereographicProjection(center2);
        double rho1 = e2.circleRadiusForParallel(h2);
        assertEquals(0, rho1, 1e-10);
    }

    @Test
    void applyToAngle(){
        HorizontalCoordinates center2 = HorizontalCoordinates.of(Math.PI/4, Math.PI/4);
        StereographicProjection e2 = new StereographicProjection(center2);
        double z = e2.applyToAngle(Math.PI/2);
        System.out.println(z);
    }

    @Test
    void inverseApplyWorks() {
        HorizontalCoordinates center = HorizontalCoordinates.of(0, 0);
        StereographicProjection projection = new StereographicProjection(center);
        CartesianCoordinates xy = CartesianCoordinates.of(0, 0);
        assertEquals2(HorizontalCoordinates.of(0.0, 0.0), projection.inverseApply(xy), 1e-9);

        xy = CartesianCoordinates.of(4.5, 1.5);
        assertEquals2(HorizontalCoordinates.of(2.745151370371352, 0.12800888628407858), projection.inverseApply(xy), 1e-9);

        xy = CartesianCoordinates.of(141651.1025, -104825.230);
        assertEquals2(HorizontalCoordinates.of(3.141583530513534, -6.751296321008142e-06), projection.inverseApply(xy), 1e-9);

        xy = CartesianCoordinates.of(1545751.021, 0.0);
        assertEquals2(HorizontalCoordinates.of(3.1415913597203584, 0.0), projection.inverseApply(xy), 1e-9);

        center = HorizontalCoordinates.of(3.14159265358, 1.50215);
        projection = new StereographicProjection(center);
        xy = CartesianCoordinates.of(0.0, 17841);
        assertEquals2(HorizontalCoordinates.of(6.283185307169793, -1.5020378986605059), projection.inverseApply(xy), 1e-9);

        center = HorizontalCoordinates.of(2.545219642, -0.8525865);
        projection = new StereographicProjection(center);
        xy = CartesianCoordinates.of(19254, -174258);
        assertEquals2(HorizontalCoordinates.of(5.686810391711612, 0.8525751611921659), projection.inverseApply(xy), 1e-9);
    }
}
