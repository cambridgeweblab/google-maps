/*
 * Copyright 2016 Google Inc. All rights reserved.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package ucles.weblab.common.googlemaps.ibmjava;

import com.google.gson.FieldNamingPolicy;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.ApiResponse;
import com.google.maps.internal.ExceptionsAllowedToRetry;
import com.google.maps.internal.RateLimitExecutorService;
import okhttp3.Dispatcher;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Proxy;
import java.util.concurrent.TimeUnit;

/**
 * This is copied and pasted pretty much entirely from google's OkHttpRequestHandler, and required to support the IBM java on Bluemix.
 * Delete this when the main google maps api supports OkHttp3
 * @see com.google.maps.OkHttpRequestHandler
 */
public class OkHttp3RequestHandler implements GeoApiContext.RequestHandler {
    private static final Logger LOG = LoggerFactory.getLogger(OkHttp3RequestHandler.class.getName());
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client;
    private final RateLimitExecutorService rateLimitExecutorService;
    private final Dispatcher dispatcher;

    public OkHttp3RequestHandler() {
        rateLimitExecutorService = new RateLimitExecutorService();
        dispatcher = new Dispatcher(rateLimitExecutorService);
        OkHttpClient.Builder builder = new OkHttpClient.Builder().dispatcher(dispatcher);
        client = builder.build();
    }

    @Override
    public <T, R extends ApiResponse<T>> PendingResult<T> handle(String hostName, String url, String userAgent,
                                                                 Class<R> clazz, FieldNamingPolicy fieldNamingPolicy,
                                                                 long errorTimeout, Integer maxRetries,
                                                                 ExceptionsAllowedToRetry exceptionsAllowedToRetry) {
        Request req = new Request.Builder()
                .get()
                .header("User-Agent", userAgent)
                .url(hostName + url).build();

        LOG.info("Request: {}", hostName + url);

        return new OkHttp3PendingResult<>(req, client, clazz, fieldNamingPolicy, errorTimeout, maxRetries, exceptionsAllowedToRetry);
    }

    @Override
    public <T, R extends ApiResponse<T>> PendingResult<T> handlePost(String hostName, String url, String payload,
                                                                     String userAgent, Class<R> clazz,
                                                                     FieldNamingPolicy fieldNamingPolicy,
                                                                     long errorTimeout, Integer maxRetries,
                                                                     ExceptionsAllowedToRetry exceptionsAllowedToRetry) {
        RequestBody body = RequestBody.create(JSON, payload);
        Request req = new Request.Builder()
                .post(body)
                .header("User-Agent", userAgent)
                .url(hostName + url).build();

        return new OkHttp3PendingResult<>(req, client, clazz, fieldNamingPolicy, errorTimeout, maxRetries, exceptionsAllowedToRetry);
    }

    @Override
    public void setConnectTimeout(long timeout, TimeUnit unit) {
        throw new UnsupportedOperationException("OkHttp3 uses a builder and doesn't support updates to its values");
    }

    @Override
    public void setReadTimeout(long timeout, TimeUnit unit) {
        throw new UnsupportedOperationException("OkHttp3 uses a builder and doesn't support updates to its values");
    }

    @Override
    public void setWriteTimeout(long timeout, TimeUnit unit) {
        throw new UnsupportedOperationException("OkHttp3 uses a builder and doesn't support updates to its values");
    }

    @Override
    public void setQueriesPerSecond(int maxQps) {
        dispatcher.setMaxRequests(maxQps);
        dispatcher.setMaxRequestsPerHost(maxQps);
        rateLimitExecutorService.setQueriesPerSecond(maxQps);
    }

    @Override
    public void setQueriesPerSecond(int maxQps, int minimumInterval) {
        dispatcher.setMaxRequests(maxQps);
        dispatcher.setMaxRequestsPerHost(maxQps);
        rateLimitExecutorService.setQueriesPerSecond(maxQps, minimumInterval);
    }

    @Override
    public void setProxy(Proxy proxy) {
        throw new UnsupportedOperationException("OkHttp3 uses a builder and doesn't support updates to its values");
    }

}
