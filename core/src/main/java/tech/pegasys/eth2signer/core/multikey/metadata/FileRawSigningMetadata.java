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
package tech.pegasys.eth2signer.core.multikey.metadata;

import tech.pegasys.eth2signer.core.signing.ArtifactSigner;
import tech.pegasys.teku.bls.BLSSecretKey;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FileRawSigningMetadata implements SigningMetadata {

  private final BLSSecretKey privateKey;

  public FileRawSigningMetadata(
      @JsonProperty(value = "privateKey", required = true) BLSSecretKey privateKey) {
    this.privateKey = privateKey;
  }

  @Override
  public ArtifactSigner createSigner(final ArtifactSignerFactory factory) {
    return factory.create(this);
  }

  public BLSSecretKey getSecretKey() {
    return privateKey;
  }
}
