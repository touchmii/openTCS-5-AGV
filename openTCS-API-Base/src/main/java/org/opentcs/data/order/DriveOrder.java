/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.data.order;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import static java.util.Objects.requireNonNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.opentcs.data.TCSObjectReference;
import org.opentcs.data.model.Location;
import org.opentcs.data.model.Point;
import org.opentcs.data.model.Vehicle;
import static org.opentcs.util.Assertions.checkArgument;

/**
 * Describes a sequence of movements and an optional operation at the end that a {@link Vehicle} is
 * supposed to execute.
 *
 * @see TransportOrder
 * @author Stefan Walter (Fraunhofer IML)
 */
public class DriveOrder
    implements Serializable {

  /**
   * This drive order's destination.
   */
  private final Destination destination;
  /**
   * A back-reference to the transport order this drive order belongs to.
   */
  private TCSObjectReference<TransportOrder> transportOrder;
  /**
   * This drive order's route.
   */
  private Route route;
  /**
   * This drive order's current state.
   */
  private State state;

  /**
   * Creates a new DriveOrder.
   *
   * @param destination This drive order's destination.
   */
  public DriveOrder(@Nonnull Destination destination) {
    this.destination = requireNonNull(destination, "destination");
    this.transportOrder = null;
    this.route = null;
    this.state = State.PRISTINE;
  }

  private DriveOrder(@Nonnull Destination destination,
                     @Nullable TCSObjectReference<TransportOrder> transportOrder,
                     @Nullable Route route,
                     @Nonnull State state) {
    this.destination = requireNonNull(destination, "destination");
    this.transportOrder = transportOrder;
    this.route = route;
    this.state = requireNonNull(state, "state");
  }

  /**
   * Returns this drive order's destination.
   *
   * @return This drive order's destination.
   */
  @Nonnull
  public Destination getDestination() {
    return destination;
  }

  /**
   * Returns a reference to the transport order this drive order belongs to.
   *
   * @return A reference to the transport order this drive order belongs to.
   */
  @Nullable
  public TCSObjectReference<TransportOrder> getTransportOrder() {
    return transportOrder;
  }

  /**
   * Creates a copy of this object, with the given transport order.
   *
   * @param transportOrder The value to be set in the copy.
   * @return A copy of this object, differing in the given value.
   */
  public DriveOrder withTransportOrder(
      @Nullable TCSObjectReference<TransportOrder> transportOrder) {
    return new DriveOrder(destination, transportOrder, route, state);
  }

  /**
   * Returns this drive order's route.
   *
   * @return This drive order's route. May be <code>null</code> if this drive
   * order's route hasn't been calculated, yet.
   */
  @Nullable
  public Route getRoute() {
    return route;
  }

  /**
   * Creates a copy of this object, with the given route.
   *
   * @param route The value to be set in the copy.
   * @return A copy of this object, differing in the given value.
   */
  public DriveOrder withRoute(@Nullable Route route) {
    return new DriveOrder(destination, transportOrder, route, state);
  }

  /**
   * Returns this drive order's state.
   *
   * @return This drive order's state.
   */
  @Nonnull
  public State getState() {
    return state;
  }

  /**
   * Creates a copy of this object, with the given state.
   *
   * @param state The value to be set in the copy.
   * @return A copy of this object, differing in the given value.
   */
  public DriveOrder withState(@Nonnull State state) {
    return new DriveOrder(destination, transportOrder, route, state);
  }

  @Override
  public String toString() {
    return route + " -> " + destination;
  }

  /**
   * Describes the destination of a drive order.
   */
  public static class Destination
      implements Serializable {

    /**
     * An operation constant for doing nothing.
     */
    public static final String OP_NOP = "NOP";
    /**
     * An operation constant for parking the vehicle.
     */
    public static final String OP_PARK = "PARK";
    /**
     * An operation constant for sending the vehicle to a point without a location associated to it.
     */
    public static final String OP_MOVE = "MOVE";
    /**
     * The actual destination (point or location).
     */
    private final TCSObjectReference<?> destination;
    /**
     * The operation to be performed at the destination location.
     */
    private final String operation;
    /**
     * Properties of this destination.
     * May contain parameters for the operation, for instance.
     */
    private final Map<String, String> properties;

    /**
     * Creates a new instance.
     *
     * @param destination The actual destination (must be a reference to a location or point).
     */
    @SuppressWarnings("unchecked")
    public Destination(@Nonnull TCSObjectReference<?> destination) {
      checkArgument(destination.getReferentClass() == Location.class
          || destination.getReferentClass() == Point.class,
                    "Not a reference on a location or point: %s",
                    destination);

      this.destination = requireNonNull(destination, "destination");
      this.operation = OP_NOP;
      this.properties = Collections.unmodifiableMap(new HashMap<>());
    }

    private Destination(@Nonnull TCSObjectReference<?> destination,
                        @Nonnull Map<String, String> properties,
                        @Nonnull String operation) {
      this.destination = requireNonNull(destination, "destination");
      this.operation = requireNonNull(operation, "operation");
      this.properties = Collections.unmodifiableMap(new HashMap<>(properties));
    }

    /**
     * Returns the actual destination (a location or point).
     *
     * @return The actual destination (a location or point).
     */
    @Nonnull
    public TCSObjectReference<?> getDestination() {
      return destination;
    }

    /**
     * Returns the operation to be performed at the destination location.
     *
     * @return The operation to be performed at the destination location.
     */
    @Nonnull
    public String getOperation() {
      return operation;
    }

    /**
     * Creates a copy of this object, with the given operation.
     *
     * @param operation The value to be set in the copy.
     * @return A copy of this object, differing in the given value.
     */
    public Destination withOperation(@Nonnull String operation) {
      return new Destination(destination, properties, operation);
    }

    /**
     * Returns the properties of this destination.
     *
     * @return The properties of this destination.
     */
    @Nonnull
    public Map<String, String> getProperties() {
      return properties;
    }

    /**
     * Creates a copy of this object, with the given properties.
     *
     * @param properties The value to be set in the copy.
     * @return A copy of this object, differing in the given value.
     */
    public Destination withProperties(Map<String, String> properties) {
      return new Destination(destination, properties, operation);
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof Destination) {
        Destination other = (Destination) o;
        return destination.equals(other.destination)
            && operation.equals(other.operation)
            && properties.equals(other.properties);
      }
      else {
        return false;
      }
    }

    @Override
    public int hashCode() {
      return destination.hashCode() ^ operation.hashCode();
    }

    @Override
    public String toString() {
      return destination.getName() + ":" + operation;
    }
  }

  /**
   * Defines the various potential states of a drive order.
   */
  public enum State {

    /**
     * A drive order's initial state, indicating it being still untouched/not being processed.
     */
    PRISTINE,
    /**
     * Indicates the vehicle processing the order is currently moving to its destination.
     */
    TRAVELLING,
    /**
     * Indicates the vehicle processing the order is currently executing an operation.
     */
    OPERATING,
    /**
     * Marks a drive order as successfully completed.
     */
    FINISHED,
    /**
     * Marks a drive order as failed.
     */
    FAILED
  }
}
