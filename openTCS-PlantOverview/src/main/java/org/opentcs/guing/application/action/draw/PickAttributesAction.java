/**
 * (c): IML, JHotDraw.
 *
 * Changed by IML to allow access to ResourceBundle.
 *
 *
 * @(#)PickAttributesAction.java
 *
 * Copyright (c) 1996-2010 by the original authors of JHotDraw and all its
 * contributors. All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with the copyright holders. For details
 * see accompanying license terms.
 */
package org.opentcs.guing.application.action.draw;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import static javax.swing.Action.LARGE_ICON_KEY;
import static javax.swing.Action.SMALL_ICON;
import javax.swing.ImageIcon;
import org.jhotdraw.draw.AttributeKey;
import static org.jhotdraw.draw.AttributeKeys.TEXT;
import static org.jhotdraw.draw.AttributeKeys.TRANSFORM;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.action.AbstractSelectedAction;
import org.jhotdraw.draw.event.FigureSelectionEvent;
import static org.opentcs.guing.util.I18nPlantOverview.TOOLBAR_PATH;
import org.opentcs.guing.util.ImageDirectory;
import org.opentcs.guing.util.ResourceBundleUtil;

/**
 * PickAttributesAction.
 *
 * @author Werner Randelshofer
 */
public class PickAttributesAction
    extends AbstractSelectedAction {

  private static final ResourceBundleUtil BUNDLE = ResourceBundleUtil.getBundle(TOOLBAR_PATH);

  private Set<AttributeKey> excludedAttributes = new HashSet<>(
      Arrays.asList(new AttributeKey[] {TRANSFORM, TEXT}));

  /**
   * Creates a new instance.
   *
   * @param editor The drawing editor
   */
  public PickAttributesAction(DrawingEditor editor) {
    super(editor);
    putValue(NAME, BUNDLE.getString("pickAttributesAction.name"));
    putValue(SHORT_DESCRIPTION, BUNDLE.getString("pickAttributesAction.shortDescription"));

    ImageIcon icon = ImageDirectory.getImageIcon("/toolbar/colorpicker.png");
    putValue(SMALL_ICON, icon);
    putValue(LARGE_ICON_KEY, icon);

    updateEnabledState();
  }

  /**
   * Set of attributes that is excluded when applying default attributes. By
   * default, the TRANSFORM attribute is excluded.
   */
  public void setExcludedAttributes(Set<AttributeKey> a) {
    this.excludedAttributes = a;
  }

  @Override
  public void actionPerformed(java.awt.event.ActionEvent e) {
    pickAttributes();
  }

  @SuppressWarnings("unchecked")
  public void pickAttributes() {
    DrawingEditor editor = getEditor();
    Collection<Figure> selection = getView().getSelectedFigures();

    if (selection.size() > 0) {
      Figure figure = selection.iterator().next();

      for (Map.Entry<AttributeKey, Object> entry : figure.getAttributes().entrySet()) {
        if (!excludedAttributes.contains(entry.getKey())) {
          editor.setDefaultAttribute(entry.getKey(), entry.getValue());
        }
      }
    }
  }

  public void selectionChanged(FigureSelectionEvent evt) {
    setEnabled(getView().getSelectionCount() == 1);
  }
}
