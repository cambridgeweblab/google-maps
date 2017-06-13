package ucles.weblab.common.googlemaps;

import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import ucles.weblab.common.googlemaps.config.ProvideApiContextConfig;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(googleCities.searchForCities(searchStr, ENGLISH).stream().anyMatch(prediction -> prediction.description.equals(parisEn))).isTrue();
        assertThat(googleCities.searchForCities(searchStr, SPANISH).stream().anyMatch(prediction -> prediction.description.equals(parisEs))).isTrue();
    }

    @Test
    public void testFindCityOrCountryByLatLong() throws GoogleConnectionException {

        LatLng oneHillsRoad = new LatLng(52.19913570000001, 0.12795240000000002);
        GeocodingResult cambridgeCityInSpanish = googleCities.findCityOrCountryByLatLong(oneHillsRoad, SPANISH);
        assertThat(cambridgeCityInSpanish.formattedAddress).isEqualTo("Cambridge, Reino Unido");

        LatLng northSea = new LatLng(56.458425, 3.555048);
        GeocodingResult noCitiesInTheNorthSea = googleCities.findCityOrCountryByLatLong(northSea, SPANISH);
        assertThat(noCitiesInTheNorthSea).isNull();

        LatLng antarctica = new LatLng(-83.766245, -46.484898);
        GeocodingResult countryNotACity = googleCities.findCityOrCountryByLatLong(antarctica, SPANISH);
        assertThat(countryNotACity.formattedAddress).isEqualTo("Antártida");

    }
}