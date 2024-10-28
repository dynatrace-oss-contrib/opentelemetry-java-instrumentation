/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.api.internal;

import static java.util.Arrays.asList;

import java.util.HashSet;
import java.util.Set;

/**
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
public final class SemconvStability {

  private static final boolean emitOldMessagingSemconv;
  private static final boolean emitStableMessagingSemconv;

  static {
    boolean oldMessaging = true;
    boolean stableMessaging = false;

    String value = ConfigPropertiesUtil.getString("otel.semconv-stability.opt-in");
    if (value != null) {
      Set<String> values = new HashSet<>(asList(value.split(",")));

      if (values.contains("messaging")) {
        oldMessaging = false;
        stableMessaging = true;
      }
      // no else -- technically it's possible to set "messaging,messaging/dup", in which case we should emit
      // both sets of attributes
      if (values.contains("messaging/dup")) {
        oldMessaging = true;
        stableMessaging = true;
      }
    }

    emitOldMessagingSemconv = oldMessaging;
    emitStableMessagingSemconv = stableMessaging;
  }

  public static boolean emitOldMessagingSemconv() {
    return emitOldMessagingSemconv;
  }

  public static boolean emitStableMessagingSemconv() {
    return emitStableMessagingSemconv;
  }

  private SemconvStability() {}
}
