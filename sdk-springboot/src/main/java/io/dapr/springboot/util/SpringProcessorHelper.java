/*
 * Copyright 2021 The Dapr Authors
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
limitations under the License.
*/

package io.dapr.springboot.util;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper for any/all spring related utility jobs required.
 */
public class SpringProcessorHelper {

  /**
   * Method to provide all possible complete routes list fos this post method present in this controller class,
   * for mentioned topic.
   *
   * @param clazz     Controller class
   * @param method    Declared method for posting data
   * @param topicName Associated topic name
   * @return All possible routes for post mapping for this class and post method
   */
  public static List<String> getAllCompleteRoutesForPost(Class clazz, Method method, String topicName) {
    List<String> routesList = new ArrayList<>();
    RequestMapping clazzRequestMapping =
        (RequestMapping) clazz.getAnnotation(RequestMapping.class);
    String[] clazzLevelRoute = null;
    if (clazzRequestMapping != null) {
      clazzLevelRoute = clazzRequestMapping.value();
    }
    String[] postValueArray = getRoutesForPost(method, topicName);
    if (postValueArray != null && postValueArray.length >= 1) {
      for (String postValue : postValueArray) {
        if (clazzLevelRoute != null && clazzLevelRoute.length >= 1) {
          for (String clazzLevelValue : clazzLevelRoute) {
            String route = clazzLevelValue + confirmLeadingSlash(postValue);
            routesList.add(route);
          }
        } else {
          routesList.add(postValue);
        }
      }
    }
    return routesList;
  }

  private static String[] getRoutesForPost(Method method, String topicName) {
    String[] postValueArray = new String[] {topicName};
    PostMapping postMapping = method.getAnnotation(PostMapping.class);
    if (postMapping != null) {
      if (postMapping.path() != null && postMapping.path().length >= 1) {
        postValueArray = postMapping.path();
      } else if (postMapping.value() != null && postMapping.value().length >= 1) {
        postValueArray = postMapping.value();
      }
    } else {
      RequestMapping reqMapping = method.getAnnotation(RequestMapping.class);
      for (RequestMethod reqMethod : reqMapping.method()) {
        if (reqMethod == RequestMethod.POST) {
          if (reqMapping.path() != null && reqMapping.path().length >= 1) {
            postValueArray = reqMapping.path();
          } else if (reqMapping.value() != null && reqMapping.value().length >= 1) {
            postValueArray = reqMapping.value();
          }
          break;
        }
      }
    }
    return postValueArray;
  }

  private static String confirmLeadingSlash(String path) {
    if (path != null && path.length() >= 1) {
      if (!path.substring(0, 1).equals("/")) {
        return "/" + path;
      }
    }
    return path;
  }

}
