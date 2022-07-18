package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.coordinates.EquatorialToHorizontalConversion;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ObservedSkyTest2 {

    private static final String HYG_CATALOGUE_NAME =
            "/hygdata_v3.csv";
    private static final String AST_CATALOGUE_NAME =
            "/asterisms.txt";
    private static StarCatalogue catalogue;
    private static ObservedSky sky;
    private static StereographicProjection stereo;
    private static GeographicCoordinates geoCoords;
    private static ZonedDateTime time;
    private static EquatorialToHorizontalConversion convEquToHor;
    private static EclipticToEquatorialConversion convEcltoEqu;

    @Test
    void init() throws IOException {

        if (catalogue == null) {
            long time0 = System.nanoTime();
            StarCatalogue.Builder builder;
            try (InputStream hygStream = getClass()
                    .getResourceAsStream(HYG_CATALOGUE_NAME)) {
                builder = new StarCatalogue.Builder()
                        .loadFrom(hygStream, HygDatabaseLoader.INSTANCE);
            }
            try (InputStream astStream = getClass()
                    .getResourceAsStream(AST_CATALOGUE_NAME)) {
                catalogue = builder
                        .loadFrom(astStream, AsterismLoader.INSTANCE)
                        .build();
            }
            catalogue = builder.build();
            System.out.println((System.nanoTime() - time0) * 1e-6);

            time = ZonedDateTime.of(
                    LocalDate.of(2020, Month.APRIL, 4),
                    LocalTime.of(0, 0), ZoneOffset.UTC
            );

            geoCoords = GeographicCoordinates.ofDeg(30, 45);

            stereo = new StereographicProjection(HorizontalCoordinates.ofDeg(20, 22));

            convEquToHor = new EquatorialToHorizontalConversion(time, geoCoords);

            convEcltoEqu = new EclipticToEquatorialConversion(time);
            sky = new ObservedSky(time, geoCoords, stereo, catalogue);
        }
    }

    @Test
    void objectClosestToWorks() throws IOException {
        init();
        long time0 = 0;
        long timeAvg = 0;
        long total = 0;
        //        for (int i = 0; i<25; ++i)
        for (Asterism asterism : catalogue.asterisms()) {
            total += asterism.stars().size();
            for (Star star : asterism.stars()) {
                time0 = System.nanoTime();
                if (!star.name().equals("Xi UMa"))
                    assertEquals(star, sky.objectClosestTo(stereo.apply(convEquToHor.apply(star.equatorialPos())),
                                                           Double.MAX_VALUE, Set.of(CelestialObjectType.values())).get());

                timeAvg += System.nanoTime() - time0;

                //Rater le test ci-dessous = il faut mettre un <= distanceMax au lieu de < distanceMax
                assertEquals(star.name(), sky.objectClosestTo(stereo.apply(convEquToHor.apply(star.equatorialPos())),
                                                              0, Set.of(CelestialObjectType.values())).get().name());

                assertEquals(Optional.empty(), sky.objectClosestTo(stereo.apply(convEquToHor.apply(star.equatorialPos())),
                                                                   -10, Set.of(CelestialObjectType.values())));


            }

        }
        long time5 = System.nanoTime();
        assertEquals("Tau Phe",
                     sky.objectClosestTo(stereo.apply(new EquatorialToHorizontalConversion(time, geoCoords)
                                                              .apply(EquatorialCoordinates.of(0.004696959812148989, -0.861893035343076))), 0.1, Set.of(CelestialObjectType.values())).get().name());
        System.out.println(System.nanoTime() - time5);
        assertEquals(Optional.empty(),
                     sky.objectClosestTo(stereo.apply(new EquatorialToHorizontalConversion(time, geoCoords)
                                                              .apply(EquatorialCoordinates.of(0.004696959812148989, -0.8618930353430763))), 0.001, Set.of(CelestialObjectType.values())));

        //        System.out.println((timeAvg / (total * 1000000d))+" in milliseconds"); //PERFORMANCE BENCH
    }

    @Test
    void stars() throws IOException {
        init();
        int i = 0;
        for (Star star : sky.stars()) {
            assertEquals(stereo.apply(convEquToHor.apply(star.equatorialPos())).x(),
                         sky.starPositions()[i]);
            i += 2;
        }
        assertEquals(catalogue.stars().size(), sky.stars().size());

        //Si fail: Cloner le tableau
        double memory = sky.starPositions()[0];
        sky.starPositions()[0] = Double.MAX_VALUE;
        assertEquals(memory, sky.starPositions()[0]);
        assertEquals(5067 * 2, sky.starPositions().length);
    }

    @Test
    void planets() throws IOException {
        init();
        assertEquals(14, sky.planetPositions().length);

        int i = 0;
        for (Planet planet : sky.planets()) {
            assertEquals(stereo.apply(convEquToHor.apply(planet.equatorialPos())).x(),
                         sky.planetPositions()[i++]);
            assertEquals(stereo.apply(convEquToHor.apply(planet.equatorialPos())).y(),
                         sky.planetPositions()[i++]);
        }

        //Si fail: Cloner le tableau
        double memory = sky.planetPositions()[0];
        sky.planetPositions()[0] = Double.MAX_VALUE;
        assertEquals(memory, sky.planetPositions()[0]);
    }

    @Test
    void moonAndSun() throws IOException {
        init();
        assertEquals(SunModel.SUN.at(Epoch.J2010.daysUntil(time), convEcltoEqu).eclipticPos().lon(),
                     sky.sun().eclipticPos().lon());
        //Sun possède le getter equatorialPos mais autant tester la précision avec 2 conversions successives...
        assertEquals(stereo.apply(convEquToHor.apply(convEcltoEqu.apply(SunModel.SUN.at(Epoch.J2010.daysUntil(time), convEcltoEqu).eclipticPos()))).x(),
                     sky.sunPosition().x());
        assertEquals(stereo.apply(convEquToHor.apply(convEcltoEqu.apply(SunModel.SUN.at(Epoch.J2010.daysUntil(time), convEcltoEqu).eclipticPos()))).y(),
                     sky.sunPosition().y());

        assertEquals(MoonModel.MOON.at(Epoch.J2010.daysUntil(time), convEcltoEqu).equatorialPos().dec(),
                     sky.moon().equatorialPos().dec());
        assertEquals(stereo.apply(convEquToHor.apply(MoonModel.MOON.at(Epoch.J2010.daysUntil(time), convEcltoEqu).equatorialPos())).x(),
                     sky.moonPosition().x());
        assertEquals(stereo.apply(convEquToHor.apply(MoonModel.MOON.at(Epoch.J2010.daysUntil(time), convEcltoEqu).equatorialPos())).y(),
                     sky.moonPosition().y());
    }
}


