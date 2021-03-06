package org.camunda.bpm.engine.test.fluent.assertions;

import java.util.Date;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.Job;
import org.fest.assertions.api.Assertions;

/**
 * Asserts for a {@link Job}
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class JobAssert extends AbstractProcessAssert<JobAssert, Job> {

  protected JobAssert(final ProcessEngine engine, final Job actual) {
    super(engine, actual, JobAssert.class);
  }

  public static JobAssert assertThat(final ProcessEngine engine, final Job actual) {
    return new JobAssert(engine, actual);
  }

  /**
   * Assert a specific id for the {@link Job}
   * @param expectedId the expected job id     
   * @return this {@link JobAssert}
   * @see org.camunda.bpm.engine.runtime.Job#getId()
   */
  public JobAssert hasId(final String expectedId) {
    isNotNull();
    Assertions.assertThat(expectedId).isNotEmpty();

    final String actualId = actual.getId();
    Assertions.assertThat(actualId)
      .overridingErrorMessage(
        "expecting %s to have id '%s', but found it to be '%s'", 
        toString(actual), expectedId, actualId
      )
      .isEqualTo(expectedId);
    return this;
  }

  /**
   * Assert a specific due date for the {@link Job}.
   * @param expectedDueDate the expected due date
   * @return this {@link JobAssert}  
   * @see org.camunda.bpm.engine.runtime.Job#getDuedate() 
   */
  public JobAssert hasDueDate(final Date expectedDueDate) {
    isNotNull();
    Assertions.assertThat(expectedDueDate).isNotNull();

    final Date actualDuedate = actual.getDuedate();
    Assertions.assertThat(actualDuedate)
      .overridingErrorMessage(
        "expecting %s to be due at '%s', but found it to be due at '%s'", 
        toString(actual), expectedDueDate, actualDuedate
      )
      .isEqualTo(expectedDueDate);
    return this;
  }

  /**
   * Assert a specific process instance id for the {@link Job}
   * @param expectedProcessInstanceId the expected process instance id     
   * @return this {@link JobAssert}
   * @see org.camunda.bpm.engine.runtime.Job#getProcessInstanceId()
   */
  public JobAssert hasProcessInstanceId(final String expectedProcessInstanceId) {
    isNotNull();
    Assertions.assertThat(expectedProcessInstanceId).isNotNull();

    final String actualProcessInstanceId = actual.getProcessInstanceId();
    Assertions.assertThat(actualProcessInstanceId)
      .overridingErrorMessage(
        "expecting %s to have process instance id '%s', but found it to be '%s'", 
        toString(actual), expectedProcessInstanceId, actualProcessInstanceId
      )
      .isEqualTo(expectedProcessInstanceId);
    return this;
  }

  /**
   * Assert a specific execution id for the {@link Job}
   * @param expectedExecutionId the expected execution id     
   * @return this {@link JobAssert}
   * @see org.camunda.bpm.engine.runtime.Job#getExecutionId()
   */
  public JobAssert hasExecutionId(final String expectedExecutionId) {
    isNotNull();
    Assertions.assertThat(expectedExecutionId).isNotNull();

    final String actualExecutionId = actual.getExecutionId();
    Assertions.assertThat(actualExecutionId)
      .overridingErrorMessage(
        "expecting %s to have execution id '%s', but found it to be '%s'",
        toString(actual), expectedExecutionId, actualExecutionId
      )
      .isEqualTo(expectedExecutionId);
    return this;
  }

  /**
   * Assert a specific number of retries left for the {@link Job}
   * @param expectedRetries the expected number of retries     
   * @return this {@link JobAssert}
   * @see org.camunda.bpm.engine.runtime.Job#getExecutionId()
   */
  public JobAssert hasRetries(final int expectedRetries) {
    isNotNull();

    final int actualRetries = actual.getRetries();
    Assertions.assertThat(actualRetries)
      .overridingErrorMessage(
        "expecting %s to have %s retries left, but found %s retries",
        toString(actual), expectedRetries, actualRetries
      )
      .isEqualTo(expectedRetries);
    return this;
  }

  /**
   * Assert the existence of an exception message for the {@link Job}
   * @return this {@link JobAssert}
   * @see org.camunda.bpm.engine.runtime.Job#getExceptionMessage() 
   */
  public JobAssert hasExceptionMessage() {
    isNotNull();
    final String actualExceptionMessage = actual.getExceptionMessage();
    Assertions.assertThat(actualExceptionMessage)
      .overridingErrorMessage(
        "expecting %s to have an exception message, but found it to be null or empty: '%s'",
        toString(actual), actualExceptionMessage
      )
      .isNotEmpty();
    return this;
  }

  /**
   * Assert a specific deployment id for the {@link Job}
   * @param expectedDeploymentId the expected deployment id     
   * @return this {@link JobAssert}
   * @see org.camunda.bpm.engine.runtime.Job#getDeploymentId()
   */
  public JobAssert hasDeploymentId(final String expectedDeploymentId) {
    isNotNull();
    Assertions.assertThat(expectedDeploymentId).isNotNull();

    final String actualDeploymentId = actual.getDeploymentId();
    Assertions.assertThat(actualDeploymentId)
      .overridingErrorMessage(
        "expecting %s to have deployment id '%s', but found it to be '%s'",
        toString(actual), expectedDeploymentId, actualDeploymentId
      )
      .isEqualTo(expectedDeploymentId);
    return this;
  }

  private String toString(Job job) {
     return job != null ? String.format("actual %s {id='%s', processInstanceId='%s', executionId='%s'}", Job.class.getName(), job.getId(), job.getProcessInstanceId(), job.getExecutionId()) : null; 
  }

}
