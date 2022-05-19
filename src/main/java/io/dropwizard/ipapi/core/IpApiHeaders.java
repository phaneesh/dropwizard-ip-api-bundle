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

package io.dropwizard.ipapi.core;

/**
 * @author phaneesh
 */
public interface IpApiHeaders {

    String X_COUNTRY = "X-REQUEST-COUNTRY";
    String X_COUNTRY_ISO = "X-REQUEST-COUNTRY-ISO";
    String X_STATE = "X-REQUEST-STATE";
    String X_STATE_ISO = "X-REQUEST-STATE-ISO";
    String X_CITY = "X-REQUEST-CITY";
    String X_POSTAL = "X-REQUEST-POSTAL-CODE";
    String X_LATITUDE = "X-REQUEST-LATITUDE";
    String X_LONGITUDE = "X-REQUEST-LONGITUDE";
    String X_ISP = "X-REQUEST-ISP";
}
