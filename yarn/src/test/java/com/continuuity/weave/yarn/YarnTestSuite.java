/*
 * Copyright 2012-2013 Continuuity,Inc.
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
package com.continuuity.weave.yarn;

import com.continuuity.weave.api.WeaveRunner;
import com.continuuity.weave.api.WeaveRunnerService;
import com.continuuity.weave.filesystem.LocalLocationFactory;
import com.continuuity.weave.internal.zookeeper.InMemoryZKServer;
import com.continuuity.weave.yarn.utils.YarnUtils;
import com.google.common.collect.Iterables;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.MiniYARNCluster;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Test suite for all tests with mini yarn cluster.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
                      EchoServerTestRun.class,
                      ResourceReportTestRun.class,
                      TaskCompletedTestRun.class,
                      DistributeShellTestRun.class,
                      LocalFileTestRun.class,
                      FailureRestartTestRun.class,
                      ProvisionTimeoutTestRun.class
                    })
public class YarnTestSuite {
  private static final Logger LOG = LoggerFactory.getLogger(YarnTestSuite.class);

  @ClassRule
  public static TemporaryFolder tmpFolder = new TemporaryFolder();

  private static InMemoryZKServer zkServer;
  private static MiniYARNCluster cluster;
  private static WeaveRunnerService runnerService;
  private static YarnConfiguration config;

  @BeforeClass
  public static final void init() throws IOException {
    // Starts Zookeeper
    zkServer = InMemoryZKServer.builder().build();
    zkServer.startAndWait();

    // Start YARN mini cluster
    config = new YarnConfiguration(new Configuration());

    if (YarnUtils.isHadoop20()) {
      config.set("yarn.resourcemanager.scheduler.class",
                 "org.apache.hadoop.yarn.server.resourcemanager.scheduler.fifo.FifoScheduler");
    } else {
      config.set("yarn.resourcemanager.scheduler.class",
                 "org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler");
      config.set("yarn.scheduler.capacity.resource-calculator",
                 "org.apache.hadoop.yarn.util.resource.DominantResourceCalculator");
    }
    config.set("yarn.minicluster.fixed.ports", "true");
    config.set("yarn.nodemanager.vmem-pmem-ratio", "20.1");
    config.set("yarn.nodemanager.vmem-check-enabled", "false");
    config.set("yarn.scheduler.minimum-allocation-mb", "128");
    config.set("yarn.nodemanager.delete.debug-delay-sec", "3600");

    cluster = new MiniYARNCluster("test-cluster", 1, 1, 1);
    cluster.init(config);
    cluster.start();

    runnerService = createWeaveRunnerService();
    runnerService.startAndWait();
  }

  @AfterClass
  public static final void finish() {
    runnerService.stopAndWait();
    cluster.stop();
    zkServer.stopAndWait();
  }

  public static final WeaveRunner getWeaveRunner() {
    return runnerService;
  }

  /**
   * Creates an unstarted instance of {@link WeaveRunnerService}.
   */
  public static final WeaveRunnerService createWeaveRunnerService() throws IOException {
    return new YarnWeaveRunnerService(config, zkServer.getConnectionStr() + "/weave",
                                      new LocalLocationFactory(tmpFolder.newFolder()));
  }

  public static final <T> boolean waitForSize(Iterable<T> iterable, int count, int limit) throws InterruptedException {
    int trial = 0;
    int size = Iterables.size(iterable);
    while (size != count && trial < limit) {
      LOG.info("Waiting for {} size {} == {}", iterable, size, count);
      TimeUnit.SECONDS.sleep(1);
      trial++;
      size = Iterables.size(iterable);
    }
    return trial < limit;
  }
}
