package ucles.weblab.common.googlemaps;

import com.google.maps.model.PlaceDetails;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import ucles.weblab.common.googlemaps.config.ProvideApiContextConfig;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@Import({ProvideApiContextConfig.class})
public class GooglePlaceDetails_IT {

    private final static String PARIS_GOOGLE_ID = "ChIJD7fiBh9u5kcRYJSMaMOCCwQ";

    @Autowired
    private GooglePlaceDetails googlePlaceDetails;

    @Test
    public void testFetchByPlaceId() throws GoogleConnectionException {
        PlaceDetails placeDetails = googlePlaceDetails.fetchByPlaceId(PARIS_GOOGLE_ID, "es");
        assertThat(placeDetails.formattedAddress).isEqualTo("París, Francia");
        assertThat(googlePlaceDetails.fetchByPlaceId("Invalid google ID", "es")).isNull();
    }
}
