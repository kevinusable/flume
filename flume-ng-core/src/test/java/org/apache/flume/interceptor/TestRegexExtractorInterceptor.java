/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flume.interceptor;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.interceptor.Interceptor.Builder;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;

import java.util.Map;

public class TestRegexExtractorInterceptor {

  private Builder fixtureBuilder;

  @Before
  public void init() throws Exception {
    fixtureBuilder = InterceptorBuilderFactory
        .newInstance(InterceptorType.REGEX_EXTRACTOR.toString());
  }

  @Test
  public void shouldNotAllowConfigurationWithoutRegex() throws Exception {
    try {
      fixtureBuilder.build();
      Assert.fail();
    } catch (IllegalArgumentException ex) {
      // Pass...
    }
  }

  @Test
  public void shouldNotAllowConfigurationWithIllegalRegex() throws Exception {
    try {
      Context context = new Context();
      context.put(RegexExtractorInterceptor.REGEX, "?&?&&&?&?&?&&&??");
      fixtureBuilder.configure(context);
      fixtureBuilder.build();
      Assert.fail();
    } catch (IllegalArgumentException ex) {
      // Pass...
    }
  }

  @Test
  public void shouldNotAllowConfigurationWithoutMatchIds() throws Exception {
    try {
      Context context = new Context();
      context.put(RegexExtractorInterceptor.REGEX, ".*");
      context.put(RegexExtractorInterceptor.SERIALIZERS, "");
      fixtureBuilder.configure(context);
      fixtureBuilder.build();
      Assert.fail();
    } catch (IllegalArgumentException ex) {
      // Pass...
    }
  }

  @Test
  public void shouldNotAllowMisconfiguredSerializers() throws Exception {
    try {
      Context context = new Context();
      context.put(RegexExtractorInterceptor.REGEX, "(\\d):(\\d):(\\d)");
      context.put(RegexExtractorInterceptor.SERIALIZERS, ",,,");
      fixtureBuilder.configure(context);
      fixtureBuilder.build();
      Assert.fail();
    } catch (IllegalArgumentException ex) {
      // Pass...
    }
  }

  @Test
  public void shouldNotAllowEmptyNames() throws Exception {
    try {
      String space = " ";
      Context context = new Context();
      context.put(RegexExtractorInterceptor.REGEX, "(\\d):(\\d):(\\d)");
      context.put(RegexExtractorInterceptor.SERIALIZERS,
          Joiner.on(',').join(space, space, space));
      fixtureBuilder.configure(context);
      fixtureBuilder.build();
      Assert.fail();
    } catch (IllegalArgumentException ex) {
      // Pass...
    }
  }

  @Test
  public void shouldExtractAddHeadersForAllMatchGroups() throws Exception {
    Context context = new Context();
    context.put(RegexExtractorInterceptor.REGEX, "#(.*?)\\|(.*?)\\|(.*?)\\|(.*?)\\|(.*?)\\|(.*?)\\|(.*?)\\|(.*?)\\|(.*?)\\|(.*?)\\|([\\s\\S]*)");
    context.put(RegexExtractorInterceptor.SERIALIZERS, "logId logTrace level project env application ower organization time className content");
    context.put(RegexExtractorInterceptor.SERIALIZERS + ".logId.name", "logId");
    context.put(RegexExtractorInterceptor.SERIALIZERS + ".logTrace.name", "logTrace");
    context.put(RegexExtractorInterceptor.SERIALIZERS + ".level.name", "level");
    context.put(RegexExtractorInterceptor.SERIALIZERS + ".project.name", "project");
    context.put(RegexExtractorInterceptor.SERIALIZERS + ".env.name", "env");
    context.put(RegexExtractorInterceptor.SERIALIZERS + ".application.name", "application");
    context.put(RegexExtractorInterceptor.SERIALIZERS + ".ower.name", "ower");
    context.put(RegexExtractorInterceptor.SERIALIZERS + ".organization.name", "organization");
    context.put(RegexExtractorInterceptor.SERIALIZERS + ".time.name", "time");
    context.put(RegexExtractorInterceptor.SERIALIZERS + ".className.name", "className");
    context.put(RegexExtractorInterceptor.SERIALIZERS + ".content.name", "content");

    fixtureBuilder.configure(context);
    Interceptor fixture = fixtureBuilder.build();

    Event event = EventBuilder.withBody("#b41f4ed37bb34a938081e733f550cbc6||INFO |LTL01|uat|ltl-web|annto|6606371292|2017-05-09 09:42:42.806|com.midea.lms.filter.SSOFilter|\"init session:wujq6|吴金琪|38374 init last site code $$ALL$$...\"",Charsets.UTF_8);
//    Event expected = EventBuilder.withBody("#AC46B71B-324E-4E5E-B4CD-2EC75178BDE6|0.0.1|INFO|ltl-web|LTL|annto-lms|09:27:23.432|com.midea.lms.session.SessionManager|get account from session, account lilu6 ..", Charsets.UTF_8);
//    expected.getHeaders().put("Num1", "1");
//    expected.getHeaders().put("Num2", "2");
//    expected.getHeaders().put("Num3", "3");

    Event actual = fixture.intercept(event);
    for(Map.Entry<String,String> headers: actual.getHeaders().entrySet()) {
      System.out.println(headers.getKey()+"===="+headers.getValue());
    }
    System.out.println(new String(actual.getBody()));


//    Assert.assertArrayEquals(expected.getBody(), actual.getBody());
//    Assert.assertEquals(expected.getHeaders(), actual.getHeaders());
  }

