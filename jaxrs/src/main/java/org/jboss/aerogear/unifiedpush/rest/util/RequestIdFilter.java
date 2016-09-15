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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * This filter will check for a request ID header in the request. The requestID will be applied to the logger thread context for output in any log statements.
 */
@WebFilter(urlPatterns = "*")
public class RequestIdFilter implements Filter {

  private final Logger logger = LoggerFactory.getLogger(RequestIdFilter.class);
  public static final String FH_REQUEST_ID = "X-FH-REQUEST-ID";

  public RequestIdFilter() {
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  public static String getRequestId(final HttpServletRequest request) {
    final String requestId = request.getHeader(FH_REQUEST_ID);
    if (requestId != null) {
      return  requestId;
    }
    return UUID.randomUUID().toString();
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    final String reqId = getRequestId(httpRequest);

    try {
      MDC.clear();
      //Adding the request ID to Thread Storage
      MDC.put("reqId", reqId);

      logger.debug("Got Request ID " + reqId);

      chain.doFilter(request, response);
    } finally {
      MDC.clear();
    }
  }

  public void destroy() {
  }
}
