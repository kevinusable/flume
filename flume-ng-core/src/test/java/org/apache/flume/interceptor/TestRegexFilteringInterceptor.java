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
import org.apache.flume.interceptor.RegexFilteringInterceptor.Constants;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Charsets;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TestRegexFilteringInterceptor {

  @Test
  /** By default, we should pass through any event. */
  public void testDefaultBehavior() throws ClassNotFoundException,
      InstantiationException, IllegalAccessException {
    Interceptor.Builder builder = InterceptorBuilderFactory.newInstance(
        InterceptorType.REGEX_FILTER.toString());
    builder.configure(new Context());
    Interceptor interceptor = builder.build();

    Event event = EventBuilder.withBody("test", Charsets.UTF_8);

    Event filteredEvent = interceptor.intercept(event);
    Assert.assertNotNull(filteredEvent);
    Assert.assertEquals(event, filteredEvent);
  }

  @Test
  public void testInclusion() throws ClassNotFoundException,
      InstantiationException, IllegalAccessException {
    Interceptor.Builder builder = InterceptorBuilderFactory.newInstance(
        InterceptorType.REGEX_FILTER.toString());

    Context ctx = new Context();
//    ctx.put(Constants.REGEX, "(INFO.*)|(WARNING.*)");
    ctx.put(Constants.REGEX,"^\\{.*application.*\\}$");
    ctx.put(Constants.EXCLUDE_EVENTS, "false");

    builder.configure(ctx);
    Interceptor interceptor = builder.build();

//    Event shouldPass1 = EventBuilder.withBody("INFO: some message",
//        Charsets.UTF_8);
//    Assert.assertNotNull(interceptor.intercept(shouldPass1));
//
//    Event shouldPass2 = EventBuilder.withBody("WARNING: some message",
//        Charsets.UTF_8);
//    Assert.assertNotNull(interceptor.intercept(shouldPass2));
//
//    Event shouldNotPass = EventBuilder.withBody("DEBUG: some message",
//        Charsets.UTF_8);
//    Assert.assertNull(interceptor.intercept(shouldNotPass));

    Event shouldPass3 = EventBuilder.withBody("{\"logId\":\"\",\"logTrace\":\"\",\"level\":\"INFO \",\"application\":\"ltl-web\",\"owner\":\"LTL\",\"organization\":\"annto-lms\",\"time\":\"09:20:04.298\",\"class\":\"c.a.d.r.zookeeper.ZookeeperRegistry\",\"content\":\" [DUBBO] Subscribe: consumer://10.24.66.106/com.midea.lms.service.TmsViechleService?application=lms-ltl-web&category=providers,configurators,routers&check=false&default.check=false&default.retries=0&default.timeout=50000&dubbo=4.0.0&interface=com.midea.lms.service.TmsViechleService&methods=selectAll,selectById&organization=midea-it-wl&owner=lms&pid=18976&revision=0.0.1&side=consumer&timestamp=1480382404266, dubbo version: 4.0.0, current host: 10.24.66.106\"}", Charsets.UTF_8);
    Event shouldPass4 = EventBuilder.withBody("helloworld", Charsets.UTF_8);
    Assert.assertNotNull(interceptor.intercept(shouldPass3));
//    Assert.assertNotNull(interceptor.intercept(shouldPass4));
//    System.out.println("---->"+new String(interceptor.intercept(shouldPass3).getBody(), Charsets.UTF_8));

    builder.configure(ctx);
  }

  @Test
  public void testExclusion() throws ClassNotFoundException,
      InstantiationException, IllegalAccessException {
    Interceptor.Builder builder = InterceptorBuilderFactory.newInstance(
        InterceptorType.REGEX_FILTER.toString());

    Context ctx = new Context();
    ctx.put(Constants.REGEX, "(css|html|js|jpeg|jpg|png|gif|bmp|ico)");
    ctx.put(Constants.EXCLUDE_EVENTS, "true");

    builder.configure(ctx);
    Interceptor interceptor = builder.build();
    int i = 0,j=0;
    for (String eventBody : loadEvents()) {
      Event shouldPass1 = EventBuilder.withBody(eventBody,
              Charsets.UTF_8);
//      Assert.assertNotNull(interceptor.intercept(shouldPass1));
      if (interceptor.intercept(shouldPass1) == null) {
        i ++;
        System.out.println(eventBody);
      } else {

      }
      j++;
    }
    System.out.println("total: " + j + ",exclude: " + i);

//    Event shouldPass2 = EventBuilder.withBody("WARNING: some message",
//        Charsets.UTF_8);
//    Assert.assertNotNull(interceptor.intercept(shouldPass2));
//
//    Event shouldNotPass = EventBuilder.withBody("this message has DEBUG in it",
//        Charsets.UTF_8);
//    Assert.assertNull(interceptor.intercept(shouldNotPass));

    builder.configure(ctx);
  }

  @Test
  public void testLoadEvents() {
    loadEvents();
  }

  private List<String> loadEvents() {
    BufferedReader reader;
    List<String> results = new ArrayList<>();
    try {
       String line;
       reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("E:\\work\\日志收集配置\\ovo\\trace\\test.log"))));
       while ((line = reader.readLine()) != null) {
        results.add(line);
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return results;
  }
}