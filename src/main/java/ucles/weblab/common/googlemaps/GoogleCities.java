package ucles.weblab.common.googlemaps;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.PendingResult;
import com.google.maps.PlaceAutocompleteRequest;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.AddressType;
import com.google.maps.model.AutocompletePrediction;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceAutocompleteType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Contains methods that make requests to google for the caller, relating to cities only.
 */
@Component
public class GoogleCities {

    private final Logger log = LoggerFactory.getLogger(GoogleCities.class);

    private GeoApiContext geoApiContext;

    @Autowired
    public GoogleCities(GeoApiContext geoApiContext) {
        this.geoApiContext = geoApiContext;
    }

    /**
     * Find cities based on a search string.
     *
     * @param languageCode  The language of the returned results - e.g 'es' will return results in Spanish
     */
    public List<AutocompletePrediction> searchForCities(String searchValue, String languageCode) throws GoogleConnectionException {
        PlaceAutocompleteRequest request = PlacesApi.placeAutocomplete(geoApiContext, searchValue);

        request.language(languageCode);
        request.type(PlaceAutocompleteType.CITIES);

        return Arrays.asList(await(request));
    }

    /**
     * Finds the first result that is a city (defined by google as a locatality or administrative area level 3) for
     * the given coordinates, or null if no city is found.
     *
     * @param languageCode  The language of the returned results - e.g 'es' will return results in Spanish
     */
    public GeocodingResult findCityByLatLong(LatLng latLong, String languageCode) throws GoogleConnectionException {
        GeocodingApiRequest request = new GeocodingApiRequest(geoApiContext);
        request.latlng(latLong);
        request.language(languageCode);

        GeocodingResult[] results = await(request);

        for(GeocodingResult result : results) {
            if(isCity(result.types)) {
                return result;
            }
        }
        return null;
    }

    private static boolean isCity(AddressType[]  addressTypes) {
        for(AddressType addressType : addressTypes) {
            //Google defines a city as a LOCALITY or an ADMINISTRATIVE AREA LEVEL 3 - see right at the bottom here https://developers.google.com/places/supported_types
            if(addressType == AddressType.LOCALITY || addressType == AddressType.ADMINISTRATIVE_AREA_LEVEL_3) {
                return true;
            }
        }
        return false;
    }

    private <T> T await(PendingResult<T> pendingResult) throws GoogleConnectionException {
        try {
            return pendingResult.await();
        } catch(IOException |InterruptedException|ApiException e) {
            log.error("Encountered an exception while trying to connect to the google maps server", e);
            throw new GoogleConnectionException(e);
        }
    }
}
