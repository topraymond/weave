/*
 * Copyright 2012-2013 Continuuity,Inc. All Rights Reserved.
 */
package com.continuuity.weave.internal.yarn;

import com.google.common.util.concurrent.Service;
import org.apache.hadoop.yarn.api.records.Resource;

/**
 * This interface provides abstraction for AM to interacts with YARN to abstract out YARN version specific
 * code, making multi-version compatibility easier.
 */
public interface YarnAMClient extends Service {

  void provision(Resource capability, ProvisionCallback completionCallback);
}
