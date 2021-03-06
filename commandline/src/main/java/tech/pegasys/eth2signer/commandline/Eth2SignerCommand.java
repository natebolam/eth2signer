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
package tech.pegasys.eth2signer.commandline;

import static tech.pegasys.eth2signer.commandline.DefaultCommandValues.CONFIG_FILE_OPTION_NAME;
import static tech.pegasys.eth2signer.commandline.DefaultCommandValues.MANDATORY_FILE_FORMAT_HELP;
import static tech.pegasys.eth2signer.commandline.DefaultCommandValues.MANDATORY_HOST_FORMAT_HELP;
import static tech.pegasys.eth2signer.commandline.DefaultCommandValues.MANDATORY_PORT_FORMAT_HELP;
import static tech.pegasys.eth2signer.core.metrics.Eth2SignerMetricCategory.DEFAULT_METRIC_CATEGORIES;

import tech.pegasys.eth2signer.commandline.config.AllowListHostsProperty;
import tech.pegasys.eth2signer.commandline.config.PicoCliTlsServerOptions;
import tech.pegasys.eth2signer.commandline.convertor.MetricCategoryConverter;
import tech.pegasys.eth2signer.core.Runner;
import tech.pegasys.eth2signer.core.config.Config;
import tech.pegasys.eth2signer.core.config.TlsOptions;
import tech.pegasys.eth2signer.core.metrics.Eth2SignerMetricCategory;

import java.io.File;
import java.net.InetAddress;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.common.base.MoreObjects;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hyperledger.besu.metrics.StandardMetricCategory;
import org.hyperledger.besu.plugin.services.metrics.MetricCategory;
import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.Option;
import picocli.CommandLine.TypeConversionException;

@SuppressWarnings("FieldCanBeLocal") // because Picocli injected fields report false positives
@Command(
    description =
        "This command runs the Eth2Signer.\n"
            + "Documentation can be found at https://docs.eth2signer.pegasys.tech.",
    abbreviateSynopsis = true,
    name = "eth2signer",
    sortOptions = false,
    mixinStandardHelpOptions = true,
    versionProvider = VersionProvider.class,
    header = "Usage:",
    synopsisHeading = "%n",
    descriptionHeading = "%nDescription:%n%n",
    optionListHeading = "%nOptions:%n",
    footerHeading = "%n",
    subcommands = {HelpCommand.class},
    footer = "Eth2Signer is licensed under the Apache License 2.0")
public class Eth2SignerCommand implements Config, Runnable {

  private static final Logger LOG = LogManager.getLogger();

  @SuppressWarnings("UnusedVariable")
  @CommandLine.Option(
      names = {CONFIG_FILE_OPTION_NAME},
      paramLabel = MANDATORY_FILE_FORMAT_HELP,
      description = "Config file in yaml format (default: none)")
  private final File configFile = null;

  @Option(
      names = {"--data-path"},
      description = "The path to a directory to store temporary files",
      paramLabel = DefaultCommandValues.MANDATORY_PATH_FORMAT_HELP,
      arity = "1")
  private Path dataPath;

  @Option(
      names = {"--key-store-path"},
      description = "The path to a directory storing toml files defining available keys",
      paramLabel = DefaultCommandValues.MANDATORY_PATH_FORMAT_HELP,
      arity = "1")
  private Path keyStorePath = Path.of("./");

  @Option(
      names = {"--key-cache-limit"},
      description =
          "The maximum number of keys that will be cached in memory (default: ${DEFAULT-VALUE})",
      paramLabel = DefaultCommandValues.MANDATORY_LONG_FORMAT_HELP,
      converter = CacheLimitConverter.class)
  private Long keyCacheLimit = 1000L;

  @Option(
      names = {"--logging", "-l"},
      paramLabel = "<LOG VERBOSITY LEVEL>",
      description =
          "Logging verbosity levels: OFF, FATAL, WARN, INFO, DEBUG, TRACE, ALL (default: INFO)")
  private final Level logLevel = Level.INFO;

  @SuppressWarnings("FieldMayBeFinal") // Because PicoCLI requires Strings to not be final.
  @Option(
      names = {"--http-listen-host"},
      description = "Host for HTTP to listen on (default: ${DEFAULT-VALUE})",
      paramLabel = MANDATORY_HOST_FORMAT_HELP,
      arity = "1")
  private String httpListenHost = InetAddress.getLoopbackAddress().getHostAddress();

  @Option(
      names = {"--http-listen-port"},
      description = "Port for HTTP to listen on (default: ${DEFAULT-VALUE})",
      paramLabel = MANDATORY_PORT_FORMAT_HELP,
      arity = "1")
  private final Integer httpListenPort = 9000;

