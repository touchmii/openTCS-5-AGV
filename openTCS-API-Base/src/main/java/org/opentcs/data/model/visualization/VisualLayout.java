/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.data.model.visualization;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import static java.util.Objects.requireNonNull;
import java.util.Set;
import org.opentcs.data.ObjectHistory;
import org.opentcs.data.TCSObject;

/**
 * Describes the visual attributes of a model.
 *
 * @author Stefan Walter (Fraunhofer IML)
 */
public class VisualLayout
    extends TCSObject<VisualLayout>
    implements Serializable {

  /**
   * This layout's scale on the X axis (in mm/pixel).
   */
  private final double scaleX;
  /**
   * This layout's scale on the Y axis (in mm/pixel).
   */
  private final double scaleY;
  /**
   * VisualLayout elements describing the visualization of a model and additional
   * elements that need to be displayed.
   */
  private final Set<LayoutElement> layoutElements;

  /**
   * Creates a new VisualLayout.
   *
   * @param name This visual layout's name.
   */
  public VisualLayout(String name) {
    super(name);
    this.scaleX = 50.0;
    this.scaleY = 50.0;
    this.layoutElements = new HashSet<>();
  }

  /**
   * Creates a new VisualLayout.
   *
   * @param name This visual layout's name.
   */
  private VisualLayout(String name,
                       Map<String, String> properties,
                       ObjectHistory history,
                       double scaleX,
                       double scaleY,
                       Set<LayoutElement> layoutElements) {
    super(name, properties, history);
    this.scaleX = scaleX;
    this.scaleY = scaleY;
    this.layoutElements = new HashSet<>(requireNonNull(layoutElements, "layoutElements"));
  }

  @Override
  public VisualLayout withProperty(String key, String value) {
    return new VisualLayout(getName(),
                            propertiesWith(key, value),
                            getHistory(),
                            scaleX,
                            scaleY,
                            layoutElements);
  }

  @Override
  public VisualLayout withProperties(Map<String, String> properties) {
    return new VisualLayout(getName(),
                            properties,
                            getHistory(),
                            scaleX,
                            scaleY,
                            layoutElements);
  }

  @Override
  public TCSObject<VisualLayout> withHistoryEntry(ObjectHistory.Entry entry) {
    return new VisualLayout(getName(),
                            getProperties(),
                            getHistory().withEntryAppended(entry),
                            scaleX,
                            scaleY,
                            layoutElements);
  }

  @Override
  public TCSObject<VisualLayout> withHistory(ObjectHistory history) {
    return new VisualLayout(getName(),
                            getProperties(),
                            history,
                            scaleX,
                            scaleY,
                            layoutElements);
  }

  /**
   * Returns this layout's scale on the X axis (in mm/pixel).
   *
   * @return This layout's scale on the X axis.
   */
  public double getScaleX() {
    return scaleX;
  }

  /**
   * Creates a copy of this object, with the given scaleX.
   *
   * @param scaleX The value to be set in the copy.
   * @return A copy of this object, differing in the given value.
   */
  public VisualLayout withScaleX(double scaleX) {
    return new VisualLayout(getName(),
                            getProperties(),
                            getHistory(),
                            scaleX,
                            scaleY,
                            layoutElements);
  }

  /**
   * Returns this layout's scale on the Y axis (in mm/pixel).
   *
   * @return This layout's scale on the Y axis.
   */
  public double getScaleY() {
    return scaleY;
  }

  /**
   * Creates a copy of this object, with the given scaleY.
   *
   * @param scaleY The value to be set in the copy.
   * @return A copy of this object, differing in the given value.
   */
  public VisualLayout withScaleY(double scaleY) {
    return new VisualLayout(getName(),
                            getProperties(),
                            getHistory(),
                            scaleX,
                            scaleY,
                            layoutElements);
  }

  /**
   * Returns the layout elements describing the visualization of a model.
   *
   * @return The layout elements describing the visualization of a model.
   */
  public Set<LayoutElement> getLayoutElements() {
    return layoutElements;
  }

  /**
   * Creates a copy of this object, with the given layoutElements.
   *
   * @param layoutElements The value to be set in the copy.
   * @return A copy of this object, differing in the given value.
   */
  public VisualLayout withLayoutElements(Set<LayoutElement> layoutElements) {
    return new VisualLayout(getName(),
                            getProperties(),
                            getHistory(),
                            scaleX,
                            scaleY,
                            layoutElements);
  }
}
