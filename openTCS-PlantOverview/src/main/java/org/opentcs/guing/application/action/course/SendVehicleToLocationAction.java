/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.guing.application.action.course;

import com.google.inject.assistedinject.Assisted;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import static java.util.Objects.requireNonNull;
import javax.inject.Inject;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import org.opentcs.customizations.plantoverview.ApplicationFrame;
import org.opentcs.data.order.OrderConstants;
import org.opentcs.guing.components.dialogs.StandardContentDialog;
import org.opentcs.guing.exchange.TransportOrderUtil;
import org.opentcs.guing.model.AbstractConnectableModelComponent;
import org.opentcs.guing.model.elements.LocationModel;
import org.opentcs.guing.model.elements.VehicleModel;
import org.opentcs.guing.persistence.ModelManager;
import org.opentcs.guing.transport.LocationActionPanel;
import static org.opentcs.guing.util.I18nPlantOverview.VEHICLEPOPUP_PATH;
import org.opentcs.guing.util.ResourceBundleUtil;

/**
 *
 * @author Stefan Walter (Fraunhofer IML)
 */
public class SendVehicleToLocationAction
    extends AbstractAction {

  /**
   * Sends a vehicle directly to a location.
   */
  public static final String ID = "course.vehicle.sendToLocation";

  private static final ResourceBundleUtil BUNDLE = ResourceBundleUtil.getBundle(VEHICLEPOPUP_PATH);
  /**
   * The vehicle.
   */
  private final VehicleModel fVehicle;
  /**
   * The application's main frame.
   */
  private final JFrame applicationFrame;
  /**
   * Provides the current system model.
   */
  private final ModelManager modelManager;
  /**
   * A helper for creating transport orders with the kernel.
   */
  private final TransportOrderUtil orderUtil;

  /**
   * Creates a new instance.
   *
   * @param vehicle The selected vehicle.
   * @param applicationFrame The application's main view.
   * @param modelManager Provides the current system model.
   * @param orderUtil A helper for creating transport orders with the kernel.
   */
  @Inject
  public SendVehicleToLocationAction(@Assisted VehicleModel vehicle,
                                     @ApplicationFrame JFrame applicationFrame,
                                     ModelManager modelManager,
                                     TransportOrderUtil orderUtil) {
    this.fVehicle = requireNonNull(vehicle, "vehicle");
    this.applicationFrame = requireNonNull(applicationFrame, "applicationFrame");
    this.modelManager = requireNonNull(modelManager, "modelManager");
    this.orderUtil = requireNonNull(orderUtil, "orderUtil");

    putValue(NAME, BUNDLE.getString("sendVehicleToLocationAction.name"));
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    List<LocationModel> locModels = locationModels();

    if (!locModels.isEmpty()) {
      LocationActionPanel contentPanel = new LocationActionPanel(locModels);
      StandardContentDialog fDialog = new StandardContentDialog(applicationFrame, contentPanel);
      fDialog.setTitle(evt.getActionCommand());
      fDialog.setVisible(true);

      if (fDialog.getReturnStatus() == StandardContentDialog.RET_OK) {
        LocationModel location = contentPanel.getSelectedLocation();
        List<AbstractConnectableModelComponent> destinationModels = new ArrayList<>();
        destinationModels.add(location);
        List<String> actions = new ArrayList<>();
        actions.add(contentPanel.getSelectedAction());
        orderUtil.createTransportOrder(destinationModels,
                                       actions,
                                       System.currentTimeMillis(),
                                       fVehicle,
                                       OrderConstants.TYPE_NONE);
      }
    }
  }

  private List<LocationModel> locationModels() {
    return modelManager.getModel().getLocationModels();
  }
}
