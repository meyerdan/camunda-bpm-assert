/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.engine.impl.fluent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.camunda.bpm.engine.fluent.AbstractFluentDelegate;
import org.camunda.bpm.engine.fluent.FluentJob;
import org.camunda.bpm.engine.fluent.FluentProcessEngine;
import org.camunda.bpm.engine.fluent.FluentProcessInstance;
import org.camunda.bpm.engine.fluent.FluentProcessVariable;
import org.camunda.bpm.engine.fluent.FluentTask;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.runtime.JobQuery;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;
import static org.camunda.bpm.engine.fluent.support.ProcessVariableMaps.parseMap;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 * @author Rafael Cordones <rafael.cordones@plexiti.com>
 */
public class FluentProcessInstanceImpl extends AbstractFluentDelegate<ProcessInstance> implements FluentProcessInstance {

  private static Logger log = Logger.getLogger(FluentProcessInstanceImpl.class.getName());

  protected String processDefinitionKey;
  protected Map<String, Object> processVariables = new HashMap<String, Object>();
  protected FluentProcessInstance.Move move = new FluentProcessInstance.Move() {

    @Override
    public void along() {
    }
  };

  public FluentProcessInstanceImpl(final FluentProcessEngine engine) {
    super(engine, null);
  }

  public FluentProcessInstanceImpl(final FluentProcessEngine engine, final ProcessInstance delegate) {
    super(engine, delegate);
  }

  public FluentProcessInstanceImpl(final FluentProcessEngine engine, final String processDefinitionKey) {
    super(engine, null);
    this.processDefinitionKey = processDefinitionKey;
    final boolean processDefinitionKeyExists = !engine.getRepositoryService().createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).list()
        .isEmpty();
    if (!processDefinitionKeyExists)
      throw new IllegalArgumentException("Process Definition with processDefinitionKey '" + processDefinitionKey + "' is not deployed.");
  }

  @Override
  public String getProcessDefinitionId() {
    return delegate != null ? delegate.getProcessDefinitionId() : null;
  }

  @Override
  public String getBusinessKey() {
    return delegate.getBusinessKey();
  }

  @Override
  public boolean isSuspended() {
    return delegate.isSuspended();
  }

  @Override
  public boolean isActive() {
    return !isSuspended() && !isEnded();
  }

  @Override
  public String getId() {
    return delegate.getId();
  }

  @Override
  public boolean isEnded() {
    return delegate != null && engine.getRuntimeService().createExecutionQuery().processInstanceId(delegate.getId()).list().isEmpty() || delegate.isEnded();
  }

  @Override
  public String getProcessInstanceId() {
    return delegate.getProcessInstanceId();
  }

  @Override
  public ProcessInstance getDelegate() {
    if (delegate == null)
      throw new IllegalStateException("Process instance not yet started. You cannot access that method before starting the processInstance instance.");
    return delegate;
  }

  @Override
  public FluentTask task() {
    if (delegate != null) {
      final List<Task> tasks = taskQuery().processInstanceId(getProcessInstanceId()).list();
      if (tasks.size() > 1)
        throw new IllegalStateException(
            "By calling task() you implicitly assumed that maximum one user task is currently waiting to be completed in the context "
                + "of this process instance. But instead " + tasks.size() + " tasks are currently waiting to be completed.");
      if (tasks.size() == 1)
        return new FluentTaskImpl(engine, tasks.get(0));
    }
    return null;
  }

  private TaskQuery taskQuery() {
    return engine.getTaskService().createTaskQuery();
  }

  @Override
  public FluentJob job() {
    if (delegate != null) {
      final List<Job> jobs = jobQuery().processInstanceId(getProcessInstanceId()).list();
      if (jobs.size() > 1)
        throw new IllegalStateException("By calling job() you implicitly assumed that maximum one job is currently waiting to be executed in the context "
            + "of this process instance. But instead " + jobs.size() + " jobs are currently waiting to be executed.");
      if (jobs.size() == 1)
        return new FluentJobImpl(engine, jobs.get(0));
    }
    return null;
  }

  private JobQuery jobQuery() {
    return engine.getManagementService().createJobQuery();
  }

  public void moveAlong(final FluentProcessInstance.Move move) {
    this.move = move;
  }

  @Override
  public FluentProcessInstance startAndMove() {
    assertNotAlreadyStarted();
    Throwable expected = null;
    try {
      move.along();
    } catch (final Throwable t) {
      expected = t;
    }
    if (expected == null)
      throw new IllegalArgumentException("Process could not be moved to the given activityId.");
    return this;
  }

  @Override
  public FluentProcessInstance start() {
    assertNotAlreadyStarted();
    this.delegate = engine.getRuntimeService().startProcessInstanceByKey(processDefinitionKey, processVariables);
    log.info("Started processInstance (definition key '" + processDefinitionKey + "', definition id: '" + delegate.getProcessDefinitionId()
        + "', instance id: '" + delegate.getId() + "').");
    return this;
  }

  private void assertNotAlreadyStarted() {
    // strange behavior, have to check for null again in format()
    checkState(delegate == null, format("the process instance '%s' has already been started.", delegate != null ? delegate.getId() : null));
  }

  @Override
  public FluentProcessInstance setVariable(final String name, final Object value) {
    if (delegate == null) {
      this.processVariables.put(name, value);
    } else {
      engine.getRuntimeService().setVariable(getId(), name, value);
    }
    return this;
  }

  @Override
  public FluentProcessVariable getVariable(final String name) {
    final Object value = delegate != null ? engine.getRuntimeService().getVariable(getId(), name) : processVariables.get(name);
    if (value == null)
      throw new IllegalArgumentException("Unable to find processVariable '" + name + "'");
    return new FluentProcessVariableImpl(name, value);
  }

  @Override
  public FluentProcessInstance setVariables(final Object... variables) {
    return setVariables(parseMap(variables));
  }

  @Override
  public FluentProcessInstance setVariables(final Map<String, Object> variables) {
    for (final Entry<String, Object> variable : variables.entrySet()) {
      setVariable(variable.getKey(), variable.getValue());
    }
    return this;
  }

  @Override
  public String processDefinitionKey() {
    final ProcessDefinition processDefinition = getRepositoryService().createProcessDefinitionQuery().processDefinitionId(getProcessDefinitionId())
        .singleResult();
    checkState(processDefinition != null, "no processDefinition found for id=" + getProcessDefinitionId());

    return processDefinition.getKey();
  }

}
