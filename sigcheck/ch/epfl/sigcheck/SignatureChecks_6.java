package ch.epfl.sigcheck;

import ch.epfl.rigel.astronomy.Asterism;
import ch.epfl.rigel.astronomy.AsterismLoader;
import ch.epfl.rigel.astronomy.CelestialObjectModel;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.Moon;
import ch.epfl.rigel.astronomy.MoonModel;
import ch.epfl.rigel.astronomy.Planet;
import ch.epfl.rigel.astronomy.PlanetModel;
import ch.epfl.rigel.astronomy.Star;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.astronomy.Sun;
import ch.epfl.rigel.astronomy.SunModel;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

final class SignatureChecks_6 {
    void checkMoonModel() {
        Enum<MoonModel> m1 = MoonModel.MOON;
        CelestialObjectModel<Moon> m2 = MoonModel.MOON;
    }

    void checkStarCatalogue() throws IOException {
        List<Star> sl = null;
        Star s = null;
        Asterism a = null;
        List<Asterism> al = null;
        Set<Asterism> as = null;
        List<Integer> il;
        StarCatalogue c = new StarCatalogue(sl, al);
        sl = c.stars();
        as = c.asterisms();
        il = c.asterismIndices(a);

        InputStream i = null;
        StarCatalogue.Loader l = null;
        StarCatalogue.Builder b = new StarCatalogue.Builder();
        b = b.addStar(s);
        sl = b.stars();
        b = b.addAsterism(a);
        al = b.asterisms();
        b = b.loadFrom(i, l);
        c = b.build();

        l.load(i, b);
    }

    void checkHygDatabaseLoader() {
        StarCatalogue.Loader l = HygDatabaseLoader.INSTANCE;
    }

    void checkAsterismLoader() {
        StarCatalogue.Loader l = AsterismLoader.INSTANCE;
    }

    static final class SignatureChecks_5 {
        //    void checkStar() {
        //        int i = 0;
        //        String s = null;
        //        EquatorialCoordinates e = null;
        //        float f = 0;
        //        Star t = new Star(i, s, e, f, f);
        //        i = t.hipparcosId();
        //        i = t.colorTemperature();
        //    }

        void checkAsterism() {
            List<Star> l = null;
            Asterism a = new Asterism(l);
            l = a.stars();
        }

        void checkCelestialObjectModel() {
            CelestialObjectModel<Sun> s = null;
            double d = 0;
            EclipticToEquatorialConversion e = null;
            Sun t = s.at(d, e);
        }

        void checkSunModel() {
            Enum<SunModel> m1 = SunModel.SUN;
            CelestialObjectModel<Sun> m2 = SunModel.SUN;
        }

        void checkPlanetModel() {
            List<PlanetModel> a = PlanetModel.ALL;
            Enum<PlanetModel> mercury1 = PlanetModel.MERCURY;
            CelestialObjectModel<Planet> mercury2 = PlanetModel.MERCURY;
            Enum<PlanetModel> venus1 = PlanetModel.VENUS;
            CelestialObjectModel<Planet> venus2 = PlanetModel.VENUS;
            Enum<PlanetModel> earth1 = PlanetModel.EARTH;
            CelestialObjectModel<Planet> earth2 = PlanetModel.EARTH;
            Enum<PlanetModel> mars1 = PlanetModel.MARS;
            CelestialObjectModel<Planet> mars2 = PlanetModel.MARS;
            Enum<PlanetModel> jupiter1 = PlanetModel.JUPITER;
            CelestialObjectModel<Planet> jupiter2 = PlanetModel.JUPITER;
            Enum<PlanetModel> saturn1 = PlanetModel.SATURN;
            CelestialObjectModel<Planet> saturn2 = PlanetModel.SATURN;
            Enum<PlanetModel> uranus1 = PlanetModel.URANUS;
            CelestialObjectModel<Planet> uranus2 = PlanetModel.URANUS;
            Enum<PlanetModel> neptune1 = PlanetModel.NEPTUNE;
            CelestialObjectModel<Planet> neptune2 = PlanetModel.NEPTUNE;
        }
    }
}
