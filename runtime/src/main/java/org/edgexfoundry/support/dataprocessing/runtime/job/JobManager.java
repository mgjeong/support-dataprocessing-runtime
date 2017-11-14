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
package org.edgexfoundry.processing.runtime.job;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.edgexfoundry.processing.runtime.data.model.error.ErrorFormat;
import org.edgexfoundry.processing.runtime.data.model.error.ErrorType;
import org.edgexfoundry.processing.runtime.data.model.job.DataFormat;
import org.edgexfoundry.processing.runtime.data.model.job.JobGroupFormat;
import org.edgexfoundry.processing.runtime.data.model.job.JobInfoFormat;
import org.edgexfoundry.processing.runtime.data.model.job.JobState;
import org.edgexfoundry.processing.runtime.data.model.response.JobGroupResponseFormat;
import org.edgexfoundry.processing.runtime.data.model.response.JobResponseFormat;
import org.edgexfoundry.processing.runtime.data.model.task.TaskFormat;
import org.edgexfoundry.processing.runtime.db.JobTableManager;
import org.edgexfoundry.processing.runtime.engine.Engine;
import org.edgexfoundry.processing.runtime.engine.EngineFactory;
import org.edgexfoundry.processing.runtime.engine.EngineType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public final class JobManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobManager.class);

    private Boolean initState = FALSE;
    private static Engine framework = null;
    private static JobTableManager jobTable = null;
    private static JobManager instance = null;
    private static Map<String, List<JobInfoFormat>> myJobs = null;

    private JobManager() {

    }

    public static JobManager getInstance() {
        if (instance == null) {
            instance = new JobManager();
            try {
                jobTable = JobTableManager.getInstance();
                framework = EngineFactory.createEngine(EngineType.Flink);
                myJobs = new HashMap<String, List<JobInfoFormat>>();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public void initialize() {
        loadJobInfo();
        initJobList();
        initState = TRUE;
    }

    private void loadJobInfo() {
        List<Map<String, String>> jobList = null;
        try {
            jobList = jobTable.getAllJobs();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        for (Map<String, String> job : jobList) {
            String jobId = null;
            String groupId = null;
            JobState state = null;
            List<DataFormat> input = null;
            List<DataFormat> output = null;
            List<TaskFormat> task = null;

            ObjectMapper mapper = new ObjectMapper();
            try {
                jobId = job.get(JobTableManager.Entry.jid.name());
                groupId = job.get(JobTableManager.Entry.gid.name());
                state = JobState.getState(job.get(JobTableManager.Entry.state.name()));
                input = mapper.readValue(job.get(JobTableManager.Entry.input.name()),
                        new TypeReference<List<DataFormat>>() {
                        });
                output = mapper.readValue(job.get(JobTableManager.Entry.output.name()),
                        new TypeReference<List<DataFormat>>() {
                        });
                task = mapper.readValue(job.get(JobTableManager.Entry.taskinfo.name()),
                        new TypeReference<List<TaskFormat>>() {
                        });
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }

            if (jobId == null || groupId == null || state == null
                    || input == null || output == null || task == null) {
                LOGGER.debug("invalid input");
                continue;
            }

            JobInfoFormat jobInfo = new JobInfoFormat(input, output, task, state);
            addJobToGroupWithJobId(groupId, jobId, jobInfo);
         }
    }

    private void initJobList() {
        Iterator<String> keys = myJobs.keySet().iterator();
        while (keys.hasNext()) {
            String groupId = keys.next();
            framework.createJob(groupId);
            List<JobInfoFormat> jobList = getJobList(groupId);

            for (JobInfoFormat job : jobList) {
                if (job.getState() == JobState.RUNNING) {
                    framework.createJob(job.getJobId());
                    framework.run(job.getJobId());
                }
            }
        }
    }

    private JobResponseFormat createGroupJob(String groupId, JobGroupFormat request) {
        if (request.getJobs() == null || request.getJobs().isEmpty()) {
            return new JobResponseFormat(
                    new ErrorFormat(ErrorType.DPFW_ERROR_INVALID_PARAMS, "no job info"));
        }

        for (JobInfoFormat jobNode : request.getJobs()) {
            if (jobNode.getInput() == null || jobNode.getInput().isEmpty()) {
                return new JobResponseFormat(
                        new ErrorFormat(ErrorType.DPFW_ERROR_INVALID_PARAMS, "no input value"));
            }
            if (jobNode.getOutput() == null || jobNode.getOutput().isEmpty()) {
                return new JobResponseFormat(
                        new ErrorFormat(ErrorType.DPFW_ERROR_INVALID_PARAMS, "no output value"));
            }
            if (jobNode.getTask() == null || jobNode.getTask().isEmpty()) {
                return new JobResponseFormat(
                        new ErrorFormat(ErrorType.DPFW_ERROR_INVALID_PARAMS, "no task value"));
            }

            JobResponseFormat newJobResponse = framework.createJob();
            if (newJobResponse.getError().isError()) {
                newJobResponse.getError().setErrorMessage("Fail to Create Job.");
                return newJobResponse;
            }

            ErrorFormat result = addJobToGroupWithJobId(groupId, newJobResponse.getJobId(), jobNode);
            if (result.isError()) {
                deleteJob(groupId);
                return new JobResponseFormat(result);
            }
        }
        return new JobResponseFormat(groupId);
    }

    public JobResponseFormat createGroupJob(JobGroupFormat request) {
        JobResponseFormat groupResponse = framework.createJob();
        if (groupResponse.getError().isError()) {
            groupResponse.getError().setErrorMessage("Fail to Create Job.");
            groupResponse.getError().setErrorCode(ErrorType.DPFW_ERROR_FULL_JOB);
            return groupResponse;
        }

        String groupId = groupResponse.getJobId();

        JobResponseFormat response = createGroupJob(groupId, request);
        if (response.getError().isError()) {
            framework.delete(groupId);
        }

        return response;
    }

    public JobGroupResponseFormat getAllJobs() {
        JobGroupResponseFormat response = new JobGroupResponseFormat();

        Iterator<String> keys = myJobs.keySet().iterator();
        while (keys.hasNext()) {
            String jobId = keys.next();
            List<JobInfoFormat> jobList = getJobList(jobId);

            JobGroupFormat jobGroup = new JobGroupFormat();
            jobGroup.setGroupId(jobId);

            for (JobInfoFormat job : jobList) {
                jobGroup.addJob(job);
            }
            response.addJobGroup(jobGroup);
        }

        response.getError().setErrorMessage("Created job instance : " + response.getJobGroups().size());
        return response;
    }

    public JobGroupResponseFormat getJob(String groupId) {
        JobGroupResponseFormat response = new JobGroupResponseFormat();
        JobGroupFormat jobGroup = new JobGroupFormat();
        jobGroup.setGroupId(groupId);
        List<JobInfoFormat> jobList = getJobList(groupId);
        if (jobList != null) {
            for (JobInfoFormat job : jobList) {
                jobGroup.addJob(job);
            }
            response.addJobGroup(jobGroup);
        }

        if (response.getJobGroups().size() == 0) {
            response.setError(new ErrorFormat(ErrorType.DPFW_ERROR_INVALID_PARAMS,
                    "There is no job corresponding to : " + groupId));
        }
        return response;
    }

    public JobResponseFormat updateJob(String groupId, JobGroupFormat request) {
        JobResponseFormat response = new JobResponseFormat();
        List<JobInfoFormat> jobList = getJobList(groupId);

        response.setJobId(groupId);
        if (jobList == null || jobList.isEmpty()) {
            response.setError(new ErrorFormat(ErrorType.DPFW_ERROR_INVALID_PARAMS,
                    "There is no job corresponding to : " + groupId));
            return response;
        }

        // TODO - need to discussion
        deleteJob(groupId);
        return createGroupJob(groupId, request);
    }

    public JobResponseFormat executeJob(String groupId) {
        JobResponseFormat frameworkResponse;
        JobResponseFormat response = new JobResponseFormat();
        List<JobInfoFormat> jobList = getJobList(groupId);

        response.setJobId(groupId);
        if (jobList == null || jobList.isEmpty()) {
            response.setError(new ErrorFormat(ErrorType.DPFW_ERROR_INVALID_PARAMS,
                    "There is no job corresponding to : " + groupId));
            return response;
        }

        for (JobInfoFormat job : jobList) {
            if (job.getState() == JobState.RUNNING) {
                continue;
            }

            frameworkResponse = framework.run(job.getJobId());
            if (frameworkResponse.getError().isError()) {
                response.setError(new ErrorFormat(ErrorType.DPFW_ERROR_ENGINE_FLINK,
                        "Fail to execute job : " + groupId));
                return response;
            }
            updateJobState(job, JobState.RUNNING);
            response.setError(frameworkResponse.getError());
        }

        return response;
    }

    public JobResponseFormat stopJob(String groupId) {
        JobResponseFormat frameworkResponse;
        JobResponseFormat response = new JobResponseFormat();
        List<JobInfoFormat> jobList = getJobList(groupId);

        response.setJobId(groupId);
        if (jobList == null || jobList.isEmpty()) {
            response.setError(new ErrorFormat(ErrorType.DPFW_ERROR_INVALID_PARAMS,
                    "There is no job corresponding to : " + groupId));
            return response;
        }

        for (JobInfoFormat job : jobList) {
            if (job.getState() == JobState.STOPPED) {
                continue;
            }

            frameworkResponse = framework.stop(job.getJobId());
            if (frameworkResponse.getError().isError()) {
                response.setError(new ErrorFormat(ErrorType.DPFW_ERROR_ENGINE_FLINK,
                        "Fail to stop job : " + groupId));
                return response;
            }
            updateJobState(job, JobState.STOPPED);
            response.setError(frameworkResponse.getError());
        }
        return response;
    }

    public JobResponseFormat deleteJob(String groupId) {
        JobResponseFormat response = new JobResponseFormat();
        List<JobInfoFormat> jobList = getJobList(groupId);

        response.setJobId(groupId);
        if (jobList == null || jobList.isEmpty()) {
            response.setError(new ErrorFormat(ErrorType.DPFW_ERROR_INVALID_PARAMS,
                    "There is no job corresponding to : " + groupId));
            return response;
        }

        for (JobInfoFormat job : jobList) {
            if (job.getState() == JobState.RUNNING) {
                framework.stop(job.getJobId());
            }
            framework.delete(job.getJobId());
        }

        removeJob(groupId);
        try {
            jobTable.deleteJobById(groupId);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        framework.delete(groupId);
        return response;
    }

    public ErrorFormat deleteAllJob() {
        Iterator<String> keys = myJobs.keySet().iterator();
        while (keys.hasNext()) {
            String groupId = keys.next();
            List<JobInfoFormat> jobList = getJobList(groupId);

            for (JobInfoFormat job : jobList) {
                if (job.getState() == JobState.RUNNING) {
                    framework.stop(job.getJobId());
                }
                framework.delete(job.getJobId());
            }
            framework.delete(groupId);
        }

        myJobs.clear();
        try {
            jobTable.deleteAllJob();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ErrorFormat(ErrorType.DPFW_ERROR_DB, e.getMessage());
        }
        return new ErrorFormat();
    }

    private void updateJobState(JobInfoFormat job, JobState state) {
        job.setState(state);
        try {
            jobTable.updateState(job.getJobId(), state.toString());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private ErrorFormat addJobToGroup(String groupId, JobInfoFormat request) {
        JobResponseFormat response = framework.createJob();
        String jobId = response.getJobId();
        return addJobToGroupWithJobId(groupId, jobId, request);
    }

    private ErrorFormat addJobToGroupWithJobId(String groupId, String jobId, JobInfoFormat request) {
        List<JobInfoFormat> jobList = getJobList(groupId);
        JobInfoFormat newJob = (JobInfoFormat) request.clone();
        newJob.setJobId(jobId);
        if (jobList == null) {
            jobList = new ArrayList<JobInfoFormat>();
        }
        jobList.add(newJob);

        if (request != null && initState == TRUE) {
            try {
                jobTable.insertJob(jobId, groupId,
                        request.getState().toString(),
                        request.getInput().toString(),
                        request.getOutput().toString(),
                        request.getTask().toString(),
                        "");
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                return new ErrorFormat(ErrorType.DPFW_ERROR_DB, e.getMessage());
            }
        }

        this.myJobs.put(groupId, jobList);
        return new ErrorFormat();
    }

    private void removeJob(String groupId) {
        this.myJobs.remove(groupId);
    }

    private List<JobInfoFormat> getJobList(String groupId) {
        return this.myJobs.get(groupId);
    }
}