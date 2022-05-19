package io.dropwizard.ipapi.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IpLocationResponse {

  private String query;

  private String status;

  private String country;

  private String countryCode;

  private String region;

  private String regionName;

  private String timezone;

  private String isp;

  private String org;

  private String as;

  private double lat;

  private double lon;

  private String city;

  private String zip;

}
