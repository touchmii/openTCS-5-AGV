/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.kernel.extensions.controlcenter.vehicles;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import static org.hamcrest.Matchers.is;
import org.junit.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.opentcs.components.kernel.services.TCSObjectService;
import org.opentcs.data.model.Vehicle;
import org.opentcs.drivers.vehicle.BasicVehicleCommAdapter;
import org.opentcs.drivers.vehicle.DefaultVehicleCommAdapterDescription;
import org.opentcs.drivers.vehicle.MovementCommand;
import org.opentcs.drivers.vehicle.VehicleCommAdapter;
import org.opentcs.drivers.vehicle.VehicleCommAdapterDescription;
import org.opentcs.drivers.vehicle.VehicleCommAdapterFactory;
import org.opentcs.drivers.vehicle.VehicleProcessModel;
import org.opentcs.kernel.KernelApplicationConfiguration;
import org.opentcs.kernel.vehicles.LocalVehicleControllerPool;
import org.opentcs.kernel.vehicles.VehicleCommAdapterRegistry;
import org.opentcs.util.ExplainedBoolean;
import org.opentcs.util.event.EventHandler;
import org.slf4j.LoggerFactory;

/**
 * Tests for the {@link AttachmentManager}.
 *
 * @author Martin Grzenia (Fraunhofer IML)
 */
public class AttachmentManagerTest {

  private final AttachmentManager attachmentManager;
  private final TCSObjectService objectService;
  private final LocalVehicleControllerPool vehicleControllerPool;
  private final VehicleCommAdapterRegistry commAdapterRegistry;
  private final VehicleEntryPool vehicleEntryPool;
  private final VehicleCommAdapterFactory commAdapterFactory;
  private final EventHandler eventHandler;

  private static final String VEHICLE_1_NAME = "Vehicle1";
  private static final String VEHICLE_2_NAME = "Vehicle2";
  private final Vehicle vehicle1;
  private final Vehicle vehicle2;

  public AttachmentManagerTest() {
    objectService = mock(TCSObjectService.class);
    vehicleControllerPool = mock(LocalVehicleControllerPool.class);
    commAdapterRegistry = mock(VehicleCommAdapterRegistry.class);
    commAdapterFactory = mock(VehicleCommAdapterFactory.class);
    vehicleEntryPool = new VehicleEntryPool(objectService);
    eventHandler = mock(EventHandler.class);
    attachmentManager = spy(new AttachmentManager(objectService,
                                                  vehicleControllerPool,
                                                  commAdapterRegistry,
                                                  vehicleEntryPool,
                                                  eventHandler,
                                                  mock(KernelApplicationConfiguration.class)));

    vehicle1 = new Vehicle(VEHICLE_1_NAME);
    vehicle2 = new Vehicle(VEHICLE_2_NAME)
        .withProperty(Vehicle.PREFERRED_ADAPTER,
                      SimpleCommAdapterFactory.class.getName());
  }

  @Before
  public void setUp() {
    Set<Vehicle> vehicles = new HashSet<>();
    vehicles.add(vehicle1);
    vehicles.add(vehicle2);
    when(objectService.fetchObjects(Vehicle.class)).thenReturn(vehicles);
    attachmentManager.initialize();
    for (VehicleEntry entry : vehicleEntryPool.getEntries().values()) {
      LoggerFactory.getLogger(getClass()).info("{}", entry.getVehicle());
    }
  }

  @After
  public void tearDown() {
    attachmentManager.terminate();
  }

  @Test
  public void shouldNotAttachUnknownVehicle() {
    attachmentManager.attachAdapterToVehicle("UnknownVehicle", commAdapterFactory);

    verify(commAdapterFactory, times(0)).getAdapterFor(any(Vehicle.class));
    verify(vehicleControllerPool, times(0)).attachVehicleController(any(String.class),
                                                                    any(VehicleCommAdapter.class));
  }

