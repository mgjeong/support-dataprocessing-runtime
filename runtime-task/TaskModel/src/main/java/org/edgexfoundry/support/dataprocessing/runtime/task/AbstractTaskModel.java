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

import java.util.List;
import org.edgexfoundry.support.dataprocessing.runtime.task.DataSet;
import org.edgexfoundry.support.dataprocessing.runtime.task.TaskModel;
import org.edgexfoundry.support.dataprocessing.runtime.task.TaskParam;
import org.edgexfoundry.support.dataprocessing.runtime.task.TaskParam.UiFieldType;

public abstract class AbstractTaskModel implements TaskModel {

  @TaskParam(key = "inrecord", uiName = "In records",
      uiType = UiFieldType.ARRAYSTRING, tooltip = "Enter in records")
  private List<String> inRecordKeys;
  @TaskParam(key = "outrecord", uiName = "Out records",
      uiType = UiFieldType.ARRAYSTRING, tooltip = "Enter out records")
  private List<String> outRecordKeys;

  /**
   * This default constructor (with no argument) is required for dynamic instantiation from
   * TaskFactory.
   */
  public AbstractTaskModel() {
  }

  @Override
  public void setInRecordKeys(List<String> inRecordKeys) {
    this.inRecordKeys = inRecordKeys;
  }

  @Override
  public void setOutRecordKeys(List<String> outRecordKeys) {
    this.outRecordKeys = outRecordKeys;
  }

  @Override
  public DataSet calculate(DataSet in) {
    return calculate(in, this.inRecordKeys, this.outRecordKeys);

  }

  public abstract DataSet calculate(DataSet in, List<String> inRecordKeys,
      List<String> outRecordKeys);
}
