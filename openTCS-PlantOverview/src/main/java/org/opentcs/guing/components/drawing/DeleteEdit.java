/*
 * openTCS copyright information:
 * Copyright (c) 2012 Fraunhofer IML
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.guing.components.drawing;

import java.util.ArrayList;
import java.util.List;
import static java.util.Objects.requireNonNull;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.jhotdraw.draw.Figure;
import org.opentcs.guing.util.I18nPlantOverview;
import org.opentcs.guing.util.ResourceBundleUtil;

/**
 * Undoes or redoes the "delete" action.
 */
class DeleteEdit
    extends AbstractUndoableEdit {

  /**
   * The drawing view we're working with.
   */
  private final OpenTCSDrawingView drawingView;
  /**
   * The deleted figures.
   */
  private final ArrayList<Figure> figures = new ArrayList<>();

  /**
   * Creates a new instance.
   *
   * @param drawingView The drawing view the delete happened in.
   * @param figures The deleted figures.
   */
  DeleteEdit(OpenTCSDrawingView drawingView, List<Figure> figures) {
    this.drawingView = requireNonNull(drawingView, "drawingView");
    this.figures.addAll(figures);
  }

  @Override
  public String getPresentationName() {
    return ResourceBundleUtil.getBundle(I18nPlantOverview.MENU_PATH).getString("deleteEdit.presentationName");
  }

  @Override
  public void undo() throws CannotUndoException {
    super.undo();
    drawingView.clearSelection();
    for (Figure figure : figures) {
      drawingView.getDrawing().add(figure);
    }
  }

  @Override
  public void redo() throws CannotRedoException {
    super.redo();
    for (Figure figure : figures) {
      drawingView.getDrawing().remove(figure);
    }
  }
}