  @Test
  public void shouldAttachAdapterToVehicle() {
    VehicleCommAdapter commAdapter = new SimpleCommAdapter(vehicle1);
    when(commAdapterFactory.getAdapterFor(vehicle1)).thenReturn(commAdapter);
    when(commAdapterFactory.getDescription()).thenReturn(new SimpleVehicleCommAdapterDescription());

    attachmentManager.attachAdapterToVehicle(VEHICLE_1_NAME, commAdapterFactory);

    verify(vehicleControllerPool, times(1)).detachVehicleController(VEHICLE_1_NAME);
    verify(vehicleControllerPool, times(1)).attachVehicleController(VEHICLE_1_NAME, commAdapter);
    assertNotNull(vehicleEntryPool.getEntryFor(VEHICLE_1_NAME));
    assertThat(vehicleEntryPool.getEntryFor(VEHICLE_1_NAME).getCommAdapter(),
               is(commAdapter));
    assertThat(vehicleEntryPool.getEntryFor(VEHICLE_1_NAME).getCommAdapterFactory(),
               is(commAdapterFactory));
    assertThat(vehicleEntryPool.getEntryFor(VEHICLE_1_NAME).getProcessModel(),
               is(commAdapter.getProcessModel()));
  }

  @Test
  public void shouldAutoAttachAdapterToVehicle() {
    List<VehicleCommAdapterFactory> factories = Arrays.asList(new NullVehicleCommAdapterFactory(),
                                                              new SimpleCommAdapterFactory());
    when(commAdapterRegistry.getFactories()).thenReturn(factories);

    attachmentManager.autoAttachAdapterToVehicle(VEHICLE_2_NAME);

    verify(attachmentManager, times(1)).attachAdapterToVehicle(VEHICLE_2_NAME, factories.get(1));
  }

  @Test
  public void shouldAutoAttachToFirstAvailableAdapter() {
    List<VehicleCommAdapterFactory> factories = Arrays.asList(new SimpleCommAdapterFactory(),
                                                              new NullVehicleCommAdapterFactory());
    when(commAdapterRegistry.getFactories()).thenReturn(factories);
    when(commAdapterRegistry.findFactoriesFor(vehicle1)).thenReturn(factories);

    attachmentManager.autoAttachAdapterToVehicle(VEHICLE_1_NAME);

    verify(attachmentManager, times(1)).attachAdapterToVehicle(VEHICLE_1_NAME, factories.get(0));
  }

  private class SimpleCommAdapter
      extends BasicVehicleCommAdapter {

    public SimpleCommAdapter(Vehicle vehicle) {
      super(new VehicleProcessModel(vehicle), 0, 0, "", Executors.newSingleThreadExecutor());
    }

    @Override
    public void sendCommand(MovementCommand cmd)
        throws IllegalArgumentException {
    }

    @Override
    protected void connectVehicle() {
    }

    @Override
    protected void disconnectVehicle() {
    }

    @Override
    protected boolean isVehicleConnected() {
      return true;
    }

    @Override
    public ExplainedBoolean canProcess(List<String> list) {
      return new ExplainedBoolean(true, "");
    }

    @Override
    public void processMessage(Object o) {
    }
  }

  private class SimpleCommAdapterFactory
      implements VehicleCommAdapterFactory {

    @Override
    public boolean providesAdapterFor(Vehicle vehicle) {
      return (vehicle.equals(vehicle1) || vehicle.equals(vehicle2));
    }

    @Override
    public VehicleCommAdapter getAdapterFor(Vehicle vehicle) {
      if (vehicle.equals(vehicle1) || vehicle.equals(vehicle2)) {
        return new SimpleCommAdapter(vehicle);
      }
      else {
        return null;
      }
    }

    @Override
    public VehicleCommAdapterDescription getDescription() {
      return new DefaultVehicleCommAdapterDescription("simpleCommAdapter", false);
    }

    @Override
    public void initialize() {
    }

    @Override
    public boolean isInitialized() {
      return true;
    }

    @Override
    public void terminate() {
    }
  }

  private class SimpleVehicleCommAdapterDescription
      extends VehicleCommAdapterDescription {

    @Override
    public String getDescription() {
      return getClass().getName();
    }

    @Override
    public boolean isSimVehicleCommAdapter() {
      return false;
    }
  }
}
