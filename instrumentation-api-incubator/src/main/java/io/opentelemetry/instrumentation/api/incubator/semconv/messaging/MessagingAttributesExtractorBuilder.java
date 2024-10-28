/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.api.incubator.semconv.messaging;

import static java.util.Collections.emptyList;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.opentelemetry.instrumentation.api.instrumenter.AttributesExtractor;
import java.util.List;

/** A builder of {@link MessagingAttributesExtractor}. */
public final class MessagingAttributesExtractorBuilder<REQUEST, RESPONSE> {

  final MessagingAttributesGetter<REQUEST, RESPONSE> getter;
  final MessagingNetworkAttributesGetter<REQUEST, RESPONSE> networkAttributesGetter;
  final MessageOperation operation;
  List<String> capturedHeaders = emptyList();

  MessagingAttributesExtractorBuilder(
      MessagingAttributesGetter<REQUEST, RESPONSE> getter,
      MessagingNetworkAttributesGetter<REQUEST, RESPONSE> networkAttributesGetter,
      MessageOperation operation) {
    this.getter = getter;
    this.networkAttributesGetter = networkAttributesGetter;
    this.operation = operation;
  }

  /**
   * Configures the messaging headers that will be captured as span attributes.
   *
   * <p>The messaging header values will be captured under the {@code messaging.header.<name>}
   * attribute key. The {@code <name>} part in the attribute key is the normalized header name:
   * lowercase, with dashes replaced by underscores.
   *
   * @param capturedHeaders A list of messaging header names.
   */
  @CanIgnoreReturnValue
  public MessagingAttributesExtractorBuilder<REQUEST, RESPONSE> setCapturedHeaders(
      List<String> capturedHeaders) {
    this.capturedHeaders = capturedHeaders;
    return this;
  }

  /**
   * Returns a new {@link MessagingAttributesExtractor} with the settings of this {@link
   * MessagingAttributesExtractorBuilder}.
   */
  public AttributesExtractor<REQUEST, RESPONSE> build() {
    return new MessagingAttributesExtractor<>(getter, networkAttributesGetter, operation, capturedHeaders);
  }
}
