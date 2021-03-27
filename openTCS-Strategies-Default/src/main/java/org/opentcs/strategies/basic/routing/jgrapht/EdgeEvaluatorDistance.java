/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.strategies.basic.routing.jgrapht;

import org.opentcs.data.model.Vehicle;

/**
 * Uses an edge's length as its weight.
 *
 * @author Stefan Walter (Fraunhofer IML)
 */
public class EdgeEvaluatorDistance
    implements EdgeEvaluator {

  public EdgeEvaluatorDistance() {
  }

  @Override
  public double computeWeight(ModelEdge edge, Vehicle vehicle) {
    return edge.getModelPath().getLength();
  }
}
