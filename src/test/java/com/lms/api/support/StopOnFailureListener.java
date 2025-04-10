package com.lms.api.support;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

public class StopOnFailureListener implements TestExecutionExceptionHandler {

  @Override
  public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
    StopOnFailureCondition.setFailed(true);
    throw throwable;
  }
}
