/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.guing.application.action.file;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Objects;
import static java.util.Objects.requireNonNull;
import javax.inject.Inject;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import org.opentcs.customizations.plantoverview.ApplicationFrame;
import org.opentcs.data.ObjectPropConstants;
import org.opentcs.guing.persistence.ModelManager;
import org.opentcs.guing.util.I18nPlantOverview;
import static org.opentcs.guing.util.I18nPlantOverview.MENU_PATH;
import org.opentcs.guing.util.ResourceBundleUtil;

/**
 * Shows a message window with some of the currently loaded model's properties.
 *
 * @author Stefan Walter (Fraunhofer IML)
 */
public class ModelPropertiesAction
    extends AbstractAction {

  /**
   * This action's ID.
   */
  public static final String ID = "file.modelProperties";
  
  private static final ResourceBundleUtil BUNDLE = ResourceBundleUtil.getBundle(MENU_PATH);
  /**
   * The parent component for dialogs shown by this action.
   */
  private final Component dialogParent;
  /**
   * Provides the current system model.
   */
  private final ModelManager modelManager;

  @Inject
  public ModelPropertiesAction(@ApplicationFrame Component dialogParent,
                               ModelManager modelManager) {
    this.dialogParent = requireNonNull(dialogParent, "dialogParent");
    this.modelManager = requireNonNull(modelManager, "modelManager");
    
    putValue(NAME, BUNDLE.getString("modelPropertiesAction.name"));
    putValue(SHORT_DESCRIPTION, BUNDLE.getString("modelPropertiesAction.shortDescription"));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    ResourceBundleUtil bundle = ResourceBundleUtil.getBundle(I18nPlantOverview.MODELPROPERTIES_PATH);

    JOptionPane.showMessageDialog(
        dialogParent,
        "<html><p><b>" + modelManager.getModel().getName() + "</b><br>"
        + bundle.getString("modelPropertiesAction.optionPane_properties.message.numberOfPoints") + numberOfPoints() + "<br>"
        + bundle.getString("modelPropertiesAction.optionPane_properties.message.numberOfPaths") + numberOfPaths() + "<br>"
        + bundle.getString("modelPropertiesAction.optionPane_properties.message.numberOfLocations") + numberOfLocations() + "<br>"
        + bundle.getString("modelPropertiesAction.optionPane_properties.message.numberOfLocationTypes") + numberOfLocationTypes() + "<br>"
        + bundle.getString("modelPropertiesAction.optionPane_properties.message.numberOfBlocks") + numberOfBlocks() + "<br>"
        + bundle.getString("modelPropertiesAction.optionPane_properties.message.numberOfVehicles") + numberOfVehicles() + "<br>"
        + "<br>"
        + bundle.getString("modelPropertiesAction.optionPane_properties.message.lastModified") + lastModified()
        + "</p></html>"
    );

  }

  private String lastModified() {
    return modelManager.getModel().getPropertyMiscellaneous().getItems().stream()
        .filter(kvp -> Objects.equals(kvp.getKey(), ObjectPropConstants.MODEL_FILE_LAST_MODIFIED))
        .findAny()
        .map(kvp -> kvp.getValue())
        .orElse("?");
  }

  private int numberOfPoints() {
    return modelManager.getModel().getPointModels().size();
  }

  private int numberOfPaths() {
    return modelManager.getModel().getPathModels().size();
  }

  private int numberOfLocations() {
    return modelManager.getModel().getLocationModels().size();
  }

  private int numberOfLocationTypes() {
    return modelManager.getModel().getLocationTypeModels().size();
  }

  private int numberOfBlocks() {
    return modelManager.getModel().getBlockModels().size();
  }

  private int numberOfVehicles() {
    return modelManager.getModel().getVehicleModels().size();
  }
}
