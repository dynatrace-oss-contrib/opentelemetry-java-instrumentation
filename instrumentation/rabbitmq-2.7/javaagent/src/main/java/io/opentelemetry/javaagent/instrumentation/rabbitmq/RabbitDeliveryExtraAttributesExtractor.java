/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.rabbitmq;

import com.google.common.base.Strings;
import com.rabbitmq.client.Envelope;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.instrumenter.AttributesExtractor;
import javax.annotation.Nullable;

class RabbitDeliveryExtraAttributesExtractor implements AttributesExtractor<DeliveryRequest, Void> {
  private static final AttributeKey<String> MESSAGING_RABBITMQ_DESTINATION_ROUTING_KEY =
      AttributeKey.stringKey("messaging.rabbitmq.destination.routing_key");
  private static final AttributeKey<Long> MESSAGING_RABBITMQ_MESSAGE_DELIVERY_TAG =
      AttributeKey.longKey("messaging.rabbitmq.message.delivery_tag");

  @Override
  public void onStart(
      AttributesBuilder attributes, Context parentContext, DeliveryRequest request) {
    Envelope envelope = request.getEnvelope();

    if (!Strings.isNullOrEmpty(envelope.getRoutingKey())) {
      attributes.put(MESSAGING_RABBITMQ_DESTINATION_ROUTING_KEY, envelope.getRoutingKey());
    }
    attributes.put(MESSAGING_RABBITMQ_MESSAGE_DELIVERY_TAG, envelope.getDeliveryTag());
  }

  @Override
  public void onEnd(
      AttributesBuilder attributes,
      Context context,
      DeliveryRequest request,
      @Nullable Void unused,
      @Nullable Throwable error) {}
}
