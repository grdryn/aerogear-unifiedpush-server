/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.aerogear.unifiedpush.rest.util;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.verify;

public class RequestIdFilterTest {

  public static final String FH_REQUEST_ID = "X-FH-REQUEST-ID";

  @Mock
  HttpServletRequest request;
  @Mock
  HttpServletResponse response;
  @Mock
  FilterChain chain;

  @Test
  public void requestIdHeaderAssigned() throws Exception {
    MockitoAnnotations.initMocks(this);

    RequestIdFilter filter = new RequestIdFilter();

    String mockString = "51f7b609-a852-4744-b07e-66b43af343e3";
    Mockito.when(request.getHeader(FH_REQUEST_ID)).thenReturn(mockString);
    filter.doFilter(request, response, chain);

    verify(chain).doFilter(request, response);
  }
}