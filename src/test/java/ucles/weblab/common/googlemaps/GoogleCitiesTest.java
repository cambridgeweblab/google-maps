package ucles.weblab.common.googlemaps;

import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import ucles.weblab.common.googlemaps.config.ProvideApiContextConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@Import({ProvideApiContextConfig.class})
public class GoogleCitiesTest {

    @Autowired
    private GoogleCities googleCities;

    private static final String SPANISH = "es";
    private static final String ENGLISH = "en";

    @Test
    public void testSearchForCities() throws GoogleConnectionException {
        String searchStr = "par";
        String parisEn = "Paris, France";
        String parisEs = "París, Francia";
        assertTrue(googleCities.searchForCities(searchStr, ENGLISH).stream().anyMatch(prediction -> prediction.description.equals(parisEn)));
        assertTrue(googleCities.searchForCities(searchStr, SPANISH).stream().anyMatch(prediction -> prediction.description.equals(parisEs)));
    }

    @Test
    public void testFindCitiesByLatLong() throws GoogleConnectionException {

        LatLng oneHillsRoad = new LatLng(52.19913570000001, 0.12795240000000002);
        GeocodingResult cambridgeCityInSpanish = googleCities.findCityByLatLong(oneHillsRoad, SPANISH);
        assertEquals("Cambridge, Reino Unido", cambridgeCityInSpanish.formattedAddress);

        LatLng northSea = new LatLng(56.458425, 3.555048);
        GeocodingResult noCitiesInTheNorthSea = googleCities.findCityByLatLong(northSea, SPANISH);
        assertNull(noCitiesInTheNorthSea);

        LatLng antarctica = new LatLng(-83.766245, -46.484898);
        GeocodingResult regionNotACity = googleCities.findCityByLatLong(antarctica, SPANISH);
        assertNull(regionNotACity);

    }
}