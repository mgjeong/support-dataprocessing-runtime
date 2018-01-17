/*******************************************************************************
 * Copyright 2017 Samsung Electronics All Rights Reserved.
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
package org.edgexfoundry.support.dataprocessing.runtime.task.model;

import org.edgexfoundry.support.dataprocessing.runtime.task.AbstractTaskModel;
import org.edgexfoundry.support.dataprocessing.runtime.task.DataSet;
import org.edgexfoundry.support.dataprocessing.runtime.task.TaskModelParam;
import org.edgexfoundry.support.dataprocessing.runtime.task.TaskType;
import org.edgexfoundry.support.dataprocessing.runtime.task.function.ErrorFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Computes Error
 * <p>
 * Flink requires this class to be serializable.
 */
public class ErrorModel extends AbstractTaskModel {
    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorModel.class);

    private String algorithmType = null;
    private String observation = null;
    private HashMap<String, LinkedList<Number>> targetList = null;
    private LinkedList<Number> observationList = null;
    private int mWindowSize = 2;

    public ErrorModel() {
        targetList = new HashMap<>();
        observationList = new LinkedList<>();
    }

    @Override
    public TaskType getType() {
        return TaskType.ERROR;
    }

    @Override
    public String getName() {
        return "error";
    }

    @Override
    public void setParam(TaskModelParam params) {

        if (params.containsKey("type")) {
            this.algorithmType = params.get("type").toString();
        }
        if (params.containsKey("observation")) {
            this.observation = params.get("observation").toString();
        }
        if (params.containsKey("interval")) {
            HashMap tInterval = (HashMap) params.get("interval");
            if (tInterval.containsKey("data")) {
                Integer dataSize = ((Number) tInterval.get("data")).intValue();
                if (dataSize != null) {
                    this.mWindowSize = (dataSize.intValue());
                }
            }
            if (tInterval.containsKey("time")) {
                Integer timeSize = ((Number) tInterval.get("time")).intValue();
                if (timeSize != null) {
                    this.mWindowSize = (timeSize.intValue());
                }
            }
        }
    }

    @Override
    public TaskModelParam getDefaultParam() {
        TaskModelParam params = new TaskModelParam();
        params.put("type", new String("mse"));
        params.put("observation", new String("/x1"));
        return params;
    }

    private double getAverageError(Double[] targets, Double[] observe) {
        double error = 0.0;
        LOGGER.info("Target Size {} : Observe Size {}", targets.length, observe.length);
        if (targets.length == observe.length) {
            double[] pred = new double[targets.length];
            double[] obsv = new double[observe.length];

            for (int index = 0; index < targets.length; index++) {
                pred[index] = targets[index].doubleValue();
                obsv[index] = observe[index].doubleValue();
            }

            if (this.algorithmType.equals("me")) {
                error = ErrorFunction.calculate(pred, obsv, ErrorFunction.MEASURE.ME);
            } else if (this.algorithmType.equals("mae")) {
                error = ErrorFunction.calculate(pred, obsv, ErrorFunction.MEASURE.MAE);
            } else if (this.algorithmType.equals("rmse")) {
                error = ErrorFunction.calculate(pred, obsv, ErrorFunction.MEASURE.RMSE);
            } else if (this.algorithmType.equals("rmse")) {
                error = ErrorFunction.calculate(pred, obsv, ErrorFunction.MEASURE.RMSE);
            } else {
                LOGGER.error("Not Supporting Type : {}", this.algorithmType);
            }

        } else {
            LOGGER.error("Length Not Match - Target {}, Observe {}", targets.length, observe.length);
        }
        return error;
    }

    @Override
    public DataSet calculate(DataSet in, List<String> inRecordKeys, List<String> outRecordKeys) {
        LOGGER.info("[Error] Entering calculation");

        LinkedList<Number> value = null;
        Double observe = in.getValue(this.observation, Double.class);
        if (observe != null) {
            if (observationList.size() >= this.mWindowSize) {
                observationList.removeFirst();
            }
            observationList.addLast(observe.doubleValue());

            for (int index = 0; index < inRecordKeys.size(); index++) {
                Number predict = in.getValue(inRecordKeys.get(index), Number.class);

                Object temp = targetList.get(inRecordKeys.get(index));
                if (temp == null) {
                    value = new LinkedList<>();
                    LOGGER.info("Instantiate new Linked List for : {}", inRecordKeys.get(index));
                } else if (temp instanceof LinkedList) {
                    value = (LinkedList<Number>) temp;
                } else {
                    value = null;
                }

                if (value != null) {
                    if (value.size() >= this.mWindowSize) {
                        value.removeFirst();
                    }
                    value.addLast(predict);
                    targetList.put(inRecordKeys.get(index), value);

                    Double[] tArr = value.toArray(new Double[0]);
                    Double[] tObs = observationList.toArray(new Double[0]);

                    in.setValue(outRecordKeys.get(index), getAverageError(tArr, tObs));
                }
            }
        } else {
            LOGGER.error("Failed to extract Observation value {} ", this.observation);
        }

        LOGGER.info("[Error] Returning calculation result");
        return in;
    }
}
