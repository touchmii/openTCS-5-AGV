/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.drivers.vehicle.commands;

import static java.util.Objects.requireNonNull;
import javax.annotation.Nonnull;
import org.opentcs.drivers.vehicle.AdapterCommand;
import org.opentcs.drivers.vehicle.SimVehicleCommAdapter;
import org.opentcs.drivers.vehicle.VehicleCommAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A command for initializing the comm adapter's position.
 *
 * @author Martin Grzenia (Fraunhofer IML)
 */
public class InitPositionCommand
    implements AdapterCommand {

  /**
   * This class's logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(InitPositionCommand.class);
  /**
   * The position to set.
   */
  private final String position;

  /**
   * Creates a new instance.
   *
   * @param position The position to set.
   */
  public InitPositionCommand(@Nonnull String position) {
    this.position = requireNonNull(position, "position");
  }

  @Override
  public void execute(VehicleCommAdapter adapter) {
    if (!(adapter instanceof SimVehicleCommAdapter)) {
      LOG.warn("Adapter is not a SimVehicleCommAdapter: {}", adapter);
      return;
    }

    ((SimVehicleCommAdapter) adapter).initVehiclePosition(position);
  }
}
