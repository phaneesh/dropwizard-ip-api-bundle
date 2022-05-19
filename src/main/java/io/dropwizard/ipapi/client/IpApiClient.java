package io.dropwizard.ipapi.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import io.dropwizard.ipapi.core.IpLocationResponse;

public interface IpApiClient {

  @Headers({"Content-Type: application/json"})
  @RequestLine("GET /json/{query}")
  IpLocationResponse location(@Param("query") String query);

}
