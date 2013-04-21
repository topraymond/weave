/*
 * Copyright 2012-2013 Continuuity,Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.continuuity.weave.internal.yarn;

/**
 * Places for define common environment keys
 */
public final class EnvKeys {

  public static final String WEAVE_CONTAINER_ZK = "WEAVE_CONTAINER_ZK";
  public static final String WEAVE_RUN_ID = "WEAVE_RUN_ID";
  public static final String WEAVE_SPEC_PATH = "WEAVE_SPEC_PATH";
  public static final String WEAVE_RUNNABLE_NAME = "WEAVE_RUNNABLE_NAME";

  public static final String WEAVE_LOG_KAFKA_ZK = "WEAVE_LOG_KAFKA_ZK";

  public static final String YARN_CONTAINER_ID = "YARN_CONTAINER_ID";
  public static final String YARN_CONTAINER_HOST = "YARN_CONTAINER_HOST";
  public static final String YARN_CONTAINER_PORT = "YARN_CONTAINER_PORT";

  private EnvKeys() {
  }
}
