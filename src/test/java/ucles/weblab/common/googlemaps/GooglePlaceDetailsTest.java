package ucles.weblab.common.googlemaps;

import com.google.maps.model.PlaceDetails;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import ucles.weblab.common.googlemaps.config.ProvideApiContextConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@Import({ProvideApiContextConfig.class})
public class GooglePlaceDetailsTest {

    private final static String PARIS_GOOGLE_ID = "ChIJD7fiBh9u5kcRYJSMaMOCCwQ";

    @Autowired
    private GooglePlaceDetails googlePlaceDetails;

    @Test
    public void testFetchByPlaceId() throws GoogleConnectionException {
        PlaceDetails placeDetails = googlePlaceDetails.fetchByPlaceId(PARIS_GOOGLE_ID, "es");
        assertEquals("París, Francia", placeDetails.formattedAddress);
        assertNull(googlePlaceDetails.fetchByPlaceId("Invalid google ID", "es"));
    }
}
