/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.camunda.bpm.engine.test.bpmn.usertask;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.test.fluent.FluentProcessEngineTestCase;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;

import static org.camunda.bpm.engine.test.fluent.FluentProcessEngineTests.assertThat;
import static org.camunda.bpm.engine.test.fluent.FluentProcessEngineTests.newProcessInstance;
import static org.camunda.bpm.engine.test.fluent.FluentProcessEngineTests.*;

/**
 * @author Thilo-Alexander Ginkel
 */
public class TaskPriorityExtensionsTest extends FluentProcessEngineTestCase {

  @Deployment
  public void testPriorityExtension() throws Exception {
    testPriorityExtension(25);
    testPriorityExtension(75);
  }

  private void testPriorityExtension(int priority) throws Exception {
    final Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("taskPriority", priority);

    // Start process-instance, passing priority that should be used as task priority
    final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("taskPriorityExtension", variables);

    final Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();

    assertEquals(priority, task.getPriority());
  }

  @Deployment
  public void testPriorityExtensionString() throws Exception {
     newProcessInstance("taskPriorityExtensionString").start();
     assertThat(processInstance().task().getPriority()).isEqualTo(42);
  }
}
