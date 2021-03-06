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
package tech.pegasys.eth2signer.tests;

import static io.restassured.RestAssured.given;

import tech.pegasys.eth2signer.dsl.signer.SignerConfiguration;
import tech.pegasys.eth2signer.dsl.signer.SignerConfigurationBuilder;

import java.util.Collections;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

public class HttpHostAllowListAcceptanceTest extends AcceptanceTestBase {
  private static final String PUBLIC_KEYS_ENDPOINT = "/signer/publicKeys";

  @Test
  void httpEndpointWithDefaultAllowHostsRespondsWithOkResponse() {
    final SignerConfiguration signerConfiguration = new SignerConfigurationBuilder().build();
    startSigner(signerConfiguration);

    given()
        .baseUri(signer.getUrl())
        .contentType(ContentType.JSON)
        .when()
        .get(PUBLIC_KEYS_ENDPOINT)
        .then()
        .assertThat()
        .statusCode(200);
  }

  @Test
  void httpEndpointForAllowedHostRespondsWithOkResponse() {
    final SignerConfiguration signerConfiguration =
        new SignerConfigurationBuilder()
            .withHttpAllowHostList(Collections.singletonList("127.0.0.1, foo"))
            .build();
    startSigner(signerConfiguration);

    given()
        .baseUri(signer.getUrl())
        .contentType(ContentType.JSON)
        .when()
        .header("Host", "foo")
        .get(PUBLIC_KEYS_ENDPOINT)
        .then()
        .assertThat()
        .statusCode(200);
  }

  @Test
  void httpEndpointForNonAllowedHostRespondsWithForbiddenResponse() {
    final SignerConfiguration signerConfiguration =
        new SignerConfigurationBuilder()
            .withHttpAllowHostList(Collections.singletonList("127.0.0.1"))
            .build();
    startSigner(signerConfiguration);

    given()
        .baseUri(signer.getUrl())
        .contentType(ContentType.JSON)
        .when()
        .header("Host", "bar")
        .get(PUBLIC_KEYS_ENDPOINT)
        .then()
        .assertThat()
        .statusCode(403);
  }
}