  @Test
  public void shouldExtractAddHeadersForAllMatchGroupsIgnoringMissingIds()
      throws Exception {
    String body = "2012-10-17 14:34:44,338";
    Context context = new Context();
    // Skip the second group
    context.put(RegexExtractorInterceptor.REGEX,
        "^(\\d\\d\\d\\d-\\d\\d-\\d\\d\\s\\d\\d:\\d\\d)(:\\d\\d,\\d\\d\\d)");
    context.put(RegexExtractorInterceptor.SERIALIZERS, "s1");
    context
        .put(RegexExtractorInterceptor.SERIALIZERS + ".s1.name", "timestamp");

    fixtureBuilder.configure(context);
    Interceptor fixture = fixtureBuilder.build();

    Event event = EventBuilder.withBody(body, Charsets.UTF_8);
    Event expected = EventBuilder.withBody(body, Charsets.UTF_8);
    expected.getHeaders().put("timestamp", "2012-10-17 14:34");

    Event actual = fixture.intercept(event);

    Assert.assertArrayEquals(expected.getBody(), actual.getBody());
    Assert.assertEquals(expected.getHeaders(), actual.getHeaders());

  }

  @Test
  public void shouldExtractAddHeadersUsingSpecifiedSerializer()
      throws Exception {
    long now = (System.currentTimeMillis() / 60000L) * 60000L;
    String pattern = "yyyy-MM-dd HH:mm:ss,SSS";
    DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern);
    String body = formatter.print(now);
    System.out.println(body);
    Context context = new Context();
    // Skip the second group
    context.put(RegexExtractorInterceptor.REGEX,
        "^(\\d\\d\\d\\d-\\d\\d-\\d\\d\\s\\d\\d:\\d\\d)(:\\d\\d,\\d\\d\\d)");
    context.put(RegexExtractorInterceptor.SERIALIZERS, "s1 s2");

    String millisSerializers = RegexExtractorInterceptorMillisSerializer.class.getName();
    context.put(RegexExtractorInterceptor.SERIALIZERS + ".s1.type", millisSerializers);
    context.put(RegexExtractorInterceptor.SERIALIZERS + ".s1.name", "timestamp");
    context.put(RegexExtractorInterceptor.SERIALIZERS + ".s1.pattern", "yyyy-MM-dd HH:mm");

    // Default type
    context.put(RegexExtractorInterceptor.SERIALIZERS + ".s2.name", "data");

    fixtureBuilder.configure(context);
    Interceptor fixture = fixtureBuilder.build();

    Event event = EventBuilder.withBody(body, Charsets.UTF_8);
    Event expected = EventBuilder.withBody(body, Charsets.UTF_8);
    expected.getHeaders().put("timestamp", String.valueOf(now));
    expected.getHeaders().put("data", ":00,000");

    Event actual = fixture.intercept(event);

    Assert.assertArrayEquals(expected.getBody(), actual.getBody());
    Assert.assertEquals(expected.getHeaders(), actual.getHeaders());
  }
}
