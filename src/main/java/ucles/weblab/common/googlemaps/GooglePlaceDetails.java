package ucles.weblab.common.googlemaps;

import com.google.maps.GeoApiContext;
import com.google.maps.PlaceDetailsRequest;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.errors.InvalidRequestException;
import com.google.maps.model.PlaceDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Methods that deal with google place IDs.
 */
@Component
public class GooglePlaceDetails {

    private GeoApiContext geoApiContext;

    private final Logger log = LoggerFactory.getLogger(GooglePlaceDetails.class);

    @Autowired
    public GooglePlaceDetails(GeoApiContext geoApiContext) {
        this.geoApiContext = geoApiContext;
    }

    /**
     * Fetch the details for a google place ID.
     *
     * @param language  The language code for the language the details returned should be in (e.g. 'es' for spanish, etc)
     * @return The details, or null if the place ID is invalid
     */
    public PlaceDetails fetchByPlaceId(String placeId, String language) throws GoogleConnectionException {
        PlaceDetailsRequest placeDetailsRequest = PlacesApi.placeDetails(geoApiContext, placeId);
        placeDetailsRequest.language(language);

        PlaceDetails placeDetails;
        try {
            placeDetails = placeDetailsRequest.await();
        } catch(InvalidRequestException ire) {
            //The provided ID was invalid - warn only
            log.warn("Invalid google place ID provided - " + placeId + ". Continuing without throwing resulting error", ire);
            return null;
        } catch(IOException |InterruptedException|ApiException e) {
            throw new GoogleConnectionException(e);
        }
        return placeDetails;
    }
}
