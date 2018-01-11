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
package org.edgexfoundry.support.dataprocessing.runtime.db;

import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//    [Reference Query]
//    INSERT INTO TABLE_NAME [(column1, column2, column3,...columnN)] VALUES (value1, value2, value3,...valueN);
//    SELECT column1, column2, columnN FROM table_name WHERE [condition];;
//    UPDATE table_name SET column1 = value1, column2 = value2...., columnN = valueN WHERE [condition];
//    DELETE FROM table_name WHERE [condition];

@Deprecated
public final class JobTableManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(JobTableManager.class);
  private static JobTableManager instance = null;
  private static final String JOBTABLENAME = "job";
  private static SqliteConnector dbCon = null;

  private JobTableManager() {
    dbCon = SqliteConnector.getInstance();
  }

  public void close() {
    dbCon.close();
    dbCon = null;
  }

  public static synchronized JobTableManager getInstance() throws Exception {
    if (instance == null) {
      instance = new JobTableManager();
      instance.createJobTable();
    }
    return instance;
  }


  public enum Entry {
    engineType,
    id,
    groupId,
    state,
    input,
    output,
    taskinfo,
    engineid,
    targetHost,
    runtimeHost
  }


  //create "job" table
  private void createJobTable() throws Exception {
    /*
    SqliteConnector.TableDesc td = new SqliteConnector.TableDesc(JOBTABLENAME);

    td.addColum(Entry.engineType.name(), new SqliteConnector.ColumnDesc("TEXT", "NOT NULL"));
    td.addColum(Entry.id.name(), new SqliteConnector.ColumnDesc("TEXT", "PRIMARY KEY NOT NULL"));
    td.addColum(Entry.groupId.name(), new SqliteConnector.ColumnDesc("TEXT", "NOT NULL"));
    td.addColum(Entry.state.name(), new SqliteConnector.ColumnDesc("TEXT", "NOT NULL"));
    td.addColum(Entry.input.name(), new SqliteConnector.ColumnDesc("TEXT", "NOT NULL"));
    td.addColum(Entry.output.name(), new SqliteConnector.ColumnDesc("TEXT", "NOT NULL"));
    td.addColum(Entry.taskinfo.name(), new SqliteConnector.ColumnDesc("TEXT", "NOT NULL"));
    td.addColum(Entry.engineid.name(), new SqliteConnector.ColumnDesc("TEXT", "NOT NULL"));
    td.addColum(Entry.targetHost.name(), new SqliteConnector.ColumnDesc("TEXT", "NOT NULL"));
    td.addColum(Entry.runtimeHost.name(), new SqliteConnector.ColumnDesc("TEXT", ""));
    dbCon.createTable(td);
    */
  }


  //insert a job to the table "job"
  public void insertJob(String engineType, String jobId, String gId, String state, String input,
      String output, String taskinfo, String engineId, String targetHost,
      String runtimeHost)
      throws SQLException, InvalidParameterException {
    /*

    if (SqliteConnector.Util.validateParams(jobId, gId, state, input, output, taskinfo, engineId)) {
      throw new InvalidParameterException();
    }

    String query = String
        .format(" INSERT OR REPLACE INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s ) "
                + " VALUES ( '%s', '%s','%s','%s','%s','%s','%s','%s', '%s', '%s');",
            JOBTABLENAME,
            Entry.engineType.name(),
            Entry.id.name(),
            Entry.groupId.name(),
            Entry.state.name(),
            Entry.input.name(),
            Entry.output.name(),
            Entry.taskinfo.name(),
            Entry.engineid.name(),
            Entry.targetHost.name(),
            Entry.runtimeHost.name(),
            engineType,
            jobId, gId, state, input, output, taskinfo, engineId,
            targetHost,
            runtimeHost);
    LOGGER.info(query);
    dbCon.executeUpdate(query);
    */
  }

  //update a job to the table "job"
  public void updateJob(String jobId, String gId, String state, String input,
      String output, String taskInfo, String engineId)
      throws SQLException, InvalidParameterException {
    /*
    if (SqliteConnector.Util.validateParams(jobId, gId, state, input, output, engineId)) {
      throw new InvalidParameterException();
    }

    String query = String.format(" UPDATE %s SET %s = '%s', %s = '%s', %s = '%s',"
            + " %s = '%s', %s = '%s', %s = '%s' WHERE %s = '%s';",
        JOBTABLENAME,
        Entry.groupId.name(), gId,
        Entry.state.name(), state,
        Entry.input.name(), input,
        Entry.output.name(), output,
        Entry.taskinfo.name(), taskInfo,
        Entry.engineid.name(), engineId,
        Entry.id.name(), jobId);
    LOGGER.info(query);
    dbCon.executeUpdate(query);
    */
  }

  //Update state column by id
  public void updateState(String jobId, String state)
      throws SQLException, InvalidParameterException {
    /*
    if (SqliteConnector.Util.validateParams(jobId, state)) {
      throw new InvalidParameterException();
    }

    String query = String.format(" UPDATE %s SET %s = '%s' WHERE %s = '%s';",
        JOBTABLENAME, Entry.state.name(), state, Entry.id.name(), jobId);
    LOGGER.info(query);
    dbCon.executeUpdate(query);
    */
  }


  //update Payload( input, output, taskinfo ) column by ID
  public void updatePayload(String jobId, String input, String output, String taskinfo)
      throws SQLException, InvalidParameterException {
    /*
    if (SqliteConnector.Util.validateParams(jobId, input, output, taskinfo)) {
      throw new InvalidParameterException();
    }

    String query = String.format(" UPDATE %s SET %s = '%s', %s = '%s', %s = '%s' WHERE %s = '%s';",
        JOBTABLENAME, Entry.input.name(), input, Entry.output.name(), output,
        Entry.taskinfo.name(), taskinfo, Entry.id.name(), jobId);
    LOGGER.info(query);
    dbCon.executeUpdate(query);
    */
  }

  //update engineid column by ID
  public void updateEngineId(String jobId, String engineId) throws SQLException {
    /*
    if (SqliteConnector.Util.validateParams(jobId/*, engineId*)) {
      throw new InvalidParameterException();
    }

    String query = String.format(" UPDATE %s SET %s = '%s' WHERE %s = '%s';",
        JOBTABLENAME, Entry.engineid.name(), engineId, Entry.id.name(), jobId);
    LOGGER.info(query);
    dbCon.executeUpdate(query);
    */
  }

  //update groupId column by id
  public void updateGroupId(String jobId, String gId) throws SQLException {
    /*
    if (SqliteConnector.Util.validateParams(jobId, gId)) {
      throw new InvalidParameterException();
    }

    String query = String.format(" UPDATE %s SET %s = '%s' WHERE %s = '%s';",
        JOBTABLENAME, Entry.groupId.name(), gId, Entry.id.name(), jobId);
    LOGGER.info(query);
    dbCon.executeUpdate(query);
    */
  }

  //Select state column value by id
  public List<Map<String, String>> getStateById(String jobId) throws SQLException {
    throw new UnsupportedOperationException();
    /*
    if (SqliteConnector.Util.validateParams(jobId)) {
      throw new InvalidParameterException();
    }

    String query = String.format(" SELECT %s FROM %s WHERE %s = '%s';",
        Entry.state.name(), JOBTABLENAME, Entry.id.name(), jobId);
    LOGGER.info(query);
    return dbCon.executeSelect(query);
    */
  }

  //select Payload( input, output, taskinfo ) column value by ID
  public List<Map<String, String>> getPayloadById(String jobId) throws SQLException {
    /*
    if (SqliteConnector.Util.validateParams(jobId)) {
      throw new InvalidParameterException();
    }

    String query = String.format(" SELECT %s, %s, %s, %s, %s FROM %s WHERE %s = '%s';",
        Entry.runtimeHost.name(), Entry.engineType.name(), Entry.input.name(),
        Entry.output.name(), Entry.taskinfo.name(), JOBTABLENAME, Entry.id.name(),
        jobId);
    LOGGER.info(query);
    return dbCon.executeSelect(query);
    */
    throw new UnsupportedOperationException();
  }

  //Select engineid column value by id
  public List<Map<String, String>> getEngineIdById(String jobId) throws SQLException {
    /*
    if (SqliteConnector.Util.validateParams(jobId)) {
      throw new InvalidParameterException();
    }

    String query = String.format(" SELECT %s FROM %s WHERE %s = '%s';",
        Entry.engineid.name(), JOBTABLENAME, Entry.id.name(), jobId);
    LOGGER.info(query);
    return dbCon.executeSelect(query);
    */
    throw new UnsupportedOperationException();
  }

  //Select groupId column value by id
  public List<Map<String, String>> getGroupIdById(String jobId) throws SQLException {
  /*
    if (SqliteConnector.Util.validateParams(jobId)) {
      throw new InvalidParameterException();
    }

    String query = String.format(" SELECT %s FROM %s WHERE %s = '%s';",
        Entry.groupId.name(), JOBTABLENAME, Entry.id.name(), jobId);
    LOGGER.info(query);
    return dbCon.executeSelect(query);
    */
    throw new UnsupportedOperationException();
  }

  //select all columns by id
  public List<Map<String, String>> getRowById(String jobId) throws SQLException {
/*
    if (SqliteConnector.Util.validateParams(jobId)) {
      throw new InvalidParameterException();
    }

    String query = String.format(" SELECT * FROM %s WHERE %s = '%s';",
        JOBTABLENAME, Entry.groupId.name(), jobId);
    LOGGER.info(query);
    return dbCon.executeSelect(query);
    */
    throw new UnsupportedOperationException();
  }

  //select id, state of all jobs
  public List<Map<String, String>> getAllJobs() throws SQLException {
    /*
    String query = String.format(" SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s FROM %s;",
        Entry.runtimeHost.name(),
        Entry.targetHost.name(), Entry.engineType.name(),
        Entry.id.name(), Entry.groupId.name(), Entry.state.name(), Entry.input.name(),
        Entry.output.name(), Entry.taskinfo.name(), JOBTABLENAME);
    LOGGER.info(query);
    return dbCon.executeSelect(query);
    */
    throw new UnsupportedOperationException();
  }


  //delete a job to the table "Job"
  public void deleteJobById(String jobId) throws SQLException {
/*
    if (SqliteConnector.Util.validateParams(jobId)) {
      throw new InvalidParameterException();
    }

    String query = String.format(" DELETE FROM %s WHERE %s = '%s';",
        JOBTABLENAME, Entry.groupId.name(), jobId);
    LOGGER.info(query);
    dbCon.executeUpdate(query);
    */
  }


  //delete all from table "Job"
  public void deleteAllJob() throws SQLException {
    /*
    String query = String.format(" DELETE FROM %s;", JOBTABLENAME);
    LOGGER.info(query);
    dbCon.executeUpdate(query);
    */
  }

  public void deleteAllJob(String engineType) throws SQLException {
    /*
    if (null != engineType) {
      String query = String
          .format(" DELETE FROM %s WHERE %s = %s;", JOBTABLENAME, Entry.engineType, engineType);
      LOGGER.info(query);
      dbCon.executeUpdate(query);
    } else {
      deleteAllJob();
    }
    */
  }
}
