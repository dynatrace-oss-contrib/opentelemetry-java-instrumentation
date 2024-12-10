/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.api.incubator.semconv.messaging;

import javax.annotation.Nullable;
import io.opentelemetry.instrumentation.api.instrumenter.SpanNameExtractor;
import io.opentelemetry.instrumentation.api.internal.SemconvStability;
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
    if (SemconvStability.emitStableMessagingSemconv()) {
      String destination = getDestination(request);
      if (destination == null) {
        return getter.getOperationName(request);
      }
      return getter.getOperationName(request) + " " + destination;
    }
    String destinationName =
        getter.isTemporaryDestination(request)
            ? MessagingAttributesExtractor.TEMP_DESTINATION_NAME
            : getter.getDestination(request);
    if (destinationName == null) {
      destinationName = "unknown";
    }

    return destinationName + " " + operation.operationName();
  }

  @Nullable
  private String getDestination(REQUEST request) {
    String destination = null;
    if (getter.getDestinationTemplate(request) != null) {
      destination = getter.getDestinationTemplate(request);
    } else if (getter.isTemporaryDestination(request)) {
      destination = MessagingAttributesExtractor.TEMP_DESTINATION_NAME;
    } else if (getter.isAnonymousDestination(request)) {
      destination = MessagingAttributesExtractor.ANONYMOUS_DESTINATION_NAME;
    } else if (getter.getDestination(request) != null) {
      destination = getter.getDestination(request);
    } else {
      if (serverAttributesGetter.getServerAddress(request) != null
          && serverAttributesGetter.getServerPort(request) != null) {
        destination = serverAttributesGetter.getServerAddress(request) + ":"
            + serverAttributesGetter.getServerPort(request);
      }
    }
    return destination;
  }
}
