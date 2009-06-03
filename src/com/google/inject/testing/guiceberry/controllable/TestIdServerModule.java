/*
 * Copyright (C) 2008 Google Inc.
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
package com.google.inject.testing.guiceberry.controllable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.servlet.RequestScoped;
import com.google.inject.testing.guiceberry.TestId;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Installing this {@link Module} in your server injector will give you the 
 * canonical implementation of a {@link TestId} {@link Provider}, one that
 * reads from the {@link HttpServletRequest}'s {@link Cookie} named
 * {@code TestId.COOKIE_NAME}.
 * 
 * @author Luiz-Otavio Zorzella
 */
public class TestIdServerModule extends AbstractModule {

  @Provides
  TestId get(@CookieMap Multimap<String,Cookie> cookieMap) {
    Collection<Cookie> temp = cookieMap.get(TestId.COOKIE_NAME);
    if (temp.size() > 1) {
      throw new IllegalStateException(String.format(
          "There's more than one Cookie named '%s'.", TestId.COOKIE_NAME));
    } else if (temp.size() == 0) {
      return null;
    }
    return new TestId(temp.iterator().next());
  }

  @Override
  protected void configure() {
    bind(TestId.class).in(RequestScoped.class);
    bind(new TypeLiteral<Map<String,Cookie>>(){})
      .annotatedWith(CookieMap.class).in(RequestScoped.class);
  }
  
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.FIELD, ElementType.PARAMETER})
  @BindingAnnotation
  @interface CookieMap {}
  
  @Provides
  Map<String,Cookie> getCookieMap(
      Provider<HttpServletRequest> httpServletRequestProvider) {
    
    Cookie[] cookies = httpServletRequestProvider.get().getCookies();
    Builder<String, Cookie> temp = new ImmutableMap.Builder<String, Cookie>();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        temp.put(cookie.getName(), cookie);
      }
    }
    return temp.build();
  }
}