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
     * the given coordinates, or the country if no city is found, or null if no country is found.
     *
     * @param languageCode  The language of the returned results - e.g 'es' will return results in Spanish
     */
    public GeocodingResult findCityOrCountryByLatLong(LatLng latLong, String languageCode) throws GoogleConnectionException {
        GeocodingApiRequest request = new GeocodingApiRequest(geoApiContext);
        request.latlng(latLong);
        request.language(languageCode);

        GeocodingResult[] results = await(request);
        GeocodingResult backupCountry = null;

        for(GeocodingResult result : results) {
            if(isCity(result.types)) {
                return result;
            }
            if(backupCountry == null && isCountry(result.types)) {
                backupCountry = result;
            }
        }
        return backupCountry;
    }

    /**
     * @return  True if one of the given types is a city
     * @see "https://developers.google.com/places/supported_types"
     */
    public static boolean isCity(AddressType[]  addressTypes) {
        for(AddressType addressType : addressTypes) {
            //Google defines a city as a LOCALITY or an ADMINISTRATIVE AREA LEVEL 3
            if(addressType == AddressType.LOCALITY || addressType == AddressType.ADMINISTRATIVE_AREA_LEVEL_3) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return  True if one of the given types is a country
     * @see "https://developers.google.com/places/supported_types"
     */
    public static boolean isCountry(AddressType[] addressTypes) {
        return Arrays.asList(addressTypes).contains(AddressType.COUNTRY);
    }

    /**
     * Sometimes the google API will include a left right mark in the city strings returned - this removes any such
     * characters to aid string comparison.
     */
    public static String removeBidi(String city) {
        return city.replaceAll("[\\u200E\\u200F]", "");
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
