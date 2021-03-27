/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.configuration.cfg4j;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static java.util.Objects.requireNonNull;
import java.util.ServiceLoader;
import org.cfg4j.provider.ConfigurationProvider;
import org.cfg4j.provider.ConfigurationProviderBuilder;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.compose.MergeConfigurationSource;
import org.cfg4j.source.context.environment.DefaultEnvironment;
import org.cfg4j.source.context.environment.Environment;
import org.cfg4j.source.files.FilesConfigurationSource;
import org.opentcs.configuration.ConfigurationBindingProvider;
import static org.opentcs.util.Assertions.checkState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A configuration binding provider implementation using cfg4j.
 *
 * @author Martin Grzenia (Fraunhofer IML)
 */
public class Cfg4jConfigurationBindingProvider
    implements ConfigurationBindingProvider {

  /**
   * This class's logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(Cfg4jConfigurationBindingProvider.class);
  /**
   * The key of the (system) property containing the reload interval.
   */
  private static final String PROPKEY_RELOAD_INTERVAL = "opentcs.cfg4j.reload.interval";
  /**
   * Default configuration file name.
   */
  private final Path defaultsPath;
  /**
   * Supplementary configuration files.
   */
  private final Path[] supplementaryPaths;
  /**
   * The (cfg4j) configuration provider.
   */
  private final ConfigurationProvider provider;

  /**
   * Creates a new instance.
   *
   * @param defaultsPath Default configuration file name.
   * @param supplementaryPaths Supplementary configuration file names.
   */
  public Cfg4jConfigurationBindingProvider(Path defaultsPath, Path... supplementaryPaths) {
    this.defaultsPath = requireNonNull(defaultsPath, "baselineDefaultsPath");
    this.supplementaryPaths = requireNonNull(supplementaryPaths, "supplementaryPaths");

    this.provider = buildProvider();
  }

  @Override
  public <T> T get(String prefix, Class<T> type) {
    return provider.bind(prefix, type);
  }

  private ConfigurationProvider buildProvider() {
    Environment environment = new DefaultEnvironment();

    return new ConfigurationProviderBuilder()
        .withConfigurationSource(buildSource(environment))
        .withEnvironment(environment)
        .withReloadStrategy(new PeriodicalReloadStrategy(reloadInterval()))
        .build();
  }

  private long reloadInterval() {
    final long DEFAULT_RELOAD_INTERVAL = 10000;
    String valueString = System.getProperty(PROPKEY_RELOAD_INTERVAL);

    if (valueString == null) {
      LOG.info("Using default configuration reload interval ({} ms).", DEFAULT_RELOAD_INTERVAL);
      return DEFAULT_RELOAD_INTERVAL;
    }

    try {
      long value = Long.parseLong(valueString);
      LOG.info("Using configuration reload interval of {} ms.", value);
      return value;
    }
    catch (NumberFormatException exc) {
      LOG.warn("Could not parse '{}', using default configuration reload interval ({} ms).",
               valueString,
               DEFAULT_RELOAD_INTERVAL,
               exc);
      return DEFAULT_RELOAD_INTERVAL;
    }
  }

  private ConfigurationSource buildSource(Environment environment) {
    List<ConfigurationSource> sources = new ArrayList<>();

    // A file for baseline defaults MUST exist in the distribution.
    checkState(defaultsPath.toFile().isFile(),
               "Required default configuration file {} does not exist.",
               defaultsPath.toFile().getAbsolutePath());
    LOG.info("Using default configuration file {}...",
             defaultsPath.toFile().getAbsolutePath());
    sources.add(new FilesConfigurationSource(() -> Arrays.asList(defaultsPath)));

    // Files with supplementary configuration MAY exist in the distribution.
    for (Path supplementaryPath : supplementaryPaths) {
      if (supplementaryPath.toFile().isFile()) {
        LOG.info("Using overrides from supplementary configuration file {}...",
                 supplementaryPath.toFile().getAbsolutePath());
        sources.add(new FilesConfigurationSource(() -> Arrays.asList(supplementaryPath)));
      }
      else {
        LOG.warn("Supplementary configuration file {} not found, skipped.",
                 supplementaryPath.toFile().getAbsolutePath());
      }
    }

    for (ConfigurationSource source : ServiceLoader.load(SupplementaryConfigurationSource.class)) {
      LOG.info("Using overrides from additional configuration source implementation {}...",
               source.getClass());
      sources.add(source);
    }

    ConfigurationSource mergedSource
        = new MergeConfigurationSource(sources.toArray(new ConfigurationSource[sources.size()]));

    ConfigurationSource cachedSource = new CachedConfigurationSource(mergedSource, environment);

    return cachedSource;
  }
}
