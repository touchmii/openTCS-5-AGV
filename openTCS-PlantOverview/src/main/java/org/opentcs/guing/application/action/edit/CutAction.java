/**
 * (c): IML, JHotDraw.
 *
 */
package org.opentcs.guing.application.action.edit;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import static javax.swing.Action.ACCELERATOR_KEY;
import static javax.swing.Action.LARGE_ICON_KEY;
import static javax.swing.Action.SMALL_ICON;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import org.jhotdraw.app.action.edit.AbstractSelectionAction;
import org.opentcs.guing.components.EditableComponent;
import static org.opentcs.guing.util.I18nPlantOverview.MENU_PATH;
import org.opentcs.guing.util.ImageDirectory;
import org.opentcs.guing.util.ResourceBundleUtil;

/**
 * Cuts the selected region and places its contents into the system clipboard.
 * This action acts on the last EditableComponent / {@code JTextComponent}
 * which had the focus when the {@code ActionEvent} was generated.
 * This action is called when the user selects the "Cut" item in
 * the Edit menu. The menu item is automatically created by the application.
 *
 * @author Werner Randelshofer
 */
public class CutAction
    extends AbstractSelectionAction {

  public final static String ID = "edit.cut";

  private static final ResourceBundleUtil BUNDLE = ResourceBundleUtil.getBundle(MENU_PATH);

  /**
   * Creates a new instance which acts on the currently focused component.
   */
  public CutAction() {
    this(null);
  }

  /**
   * Creates a new instance which acts on the specified component.
   *
   * @param target The target of the action. Specify null for the currently
   * focused component.
   */
  public CutAction(JComponent target) {
    super(target);

    putValue(NAME, BUNDLE.getString("cutAction.name"));
    putValue(SHORT_DESCRIPTION, BUNDLE.getString("cutAction.shortDescription"));
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl X"));

    ImageIcon icon = ImageDirectory.getImageIcon("/menu/edit-cut-4.png");
    putValue(SMALL_ICON, icon);
    putValue(LARGE_ICON_KEY, icon);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    Component cFocusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();

    if (cFocusOwner instanceof JComponent) {
      if (cFocusOwner.isEnabled()) {
        // Cut all selected UserObjects from the tree
        if (cFocusOwner instanceof EditableComponent) {
          ((EditableComponent) cFocusOwner).cutSelectedItems();
        }
      }
    }

    // "Old" version with JHotDraw clipboard
//    JComponent cTarget = target;
//
//    if (cTarget == null && (cFocusOwner instanceof JComponent)) {
//      cTarget = (JComponent) cFocusOwner;
//    }
//
//    if (cTarget != null && cTarget.isEnabled() && cTarget.getTransferHandler() != null) {
//      cTarget.getTransferHandler().exportToClipboard(cTarget, ClipboardUtil.getClipboard(), TransferHandler.MOVE);
//    }
  }
}
