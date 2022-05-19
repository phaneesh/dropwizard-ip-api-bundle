/*
 * Copyright (c) 2016 Phaneesh Nagaraja <phaneesh.n@gmail.com>.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.dropwizard.ipapi.filter;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.base.Strings;
import io.dropwizard.ipapi.client.IpApiClient;
import io.dropwizard.ipapi.config.IpApiConfig;
import io.dropwizard.ipapi.core.IpApiHeaders;
import io.dropwizard.ipapi.core.IpLocationResponse;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.net.InetAddress;
import java.time.Duration;
import java.util.Objects;

/**
 * @author phaneesh
 */
@Slf4j
@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class IpApiRequestFilter implements ContainerRequestFilter {

  private final IpApiConfig config;

  private final IpApiClient client;

  private LoadingCache<String, IpLocationResponse> locationCache = Caffeine.newBuilder()
      .maximumSize(10_00000)
      .expireAfterWrite(Duration.ofMinutes(60))
      .build(this::getLocationInfo);

  public IpApiRequestFilter(IpApiConfig config, IpApiClient client) {
    this.config = config;
    this.client = client;
  }

  @Override
  public void filter(final ContainerRequestContext containerRequestContext) throws IOException {
    final String clientAddress = containerRequestContext.getHeaders().getFirst(config.getRemoteIpHeader());
    if (Strings.isNullOrEmpty(clientAddress)) {
      return;
    }
    if (log.isDebugEnabled())
      log.debug("Header: {} | Value: {}", config.getRemoteIpHeader(), clientAddress);
    //Multiple Client ip addresses are being sent in case of multiple people stamping the request
    final String[] addresses = clientAddress.split(",");
    final String clientIp = addresses[0].split(":")[0];
    InetAddress address;
    if (!Strings.isNullOrEmpty(clientIp)) {
      try {
        address = InetAddress.getByName(clientIp);
      } catch (Exception e) {
        log.warn("Cannot resolve address: {} | Error: {}", clientIp, e.getMessage());
        return;
      }
      //Short circuit if there is no ip address
      if (address == null) {
        log.warn("Cannot resolve address: {}", clientIp);
        return;
      }
      try {
        var locInfo = locationCache.get(clientIp);
        if(Objects.nonNull(locInfo)) {
          addLocationHeaders(locInfo, containerRequestContext);
        }
      } catch (Exception ex) {
        log.warn("IpAPI Error: {}", ex.getMessage());
      }
    }
  }

  private IpLocationResponse getLocationInfo(String address) {
    try {
      return client.location(address);
    } catch (Exception ex) {
      log.warn("IpAPI Error: {}", ex.getMessage());
      return null;
    }
  }

  private void addLocationHeaders(IpLocationResponse ipLocationResponse, final ContainerRequestContext containerRequestContext) {
    if (!Strings.isNullOrEmpty(ipLocationResponse.getCountry()))
      containerRequestContext.getHeaders().putSingle(IpApiHeaders.X_COUNTRY, toAscii(ipLocationResponse.getCountry()));
    if (!Strings.isNullOrEmpty(ipLocationResponse.getCountryCode()))
      containerRequestContext.getHeaders().putSingle(IpApiHeaders.X_COUNTRY_ISO, ipLocationResponse.getCountryCode());
    if (!Strings.isNullOrEmpty(ipLocationResponse.getRegion()))
      containerRequestContext.getHeaders().putSingle(IpApiHeaders.X_STATE_ISO, ipLocationResponse.getRegion());
    if (!Strings.isNullOrEmpty(ipLocationResponse.getRegionName()))
      containerRequestContext.getHeaders().putSingle(IpApiHeaders.X_STATE, ipLocationResponse.getRegionName());
    if (!Strings.isNullOrEmpty(ipLocationResponse.getCity()))
      containerRequestContext.getHeaders().putSingle(IpApiHeaders.X_CITY, ipLocationResponse.getCity());
    if (!Strings.isNullOrEmpty(ipLocationResponse.getIsp()))
      containerRequestContext.getHeaders().putSingle(IpApiHeaders.X_ISP, ipLocationResponse.getIsp());
    if (!Strings.isNullOrEmpty(ipLocationResponse.getZip()))
      containerRequestContext.getHeaders().putSingle(IpApiHeaders.X_POSTAL, ipLocationResponse.getZip());
    containerRequestContext.getHeaders().putSingle(IpApiHeaders.X_LATITUDE, String.valueOf(ipLocationResponse.getLat()));
    containerRequestContext.getHeaders().putSingle(IpApiHeaders.X_LONGITUDE, String.valueOf(ipLocationResponse.getLon()));
  }

  private String toAscii(String input) {
    if (!Strings.isNullOrEmpty(input)) {
      return input.replaceAll("[^\\x20-\\x7e]", "");
    }
    return input;
  }

}