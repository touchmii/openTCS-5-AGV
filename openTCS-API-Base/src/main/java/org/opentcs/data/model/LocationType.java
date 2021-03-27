/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import static java.util.Objects.requireNonNull;
import org.opentcs.data.ObjectHistory;
import org.opentcs.data.TCSObject;

/**
 * Describes the type of a {@link Location}.
 *
 * @author Stefan Walter (Fraunhofer IML)
 */
public class LocationType
    extends TCSObject<LocationType>
    implements Serializable {

  /**
   * The operations allowed at locations of this type.
   */
  private final List<String> allowedOperations;

  /**
   * Creates a new LocationType.
   *
   * @param name The new location type's name.
   */
  public LocationType(String name) {
    super(name);
    this.allowedOperations = new ArrayList<>();
  }

  private LocationType(String name,
                       Map<String, String> properties,
                       ObjectHistory history,
                       List<String> allowedOperations) {
    super(name, properties, history);
    this.allowedOperations = listWithoutNullValues(requireNonNull(allowedOperations,
                                                                  "allowedOperations"));
  }

  @Override
  public LocationType withProperty(String key, String value) {
    return new LocationType(getName(),
                            propertiesWith(key, value),
                            getHistory(),
                            allowedOperations);
  }

  @Override
  public LocationType withProperties(Map<String, String> properties) {
    return new LocationType(getName(),
                            properties,
                            getHistory(),
                            allowedOperations);
  }

  @Override
  public TCSObject<LocationType> withHistoryEntry(ObjectHistory.Entry entry) {
    return new LocationType(getName(),
                            getProperties(),
                            getHistory().withEntryAppended(entry),
                            allowedOperations);
  }

  @Override
  public TCSObject<LocationType> withHistory(ObjectHistory history) {
    return new LocationType(getName(),
                            getProperties(),
                            history,
                            allowedOperations);
  }

  /**
   * Returns a set of operations allowed with locations of this type.
   *
   * @return A set of operations allowed with locations of this type.
   */
  public List<String> getAllowedOperations() {
    return Collections.unmodifiableList(allowedOperations);
  }

  /**
   * Checks if a given operation is allowed with locations of this type.
   *
   * @param operation The operation to be checked for.
   * @return <code>true</code> if, and only if, the given operation is allowed
   * with locations of this type.
   */
  public boolean isAllowedOperation(String operation) {
    requireNonNull(operation, "operation");
    return allowedOperations.contains(operation);
  }

  /**
   * Creates a copy of this object, with the given allowed operations.
   *
   * @param allowedOperations The value to be set in the copy.
   * @return A copy of this object, differing in the given value.
   */
  public LocationType withAllowedOperations(List<String> allowedOperations) {
    return new LocationType(getName(),
                            getProperties(),
                            getHistory(),
                            allowedOperations);
  }
}
