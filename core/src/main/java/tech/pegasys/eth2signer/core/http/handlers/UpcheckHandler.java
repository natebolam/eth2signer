/*
 * Copyright 2020 ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package tech.pegasys.eth2signer.core.http.handlers;

import static com.google.common.net.MediaType.PLAIN_TEXT_UTF_8;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class UpcheckHandler implements Handler<RoutingContext> {

  @Override
  public void handle(RoutingContext routingContext) {
    routingContext.response().putHeader(CONTENT_TYPE, PLAIN_TEXT_UTF_8.toString()).end("OK");
  }
}
