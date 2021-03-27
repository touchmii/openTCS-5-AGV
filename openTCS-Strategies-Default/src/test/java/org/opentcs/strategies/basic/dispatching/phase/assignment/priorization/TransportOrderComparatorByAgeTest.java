/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.strategies.basic.dispatching.phase.assignment.priorization;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.theInstance;
import org.junit.*;
import static org.junit.Assert.assertThat;
import org.opentcs.data.order.TransportOrder;
import org.opentcs.strategies.basic.dispatching.priorization.transportorder.TransportOrderComparatorByAge;

/**
 *
 * @author Stefan Walter (Fraunhofer IML)
 */
public class TransportOrderComparatorByAgeTest {

  private TransportOrderComparatorByAge comparator;

  @Before
  public void setUp() {
    comparator = new TransportOrderComparatorByAge();
  }

  @Test
  public void sortOlderOrdersUp() {
    Instant creationTime = Instant.now();
    TransportOrder plainOrder = new TransportOrder("Some order", new ArrayList<>());
    TransportOrder order1 = plainOrder.withCreationTime(creationTime);
    TransportOrder order2 = plainOrder.withCreationTime(creationTime.plus(2, ChronoUnit.HOURS));
    TransportOrder order3 = plainOrder.withCreationTime(creationTime.plus(1, ChronoUnit.HOURS));

    List<TransportOrder> list = new ArrayList<>();
    list.add(order1);
    list.add(order2);
    list.add(order3);

    Collections.sort(list, comparator);

    assertThat(list.get(0), is(theInstance(order1)));
    assertThat(list.get(1), is(theInstance(order3)));
    assertThat(list.get(2), is(theInstance(order2)));
  }

}
