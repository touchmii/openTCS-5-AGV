/*
 * openTCS copyright information:
 * Copyright (c) 2005-2011 ifak e.V.
 * Copyright (c) 2012 Fraunhofer IML
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.guing.components.tree.elements;

import com.google.inject.assistedinject.Assisted;
import java.util.Objects;
import javax.inject.Inject;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import org.opentcs.guing.application.OpenTCSView;
import org.opentcs.guing.components.drawing.OpenTCSDrawingEditor;
import org.opentcs.guing.model.elements.LocationModel;
import org.opentcs.guing.persistence.ModelManager;
import org.opentcs.guing.util.IconToolkit;

/**
 * Die Repräsentation einer Station in der Baumansicht.
 *
 * @author Sebastian Naumann (ifak e.V. Magdeburg)
 */
public class LocationUserObject
    extends FigureUserObject
    implements ContextObject {

  private final UserObjectContext context;

  /**
   * Creates a new instance.
   *
   * @param model The corresponding model object
   * @param context The user object context
   * @param view The openTCS view
   * @param editor The drawing editor
   * @param modelManager The model manager
   */
  @Inject
  public LocationUserObject(@Assisted LocationModel model,
                            @Assisted UserObjectContext context,
                            OpenTCSView view,
                            OpenTCSDrawingEditor editor,
                            ModelManager modelManager) {
    super(model, view, editor, modelManager);
    this.context = Objects.requireNonNull(context, "context");
  }

  @Override
  public LocationModel getModelComponent() {
    return (LocationModel) super.getModelComponent();
  }

  @Override // AbstractUserObject
  public JPopupMenu getPopupMenu() {
    JPopupMenu menu = context.getPopupMenu(userObjectItems);

    return menu;
  }

  @Override // FigureUserObject
  public ImageIcon getIcon() {
    return IconToolkit.instance().createImageIcon("tree/location.18x18.png");
  }

  @Override
  public boolean removed() {
    return context.removed(this);
  }

  @Override
  public UserObjectContext.ContextType getContextType() {
    return context.getType();
  }
}
