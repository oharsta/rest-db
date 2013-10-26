/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.zilverline.rest.db;

import com.google.common.cache.CacheBuilderSpec;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.auth.oauth.OAuthProvider;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.zilverline.rest.db.resource.RestDbResource;

/**
 * Main entry
 */
public class RestDbService extends Service<RestDbConfiguration> {

  /*
   * Used by DropWizard to bootstrap the application. See README.md
   */
  public static void main(String[] args) throws Exception {
    if (args == null || args.length != 2) {
      args = new String[]{"server", "rest-db-local.yml"};
    }
    new RestDbService().run(args);
  }


  @Override
  public void initialize(Bootstrap<RestDbConfiguration> bootstrap) {
    bootstrap.setName("rest-db");
    bootstrap.addBundle(new AssetsBundle());
  }

  @Override
  public void run(RestDbConfiguration configuration, Environment environment) throws Exception {
    environment.addResource(new RestDbResource(configuration.getDb()));
  }
}
