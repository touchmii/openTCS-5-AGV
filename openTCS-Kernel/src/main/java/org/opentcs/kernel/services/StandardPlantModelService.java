/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.kernel.services;

import static com.google.common.base.Strings.isNullOrEmpty;
import java.util.Map;
import static java.util.Objects.requireNonNull;
import java.util.Set;
import javax.inject.Inject;
import org.opentcs.access.Kernel;
import org.opentcs.access.KernelRuntimeException;
import org.opentcs.access.LocalKernel;
import org.opentcs.access.ModelTransitionEvent;
import org.opentcs.access.to.model.PlantModelCreationTO;
import org.opentcs.components.kernel.services.InternalPlantModelService;
import org.opentcs.components.kernel.services.NotificationService;
import org.opentcs.components.kernel.services.PlantModelService;
import org.opentcs.components.kernel.services.TCSObjectService;
import org.opentcs.customizations.ApplicationEventBus;
import org.opentcs.customizations.kernel.GlobalSyncObject;
import org.opentcs.data.ObjectExistsException;
import org.opentcs.data.ObjectUnknownException;
import org.opentcs.data.model.TCSResource;
import org.opentcs.data.model.TCSResourceReference;
import org.opentcs.data.notification.UserNotification;
import org.opentcs.kernel.persistence.ModelPersister;
import org.opentcs.kernel.workingset.Model;
import org.opentcs.util.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the standard implementation of the {@link PlantModelService} interface.
 *
 * @author Martin Grzenia (Fraunhofer IML)
 */
public class StandardPlantModelService
    extends AbstractTCSObjectService
    implements InternalPlantModelService {

  /**
   * This class' logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(StandardPlantModelService.class);
  /**
   * The kernel.
   */
  private final Kernel kernel;
  /**
   * A global object to be used for synchronization within the kernel.
   */
  private final Object globalSyncObject;
  /**
   * The model facade to the object pool.
   */
  private final Model model;
  /**
   * The persister loading and storing model data.
   */
  private final ModelPersister modelPersister;
  /**
   * Where we send events to.
   */
  private final EventHandler eventHandler;
  /**
   * The notification service.
   */
  private final NotificationService notificationService;

  /**
   * Creates a new instance.
   *
   * @param kernel The kernel.
   * @param objectService The tcs object service.
   * @param globalSyncObject The kernel threads' global synchronization object.
   * @param model The model to be used.
   * @param modelPersister The model persister to be used.
   * @param eventHandler Where this instance sends events to.
   * @param notificationService The notification service.
   */
  @Inject
  public StandardPlantModelService(LocalKernel kernel,
                                   TCSObjectService objectService,
                                   @GlobalSyncObject Object globalSyncObject,
                                   Model model,
                                   ModelPersister modelPersister,
                                   @ApplicationEventBus EventHandler eventHandler,
                                   NotificationService notificationService) {
    super(objectService);
    this.kernel = requireNonNull(kernel, "kernel");
    this.globalSyncObject = requireNonNull(globalSyncObject, "globalSyncObject");
    this.model = requireNonNull(model, "model");
    this.modelPersister = requireNonNull(modelPersister, "modelPersister");
    this.eventHandler = requireNonNull(eventHandler, "eventHandler");
    this.notificationService = requireNonNull(notificationService, "notificationService");
  }

  @Override
  public Set<TCSResource<?>> expandResources(Set<TCSResourceReference<?>> resources)
      throws ObjectUnknownException {
    synchronized (globalSyncObject) {
      return model.expandResources(resources);
    }
  }

  @Override
  public void loadPlantModel()
      throws IllegalStateException {
    synchronized (globalSyncObject) {
      if (!modelPersister.hasSavedModel()) {
        createPlantModel(new PlantModelCreationTO(Kernel.DEFAULT_MODEL_NAME));
        return;
      }

      final String oldModelName = getModelName();
      // Load the new model
      PlantModelCreationTO modelCreationTO = modelPersister.readModel();
      final String newModelName = isNullOrEmpty(modelCreationTO.getName())
          ? ""
          : modelCreationTO.getName();
      // Let listeners know we're in transition.
      emitModelEvent(oldModelName, newModelName, true, false);
      model.createPlantModelObjects(modelCreationTO);
      // Let listeners know we're done with the transition.
      emitModelEvent(oldModelName, newModelName, true, true);
      notificationService.publishUserNotification(
          new UserNotification("Kernel loaded model " + newModelName,
                               UserNotification.Level.INFORMATIONAL));
    }
  }

  @Override
  public void savePlantModel()
      throws IllegalStateException {
    synchronized (globalSyncObject) {
      modelPersister.saveModel(model.createPlantModelCreationTO());
    }
  }

  @Override
  public void createPlantModel(PlantModelCreationTO to)
      throws ObjectUnknownException, ObjectExistsException, IllegalStateException {

    boolean kernelInOperating = kernel.getState() == Kernel.State.OPERATING;
    // If we are in state operating, change the kernel state before creating the plant model
    if (kernelInOperating) {
      kernel.setState(Kernel.State.MODELLING);
    }

    String oldModelName = getModelName();
    emitModelEvent(oldModelName, to.getName(), true, false);

    // Create the plant model
    synchronized (globalSyncObject) {
      model.createPlantModelObjects(to);
    }

    savePlantModel();

    // If we were in state operating before, change the kernel state back to operating
    if (kernelInOperating) {
      kernel.setState(Kernel.State.OPERATING);
    }

    emitModelEvent(oldModelName, to.getName(), true, true);
    notificationService.publishUserNotification(
        new UserNotification("Kernel created model " + to.getName(),
                             UserNotification.Level.INFORMATIONAL));
  }

  @Override
  public String getModelName() {
    synchronized (globalSyncObject) {
      return model.getName();
    }
  }

  @Override
  public Map<String, String> getModelProperties()
      throws KernelRuntimeException {
    synchronized (globalSyncObject) {
      return model.getProperties();
    }
  }

  /**
   * Generates an event for a Model change.
   *
   * @param oldModelName The old model name.
   * @param newModelName The new model name.
   * @param modelContentChanged Whether the model's content actually changed.
   * @param transitionFinished Whether the transition is finished or not.
   */
  private void emitModelEvent(String oldModelName,
                              String newModelName,
                              boolean modelContentChanged,
                              boolean transitionFinished) {
    requireNonNull(newModelName, "newModelName");

    eventHandler.onEvent(new ModelTransitionEvent(oldModelName,
                                                  newModelName,
                                                  modelContentChanged,
                                                  transitionFinished));
  }
}
