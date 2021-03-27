/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.guing.application.action.synchronize;

import com.google.inject.Inject;
import java.awt.event.ActionEvent;
import static java.util.Objects.requireNonNull;
import javax.swing.AbstractAction;
import static javax.swing.Action.ACCELERATOR_KEY;
import static javax.swing.Action.MNEMONIC_KEY;
import javax.swing.KeyStroke;
import org.opentcs.guing.application.OpenTCSView;
import org.opentcs.guing.application.OperationMode;
import static org.opentcs.guing.util.I18nPlantOverview.MENU_PATH;
import org.opentcs.guing.util.ResourceBundleUtil;

/**
 * An action to switch to operating mode.
 *
 * @author Mustafa Yalciner (Fraunhofer IML)
 */
public class SwitchToOperatingAction
    extends AbstractAction {

  /**
   * The id of this action. (Often used to map a key combination in the ressources.)
   */
  public static final String ID = "file.mode.switchToOperating";

  private static final ResourceBundleUtil BUNDLE = ResourceBundleUtil.getBundle(MENU_PATH);

  /**
   * The view.
   */
  private final OpenTCSView view;

  @Inject
  public SwitchToOperatingAction(OpenTCSView view) {
    this.view = requireNonNull(view, "view");

    putValue(NAME, BUNDLE.getString("switchToOperatingAction.name"));
    putValue(SHORT_DESCRIPTION, BUNDLE.getString("switchToOperatingAction.shortDescription"));
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("alt O"));
    putValue(MNEMONIC_KEY, Integer.valueOf('O'));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    view.switchPlantOverviewState(OperationMode.OPERATING);
  }
}
