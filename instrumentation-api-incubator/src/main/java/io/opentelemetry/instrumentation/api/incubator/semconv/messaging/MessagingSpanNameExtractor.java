/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.api.incubator.semconv.messaging;

import io.opentelemetry.instrumentation.api.instrumenter.SpanNameExtractor;
import io.opentelemetry.instrumentation.api.semconv.network.ServerAttributesGetter;

public final class MessagingSpanNameExtractor<REQUEST> implements SpanNameExtractor<REQUEST> {

  /**
   * Returns a {@link SpanNameExtractor} that constructs the span name according to <a
   * href="https://github.com/open-telemetry/semantic-conventions/blob/main/docs/messaging/messaging-spans.md#span-name">
   * messaging semantic conventions</a>: {@code <destination name> <operation name>}.
   *
   * @see MessagingAttributesGetter#getDestination(Object) used to extract {@code <destination
   *     name>}.
   * @see MessageOperation used to extract {@code <operation name>}.
   */
  public static <REQUEST> SpanNameExtractor<REQUEST> create(
      MessagingAttributesGetter<REQUEST, ?> getter, MessageOperation operation,
      ServerAttributesGetter<REQUEST> serverAttributesGetter) {
    return new MessagingSpanNameExtractor<>(getter, operation, serverAttributesGetter);
  }

  private final MessagingAttributesGetter<REQUEST, ?> getter;
  private final ServerAttributesGetter<REQUEST> serverAttributesGetter;
  private final MessageOperation operation;

  private MessagingSpanNameExtractor(
      MessagingAttributesGetter<REQUEST, ?> getter, MessageOperation operation,
      ServerAttributesGetter<REQUEST> serverAttributesGetter) {
    this.getter = getter;
    this.serverAttributesGetter = serverAttributesGetter;
    this.operation = operation;
  }

  @Override
  public String extract(REQUEST request) {
    if (getter.getDestinationTemplate(request) != null) {
      return getter.getDestinationTemplate(request) + " " + operation.operationName();
    }
    if (getter.isTemporaryDestination(request)) {
      return MessagingAttributesExtractor.TEMP_DESTINATION_NAME + " " + operation.operationName();
    }
    if (getter.isAnonymousDestination(request)) {
      return MessagingAttributesExtractor.ANONYMOUS_DESTINATION_NAME + " "
          + operation.operationName();
    }
    if (getter.getDestination(request) != null) {
      return getter.getDestination(request) + " " + operation.operationName();
    }

    if (serverAttributesGetter.getServerAddress(request) != null
        && serverAttributesGetter.getServerPort(request) != null) {
      return serverAttributesGetter.getServerAddress(request) + ":"
          + serverAttributesGetter.getServerPort(request) + " " + operation.operationName();
    }

    return operation.operationName();
  }
}
