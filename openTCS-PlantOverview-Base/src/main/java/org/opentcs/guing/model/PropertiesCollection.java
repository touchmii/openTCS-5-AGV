/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.guing.model;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import static org.opentcs.guing.I18nPlantOverviewBase.BUNDLE_PATH;
import org.opentcs.guing.components.properties.event.AttributesChangeListener;
import org.opentcs.guing.components.properties.type.AbstractProperty;
import org.opentcs.guing.components.properties.type.MultipleDifferentValues;
import org.opentcs.guing.components.properties.type.Property;

/**
 * Erlaubt die gleichzeitige Änderung von gleichnamigen Attributen mehrerer
 * Modellkomponenten auf einmal.
 *
 * @author Sebastian Naumann (ifak e.V. Magdeburg)
 */
public class PropertiesCollection
    extends CompositeModelComponent {

  /**
   * This class's resource bundle.
   */
  private final ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_PATH);

  /**
   * Creates a new instance.
   *
   * @param models
   */
  public PropertiesCollection(Collection<ModelComponent> models) {
    Objects.requireNonNull(models, "models is null");

    for (ModelComponent curModel : models) {
      add(curModel);
    }

    extractSameProperties();
  }

  /**
   * Findet die Attribute heraus, die gemeinschaftlich bearbeitet werden können.
   */
  private void extractSameProperties() {
    if (getChildComponents().isEmpty()) {
      return;
    }
    ModelComponent firstModel = getChildComponents().get(0);
    Map<String, Property> properties = firstModel.getProperties();

    for (Map.Entry<String, Property> curEntry : properties.entrySet()) {
      String name = curEntry.getKey();
      Property property = curEntry.getValue();
      boolean ok = true;
      boolean differentValues = false;

      if (!property.isCollectiveEditable()) {
        ok = false;
      }

      for (int i = 1; i < getChildComponents().size(); i++) {
        ModelComponent followingModel = getChildComponents().get(i);
        // Typ der Modelle vergleichen - nur gleichartige Objekte sollen gemeinsam editierbar sein
        if (!firstModel.getClass().equals(followingModel.getClass())) {
          return;
        }

        Property pendant = followingModel.getProperty(name);

        if (pendant == null) {
          ok = false;
          break;
        }
        else if ((!pendant.isCollectiveEditable())) {
          ok = false;
          break;
        }

        if (!(property.getComparableValue() == null && pendant.getComparableValue() == null)
            && (property.getComparableValue() != null && pendant.getComparableValue() == null
                || property.getComparableValue() == null && pendant.getComparableValue() != null
                || !property.getComparableValue().equals(pendant.getComparableValue()))) {
          differentValues = true;
        }
      }

      if (ok) {
        AbstractProperty clone = (AbstractProperty) property.clone();

        if (differentValues) {
          clone.setValue(new MultipleDifferentValues());

          clone.unmarkChanged();

          clone.setDescription(property.getDescription());
          clone.setHelptext(property.getHelptext());
          clone.setModellingEditable(property.isModellingEditable());
          clone.setOperatingEditable(property.isOperatingEditable());
          setProperty(name, clone);
        }
        else {
          // TODO: clone() Methode ?
          clone.setDescription(property.getDescription());
          clone.setHelptext(property.getHelptext());
          clone.setModellingEditable(property.isModellingEditable());
          clone.setOperatingEditable(property.isOperatingEditable());
          setProperty(name, clone);
        }
      }
    }
  }

  /**
   * Informiert alle registrierten Listener (Controller/Views) über die Änderung
   * der Daten. Der Controller/View, der die Änderung verursacht hat, ruft diese
   * Methode auf und übergibt sich dabei selbst.
   *
   * @param listener
   */
  @Override // AbstractModelComponent
  public void propertiesChanged(AttributesChangeListener listener) {
    for (ModelComponent model : getChildComponents()) {
      copyPropertiesToModel(model);
      model.propertiesChanged(listener);
    }

    extractSameProperties();
  }

  /**
   * Übernimmt alle Werte der hiesigen Attribute für das übergebene Modell.
   *
   * @param model
   */
  protected void copyPropertiesToModel(ModelComponent model) {
    for (String name : getProperties().keySet()) {
      Property property = getProperty(name);
      if (property.hasChanged()) {
        Property modelProperty = model.getProperty(name);
        modelProperty.copyFrom(property);
        modelProperty.markChanged();
      }
    }
  }

  @Override // AbstractModelComponent
  public String getDescription() {
    return bundle.getString("propertiesCollection.description");
  }
}
