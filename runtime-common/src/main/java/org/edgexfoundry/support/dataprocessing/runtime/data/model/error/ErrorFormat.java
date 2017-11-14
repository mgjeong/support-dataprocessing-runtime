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
package org.edgexfoundry.processing.runtime.data.model.error;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.edgexfoundry.processing.runtime.data.model.Format;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Result", description = "Result")
public class ErrorFormat extends Format {
    @ApiModelProperty(required = true)
    private ErrorType errorCode;
    @ApiModelProperty(required = true)
    private String errorMessage;

    public ErrorFormat() {
        this(ErrorType.DPFW_ERROR_NONE, "Success.");
    }

    public ErrorFormat(ErrorType errorCode) {
        this(errorCode, "Success.");
    }

    public ErrorFormat(ErrorType errorCode, String errorMessage) {
        setErrorCode(errorCode);
        setErrorMessage(errorMessage);
    }

    public ErrorType getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorType errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @JsonIgnore
    public Boolean isError() {
        return (this.errorCode != ErrorType.DPFW_ERROR_NONE);
    }

    @JsonIgnore
    public Boolean isNoError() {
        return (this.errorCode == ErrorType.DPFW_ERROR_NONE);
    }
}
