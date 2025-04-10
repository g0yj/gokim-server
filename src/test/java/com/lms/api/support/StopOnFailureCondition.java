package com.lms.api.support;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

public class StopOnFailureCondition implements ExecutionCondition {

  private static boolean failed = false;

  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
    if (failed) {
      return ConditionEvaluationResult.disabled("A previous test failed. Skipping this test.");
    }
    return ConditionEvaluationResult.enabled("Test is enabled");
  }

  public static void setFailed(boolean hasFailed) {
    failed = hasFailed;
  }
}