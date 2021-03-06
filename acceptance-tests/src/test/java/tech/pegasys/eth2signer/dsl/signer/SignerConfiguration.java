/*
 * Copyright 2019 ConsenSys AG.
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
package tech.pegasys.eth2signer.dsl.signer;

import tech.pegasys.eth2signer.core.config.TlsOptions;
import tech.pegasys.eth2signer.dsl.tls.TlsCertificateDefinition;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class SignerConfiguration {

  public static final int UNASSIGNED_PORT = 0;

  private final String hostname;
  private final int httpRpcPort;
  private final List<String> httpHostAllowList;
  private final Path keyStorePath;
  private final List<String> metricsHostAllowList;
  private final boolean metricsEnabled;
  private Optional<TlsOptions> serverTlsOptions;
  private Optional<TlsCertificateDefinition> overriddenCaTrustStore;
  private final int metricsPort;

  public SignerConfiguration(
      final String hostname,
      final int httpRpcPort,
      final List<String> httpHostAllowList,
      final Path keyStorePath,
      final int metricsPort,
      final List<String> metricsHostAllowList,
      final boolean metricsEnabled,
      final Optional<TlsOptions> serverTlsOptions,
      final Optional<TlsCertificateDefinition> overriddenCaTrustStore) {
    this.hostname = hostname;
    this.httpRpcPort = httpRpcPort;
    this.httpHostAllowList = httpHostAllowList;
    this.keyStorePath = keyStorePath;
    this.metricsPort = metricsPort;
    this.metricsHostAllowList = metricsHostAllowList;
    this.metricsEnabled = metricsEnabled;
    this.serverTlsOptions = serverTlsOptions;
    this.overriddenCaTrustStore = overriddenCaTrustStore;
  }

  public String hostname() {
    return hostname;
  }

  public int httpPort() {
    return httpRpcPort;
  }

  public List<String> getHttpHostAllowList() {
    return httpHostAllowList;
  }

  public Path getKeyStorePath() {
    return keyStorePath;
  }

  public boolean isHttpDynamicPortAllocation() {
    return httpRpcPort == UNASSIGNED_PORT;
  }

  public boolean isMetricsEnabled() {
    return metricsEnabled;
  }

  public int getMetricsPort() {
    return metricsPort;
  }

  public List<String> getMetricsHostAllowList() {
    return metricsHostAllowList;
  }

  public Optional<TlsOptions> getServerTlsOptions() {
    return serverTlsOptions;
  }

  public Optional<TlsCertificateDefinition> getOverriddenCaTrustStore() {
    return overriddenCaTrustStore;
  }

  public boolean isMetricsDynamicPortAllocation() {
    return metricsPort == UNASSIGNED_PORT;
  }
}
