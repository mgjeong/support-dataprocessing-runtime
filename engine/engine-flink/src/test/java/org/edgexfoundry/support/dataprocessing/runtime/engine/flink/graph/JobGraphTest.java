/*******************************************************************************
 * Copyright 2018 Samsung Electronics All Rights Reserved.
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
 *
 *******************************************************************************/
package org.edgexfoundry.support.dataprocessing.runtime.engine.flink.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.edgexfoundry.support.dataprocessing.runtime.task.DataSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class JobGraphTest {

  private static final String ID = "testId";
  private static long count;

  @Before
  public void resetCount() {
    count = 1;
  }

  @Test
  public void testGetJobId() {
    JobGraph jobGraph = new JobGraph(ID, null);
    Assert.assertEquals(jobGraph.getJobId(), ID);
  }

  @Test(expected = IllegalStateException.class)
  public void testInitializeWithCycle() throws Exception {
    Map<Vertex, List<Vertex>> edges = new HashMap<>();
    List<Vertex> vertices = new ArrayList<>();
    vertices.add(new TestVertex(count++));
    vertices.add(new TestVertex(count++));
    vertices.add(new TestVertex(count++));

    List<Vertex> list = new ArrayList<>();
    list.add(vertices.get(1));
    edges.put(vertices.get(0), list);

    list = new ArrayList<>();
    list.add(vertices.get(2));
    edges.put(vertices.get(1), list);

    list = new ArrayList<>();
    list.add(vertices.get(0));
    edges.put(vertices.get(2), list);

    new JobGraph(ID, edges).initialize();
  }

  @Test
  public void testInitExecution() throws Exception {
    Map<Vertex, List<Vertex>> edges = new HashMap<>();
    List<Vertex> vertices = new ArrayList<>();
    vertices.add(new TestVertex(count++));
    vertices.add(new TestVertex(count++));
    vertices.add(new TestVertex(count++));

    List<Vertex> list = new ArrayList<>();
    list.add(vertices.get(1));
    edges.put(vertices.get(0), list);

    list = new ArrayList<>();
    list.add(vertices.get(2));
    edges.put(vertices.get(1), list);

    JobGraph jobGraph = new JobGraph(ID, edges);
    jobGraph.initialize();
    jobGraph.initExecution();
  }

  private final class TestVertex implements Vertex {

    private Long id;
    private DataStream<DataSet> data = Mockito.mock(DataStream.class);

    public TestVertex(Long id) {
      this.id = id;
    }

    @Override
    public Long getId() {
      return id;
    }

    @Override
    public DataStream<DataSet> serve() throws Exception {
      return data;
    }

    @Override
    public void setInflux(DataStream<DataSet> influx) {
      return;
    }
  }

}
