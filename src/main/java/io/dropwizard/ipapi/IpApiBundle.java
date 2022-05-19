/*
 * Copyright 2016 Phaneesh Nagaraja <phaneesh.n@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dropwizard.ipapi;

import feign.Feign;
import feign.Logger.Level;
import feign.Response;
import feign.codec.ErrorDecoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.ipapi.client.IpApiClient;
import io.dropwizard.ipapi.config.IpApiConfig;
import io.dropwizard.ipapi.filter.IpApiRequestFilter;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author phaneesh
 */
public abstract class IpApiBundle<T extends Configuration> implements ConfiguredBundle<T> {

    public abstract IpApiConfig getMaxMindConfig(final T configuration);

    @Override
    public void initialize(Bootstrap<?> bootstrap) {

    }

    @Override
    public void run(final T configuration, final Environment environment) {
        var ipApiConfig = getMaxMindConfig(configuration);
        environment.jersey().register(new IpApiRequestFilter(ipApiConfig, createIpApiClient(ipApiConfig, environment)));
    }

    private IpApiClient createIpApiClient(IpApiConfig ipApiConfig, final Environment environment) {
        return Feign.builder()
            .client(new OkHttpClient())
            .encoder(new JacksonEncoder(environment.getObjectMapper()))
            .decoder(new JacksonDecoder(environment.getObjectMapper()))
            .logger(new Slf4jLogger(IpApiClient.class))
            .errorDecoder((methodKey, response) -> {
              try {
                return new WebApplicationException(IOUtils.toString(response.body().asReader(Charset.defaultCharset())), response.status());
              } catch (IOException e) {
                throw new WebApplicationException(e);
              }
            })
            .logLevel(Level.FULL)
            .target(IpApiClient.class, "http://ip-api.com");
    }
}
