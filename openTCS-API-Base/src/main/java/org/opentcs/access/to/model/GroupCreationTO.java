/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.access.to.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import static java.util.Objects.requireNonNull;
import java.util.Set;
import javax.annotation.Nonnull;
import org.opentcs.access.to.CreationTO;

/**
 * A transfer object describing a group in the plant model.
 *
 * @author Stefan Walter (Fraunhofer IML)
 */
public class GroupCreationTO
    extends CreationTO
    implements Serializable {

  /**
   * This group's member names.
   */
  @Nonnull
  private Set<String> memberNames = new HashSet<>();

  /**
   * Creates a new instance.
   *
   * @param name The name of this group.
   */
  public GroupCreationTO(String name) {
    super(name);
  }

  private GroupCreationTO(@Nonnull String name,
                          @Nonnull Map<String, String> properties,
                          @Nonnull Set<String> memberNames) {
    super(name, properties);
    this.memberNames = requireNonNull(memberNames, "memberNames");
  }

  /**
   * Creates a copy of this object with the given name.
   *
   * @param name The new name.
   * @return A copy of this object, differing in the given value.
   */
  @Override
  public GroupCreationTO withName(@Nonnull String name) {
    return new GroupCreationTO(name, getModifiableProperties(), memberNames);
  }

  /**
   * Creates a copy of this object with the given properties.
   *
   * @param properties the new properties.
   * @return A copy of this object, differing in the given properties.
   */
  @Override
  public GroupCreationTO withProperties(@Nonnull Map<String, String> properties) {
    return new GroupCreationTO(getName(), properties, memberNames);
  }

  /**
   * Creates a copy of this object and adds the given property.
   * If value == null, then the key-value pair is removed from the properties.
   *
   * @param key the key.
   * @param value the value
   * @return A copy of this object that either
   * includes the given entry in it's current properties, if value != null or
   * excludes the entry otherwise.
   */
  @Override
  public GroupCreationTO withProperty(@Nonnull String key, @Nonnull String value) {
    return new GroupCreationTO(getName(), propertiesWith(key, value), memberNames);
  }

  /**
   * Returns the names of this group's members.
   *
   * @return The names of this group's members.
   */
  @Nonnull
  public Set<String> getMemberNames() {
    return Collections.unmodifiableSet(memberNames);
  }

  /**
   * Creates a copy of this object with group's members.
   *
   * @param memberNames The names of this group's members.
   * @return A copy of this object, differing in the given value.
   */
  public GroupCreationTO withMemberNames(@Nonnull Set<String> memberNames) {
    return new GroupCreationTO(getName(), getModifiableProperties(), memberNames);
  }
}
