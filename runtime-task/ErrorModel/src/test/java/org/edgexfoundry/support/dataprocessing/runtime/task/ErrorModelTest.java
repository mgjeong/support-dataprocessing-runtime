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
package org.edgexfoundry.support.dataprocessing.runtime.task;

import org.edgexfoundry.support.dataprocessing.runtime.task.model.ErrorModel;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class ErrorModelTest {

  String[] records = {
      "{\"FIELD1\":4.236298,\"FIELD2\":21.9821,\"FIELD3\":-0.3503797,\"FIELD4\":97.52163}",
      "{\"FIELD1\":1.657013,\"FIELD2\":80.66301,\"FIELD3\":0.2126006,\"FIELD4\":68.66834}",
      "{\"FIELD1\":2.804001,\"FIELD2\":22.353,\"FIELD3\":0.3414961,\"FIELD4\":80.95125}",
      "{\"FIELD1\":2.835701,\"FIELD2\":30.2016,\"FIELD3\":0.3855923,\"FIELD4\":94.89861}",
      "{\"FIELD1\":1.011299,\"FIELD2\":36.39751,\"FIELD3\":-0.1509783,\"FIELD4\":65.72945}",
      "{\"FIELD1\":25.852,\"FIELD2\":42.67701,\"FIELD3\":0.1589062,\"FIELD4\":69.20668}",
      "{\"FIELD1\":1.5354,\"FIELD2\":35.2678,\"FIELD3\":0.2476902,\"FIELD4\":62.75486}",
      "{\"FIELD1\":0.9810982,\"FIELD2\":43.577,\"FIELD3\":0.1706926,\"FIELD4\":111.5596}",
      "{\"FIELD1\":5.011002,\"FIELD2\":33.657,\"FIELD3\":0.2070035,\"FIELD4\":84.48615}",
      "{\"FIELD1\":0.5179977,\"FIELD2\":19.36099,\"FIELD3\":-0.2641146,\"FIELD4\":42.66606}",
      "{\"FIELD1\":0.6478004,\"FIELD2\":31.063,\"FIELD3\":0.2930824,\"FIELD4\":124.0073}",
      "{\"FIELD1\":1.225105,\"FIELD2\":25.7122,\"FIELD3\":0.3076169,\"FIELD4\":61.17281}",
      "{\"FIELD1\":1.225105,\"FIELD2\":25.7122,\"FIELD3\":0.3076169,\"FIELD4\":61.17281}",
      "{\"FIELD1\":1.225105,\"FIELD2\":25.7122,\"FIELD3\":0.3076169,\"FIELD4\":61.17281}",
      "{\"FIELD1\":1.433899,\"FIELD2\":9.8368,\"FIELD3\":0.2920688,\"FIELD4\":64.94667}",
      "{\"FIELD1\":1.941299,\"FIELD2\":38.4189,\"FIELD3\":-0.3777421,\"FIELD4\":85.24147}",
      "{\"FIELD1\":1.941299,\"FIELD2\":38.4189,\"FIELD3\":-0.3777421,\"FIELD4\":85.24147}",
      "{\"FIELD1\":13.7101,\"FIELD2\":11.9286,\"FIELD3\":-0.266998,\"FIELD4\":92.47059}",
      "{\"FIELD1\":2.462303,\"FIELD2\":28.7939,\"FIELD3\":0.168242,\"FIELD4\":92.79195}",
      "{\"FIELD1\":40.823,\"FIELD2\":268.315,\"FIELD3\":-0.1192806,\"FIELD4\":140.4156}",
      "{\"FIELD1\":34.04398,\"FIELD2\":195.447,\"FIELD3\":0.2151943,\"FIELD4\":161.9492}",
      "{\"FIELD1\":147.1299,\"FIELD2\":325.6399,\"FIELD3\":-0.1747214,\"FIELD4\":172.2188}",
      "{\"FIELD1\":21.57201,\"FIELD2\":72.10201,\"FIELD3\":0.2791926,\"FIELD4\":153.6269}",
      "{\"FIELD1\":43.104,\"FIELD2\":140.136,\"FIELD3\":-0.09101307,\"FIELD4\":110.5763}",
      "{\"FIELD1\":52.87903,\"FIELD2\":286.452,\"FIELD3\":0.2273007,\"FIELD4\":144.3571}",
      "{\"FIELD1\":18.94098,\"FIELD2\":124.101,\"FIELD3\":-0.1240541,\"FIELD4\":70.66239}",
      "{\"FIELD1\":50.66202,\"FIELD2\":177.417,\"FIELD3\":-0.1213853,\"FIELD4\":114.7567}",
      "{\"FIELD1\":19.7442,\"FIELD2\":82.3162,\"FIELD3\":-0.2416152,\"FIELD4\":112.7297}",
      "{\"FIELD1\":28.613,\"FIELD2\":134.832,\"FIELD3\":0.2005066,\"FIELD4\":172.7764}",
      "{\"FIELD1\":52.00302,\"FIELD2\":160.921,\"FIELD3\":0.1393138,\"FIELD4\":131.1788}",
      "{\"FIELD1\":39.946,\"FIELD2\":84.3167,\"FIELD3\":0.1308845,\"FIELD4\":163.2758}",
      "{\"FIELD1\":25.164,\"FIELD2\":125.938,\"FIELD3\":-0.1843446,\"FIELD4\":85.59853}",
      "{\"FIELD1\":43.07201,\"FIELD2\":161.097,\"FIELD3\":0.2109266,\"FIELD4\":160.4935}",
      "{\"FIELD1\":41.54399,\"FIELD2\":203.629,\"FIELD3\":-0.1425254,\"FIELD4\":134.2193}",
      "{\"FIELD1\":75.16803,\"FIELD2\":200.829,\"FIELD3\":0.1281825,\"FIELD4\":117.3907}",
      "{\"FIELD1\":19.9903,\"FIELD2\":41.2381,\"FIELD3\":-0.2490638,\"FIELD4\":89.86404}",
      "{\"FIELD1\":51.9014,\"FIELD2\":129.9364,\"FIELD3\":0.2168488,\"FIELD4\":174.2238}",
      "{\"FIELD1\":19.809,\"FIELD2\":193.711,\"FIELD3\":0.1284131,\"FIELD4\":166.1076}",
      "{\"FIELD1\":30.532,\"FIELD2\":189.437,\"FIELD3\":-0.1540674,\"FIELD4\":159.7849}",
      "{\"FIELD1\":28.8526,\"FIELD2\":97.7803,\"FIELD3\":0.09721822,\"FIELD4\":128.0064}"
  };

  @Test
  public void testGetProcessingType() {

    ErrorModel model = new ErrorModel();

    Assert.assertEquals(TaskType.ERROR, model.getType());

  }

  @Test
  public void testGetName() {
    ErrorModel model = new ErrorModel();
    Assert.assertEquals("error", model.getName());
  }

  @Test
  public void testSetParam() {
    ErrorModel model = new ErrorModel();

    TaskModelParam params = new TaskModelParam();
    params.put("type", "mse");
    params.put("observation", "/records/FIELD1");
    params.put("windowSize", 3);
    params.put("windowOption", "TIME");

    model.setParam(params);
  }

  @Test
  public void testMSE() {
    List<String> target = new ArrayList<>();
    for (int index = 2; index <= 3; index++) {
      //target.add("/FIELD"+index);
      target.add("/records/FIELD" + index);
    }

    List<String> output = new ArrayList<>();
    output.add("/Out1");
    output.add("/Out2");
    TaskModelParam params = new TaskModelParam();
    params.put("type", "mse");
    params.put("observation", "/records/FIELD1");
    params.put("windowSize", 3);
    params.put("windowOption", "TIME");

    ErrorModel model = new ErrorModel();
    model.setParam(params);

    for (int i = 0; i < records.length; i++) {

      DataSet dataSet = DataSet.create();
      dataSet.addRecord(records[i]);
      dataSet = model.calculate(dataSet, target, output);

      if (dataSet == null) {
        System.out.println("ignored input " + i);
      } else {
        //System.out.println("Prediction : " + cls);
        System.out.println(dataSet.toString());
      }
    }
  }

  @Test
  public void testRMSE() {
    List<String> target = new ArrayList<>();
    for (int index = 2; index <= 3; index++) {
      //target.add("/FIELD"+index);
      target.add("/records/FIELD" + index);
    }

    List<String> output = new ArrayList<>();
    output.add("/Out1");
    output.add("/Out2");
    TaskModelParam params = new TaskModelParam();
    params.put("type", "rmse");
    params.put("observation", "/records/FIELD1");
    params.put("windowSize", 3);
    params.put("windowOption", "TIME");

    ErrorModel model = new ErrorModel();
    model.setParam(params);

    for (int i = 0; i < records.length; i++) {

      DataSet dataSet = DataSet.create();
      dataSet.addRecord(records[i]);
      dataSet = model.calculate(dataSet, target, output);

      if (dataSet == null) {
        System.out.println("ignored input " + i);
      } else {
        //System.out.println("Prediction : " + cls);
        System.out.println(dataSet.toString());
      }
    }
  }
  @Test
  public void testMAE() {
    List<String> target = new ArrayList<>();
    for (int index = 2; index <= 3; index++) {
      //target.add("/FIELD"+index);
      target.add("/records/FIELD" + index);
    }

    List<String> output = new ArrayList<>();
    output.add("/Out1");
    output.add("/Out2");
    TaskModelParam params = new TaskModelParam();
    params.put("type", "mae");
    params.put("observation", "/records/FIELD1");
    params.put("windowSize", 3);
    params.put("windowOption", "TIME");

    ErrorModel model = new ErrorModel();
    model.setParam(params);

    for (int i = 0; i < records.length; i++) {

      DataSet dataSet = DataSet.create();
      dataSet.addRecord(records[i]);
      dataSet = model.calculate(dataSet, target, output);

      if (dataSet == null) {
        System.out.println("ignored input " + i);
      } else {
        //System.out.println("Prediction : " + cls);
        System.out.println(dataSet.toString());
      }
    }
  }
  @Test
  public void testME() {
    List<String> target = new ArrayList<>();
    for (int index = 2; index <= 3; index++) {
      //target.add("/FIELD"+index);
      target.add("/records/FIELD" + index);
    }

    List<String> output = new ArrayList<>();
    output.add("/Out1");
    output.add("/Out2");
    TaskModelParam params = new TaskModelParam();
    params.put("type", "me");
    params.put("observation", "/records/FIELD1");
    params.put("windowSize", 3);
    params.put("windowOption", "TIME");

    ErrorModel model = new ErrorModel();
    model.setParam(params);

    for (int i = 0; i < records.length; i++) {

      DataSet dataSet = DataSet.create();
      dataSet.addRecord(records[i]);
      dataSet = model.calculate(dataSet, target, output);

      if (dataSet == null) {
        System.out.println("ignored input " + i);
      } else {
        //System.out.println("Prediction : " + cls);
        System.out.println(dataSet.toString());
      }
    }
  }

  @Test
  public void testME2() {
    List<String> target = new ArrayList<>();
    for (int index = 2; index <= 3; index++) {
      //target.add("/FIELD"+index);
      target.add("/FIELD" + index);
    }

    List<String> output = new ArrayList<>();
    output.add("/Out1");
    TaskModelParam params = new TaskModelParam();
    params.put("type", "me");
    params.put("observation", "/FIELD1");
    params.put("windowSize", 3);
    params.put("windowOption", "TIME");

    ErrorModel model = new ErrorModel();
    model.setParam(params);

    for (int i = 0; i < records.length; i++) {

      DataSet dataSet = DataSet.create();
      dataSet.put("/FIELD2", (0.5*i));
      dataSet.put("/FIELD2", (1.5*i));
      dataSet.put("/FIELD3", (2.5*i));

      dataSet = model.calculate(dataSet, target, output);

      if (dataSet == null) {
        System.out.println("ignored input " + i);
      } else {
        //System.out.println("Prediction : " + cls);
        System.out.println(dataSet.toString());
      }
    }
  }

  @Test
  public void testMSEAbnormal() {
    List<String> target = new ArrayList<>();
    for (int index = 2; index <= 3; index++) {
      //target.add("/FIELD"+index);
      target.add("/FIELD" + index);
    }

    List<String> output = new ArrayList<>();
    output.add("/Out1");
    TaskModelParam params = new TaskModelParam();
    params.put("type", "mse");
    params.put("observation", "/FIELD1");
    params.put("windowSize", 3);
    params.put("windowOption", "TIME");

    ErrorModel model = new ErrorModel();
    model.setParam(params);

    for (int i = 0; i < records.length; i++) {

      DataSet dataSet = DataSet.create();
      dataSet.put("/FIELD2", (0.5*i));
      dataSet.put("/FIELD2", (1.5*i));
      dataSet.put("/FIELD3", (2.5*i));

      dataSet = model.calculate(dataSet, target, output);

      if (dataSet == null) {
        System.out.println("ignored input " + i);
      } else {
        //System.out.println("Prediction : " + cls);
        System.out.println(dataSet.toString());
      }
    }
  }

  @Test
  public void testRMSEAbnormal() {
    List<String> target = new ArrayList<>();
    for (int index = 2; index <= 3; index++) {
      //target.add("/FIELD"+index);
      target.add("/records/FIELD" + index);
    }

    List<String> output = new ArrayList<>();
    output.add("/Out1");
    TaskModelParam params = new TaskModelParam();
    params.put("type", "rmse");
    params.put("observation", "/FIELD10");
    params.put("windowSize", 3);
    params.put("windowOption", "TIME");

    ErrorModel model = new ErrorModel();
    model.setParam(params);

    for (int i = 0; i < records.length; i++) {

      DataSet dataSet = DataSet.create();
      dataSet.put("/FIELD2", (0.5*i));
      dataSet.put("/FIELD2", (1.5*i));
      dataSet.put("/FIELD3", (2.5*i));

      dataSet = model.calculate(dataSet, target, output);

      if (dataSet == null) {
        System.out.println("ignored input " + i);
      } else {
        //System.out.println("Prediction : " + cls);
        System.out.println(dataSet.toString());
      }
    }
  }

  @Test
  public void testMEAbnormal() {
    List<String> target = new ArrayList<>();
    for (int index = 2; index <= 3; index++) {
      //target.add("/FIELD"+index);
      target.add("/records/FIELD" + (index+10));
    }

    List<String> output = new ArrayList<>();
    output.add("/Out1");
    TaskModelParam params = new TaskModelParam();
    params.put("type", "me");
    params.put("observation", "/FIELD10");
    params.put("windowSize", 3);
    params.put("windowOption", "TIME");

    ErrorModel model = new ErrorModel();
    model.setParam(params);

    for (int i = 0; i < records.length; i++) {

      DataSet dataSet = DataSet.create();
      dataSet.put("/FIELD2", (0.5*i));
      dataSet.put("/FIELD2", (1.5*i));
      dataSet.put("/FIELD3", (2.5*i));

      dataSet = model.calculate(dataSet, target, output);

      if (dataSet == null) {
        System.out.println("ignored input " + i);
      } else {
        //System.out.println("Prediction : " + cls);
        System.out.println(dataSet.toString());
      }
    }
  }
}