  @Option(
      names = {"--http-host-allowlist"},
      paramLabel = "<hostname>[,<hostname>...]... or * or all",
      description =
          "Comma separated list of hostnames to allow for http access, or * to accept any host (default: ${DEFAULT-VALUE})",
      defaultValue = "localhost,127.0.0.1")
  private final AllowListHostsProperty httpHostAllowList = new AllowListHostsProperty();

  @Option(
      names = {"--metrics-enabled"},
      description = "Set to start the metrics exporter (default: ${DEFAULT-VALUE})")
  private final Boolean metricsEnabled = false;

  @SuppressWarnings({"FieldCanBeFinal", "FieldMayBeFinal"}) // PicoCLI requires non-final Strings.
  @Option(
      names = {"--metrics-host"},
      paramLabel = MANDATORY_HOST_FORMAT_HELP,
      description = "Host for the metrics exporter to listen on (default: ${DEFAULT-VALUE})",
      arity = "1")
  private String metricsHost = InetAddress.getLoopbackAddress().getHostAddress();

  @Option(
      names = {"--metrics-port"},
      paramLabel = MANDATORY_PORT_FORMAT_HELP,
      description = "Port for the metrics exporter to listen on (default: ${DEFAULT-VALUE})",
      arity = "1")
  private final Integer metricsPort = 9001;

  @Option(
      names = {"--metrics-category", "--metrics-categories"},
      paramLabel = "<category name>",
      split = ",",
      arity = "1..*",
      description =
          "Comma separated list of categories to track metrics for (default: ${DEFAULT-VALUE}),",
      converter = Eth2SignerMetricCategoryConverter.class)
  private final Set<MetricCategory> metricCategories = DEFAULT_METRIC_CATEGORIES;

  @Option(
      names = {"--metrics-host-allowlist"},
      paramLabel = "<hostname>[,<hostname>...]... or * or all",
      description =
          "Comma separated list of hostnames to allow for metrics access, or * to accept any host (default: ${DEFAULT-VALUE})",
      defaultValue = "localhost,127.0.0.1")
  private final AllowListHostsProperty metricsHostAllowList = new AllowListHostsProperty();

  @ArgGroup(exclusive = false)
  private PicoCliTlsServerOptions picoCliTlsServerOptions;

  @Override
  public Level getLogLevel() {
    return logLevel;
  }

  @Override
  public String getHttpListenHost() {
    return httpListenHost;
  }

  @Override
  public Integer getHttpListenPort() {
    return httpListenPort;
  }

  @Override
  public AllowListHostsProperty getHttpHostAllowList() {
    return httpHostAllowList;
  }

  @Override
  public Path getDataPath() {
    return dataPath;
  }

  @Override
  public Path getKeyConfigPath() {
    return keyStorePath;
  }

  @Override
  public Boolean isMetricsEnabled() {
    return metricsEnabled;
  }

  @Override
  public Integer getMetricsPort() {
    return metricsPort;
  }

  @Override
  public String getMetricsNetworkInterface() {
    return metricsHost;
  }

  @Override
  public Set<MetricCategory> getMetricCategories() {
    return metricCategories;
  }

  @Override
  public List<String> getMetricsHostAllowList() {
    return metricsHostAllowList;
  }

  @Override
  public Long getKeyCacheLimit() {
    return keyCacheLimit;
  }

  @Override
  public Optional<TlsOptions> getTlsOptions() {
    return Optional.ofNullable(picoCliTlsServerOptions);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("configFile", configFile)
        .add("dataPath", dataPath)
        .add("keyStorePath", keyStorePath)
        .add("keyCacheLimit", keyCacheLimit)
        .add("logLevel", logLevel)
        .add("httpListenHost", httpListenHost)
        .add("httpListenPort", httpListenPort)
        .add("httpHostAllowList", httpHostAllowList)
        .add("metricsEnabled", metricsEnabled)
        .add("metricsHost", metricsHost)
        .add("metricsPort", metricsPort)
        .add("metricCategories", metricCategories)
        .add("metricsHostAllowList", metricsHostAllowList)
        .add("picoCliTlsServerOptions", picoCliTlsServerOptions)
        .toString();
  }

  @Override
  public void run() {
    LOG.debug("Commandline has been parsed with: " + toString());
    final Runner runner = new Runner(this);
    runner.run();
  }

  public static class Eth2SignerMetricCategoryConverter extends MetricCategoryConverter {

    public Eth2SignerMetricCategoryConverter() {
      addCategories(Eth2SignerMetricCategory.class);
      addCategories(StandardMetricCategory.class);
    }
  }

  public static class CacheLimitConverter implements CommandLine.ITypeConverter<Long> {

    @Override
    public Long convert(final String value) {
      try {
        final long longValue = Long.parseLong(value);
        if (longValue < 0) {
          throw new TypeConversionException("Cache size must be a positive value");
        }
        return longValue;
      } catch (final NumberFormatException e) {
        throw new TypeConversionException("Cache size is not a valid number");
      }
    }
  }
}
