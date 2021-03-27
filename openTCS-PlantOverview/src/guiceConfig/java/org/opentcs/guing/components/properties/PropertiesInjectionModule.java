/*
 * openTCS copyright information:
 * Copyright (c) 2014 Fraunhofer IML
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.guing.components.properties;

import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.MapBinder;
import javax.inject.Singleton;
import org.opentcs.customizations.plantoverview.PlantOverviewInjectionModule;
import org.opentcs.guing.components.dialogs.DetailsDialogContent;
import org.opentcs.guing.components.properties.panel.KeyValuePropertyEditorPanel;
import org.opentcs.guing.components.properties.panel.KeyValueSetPropertyEditorPanel;
import org.opentcs.guing.components.properties.panel.LinkActionsEditorPanel;
import org.opentcs.guing.components.properties.panel.LocationTypeActionsEditorPanel;
import org.opentcs.guing.components.properties.panel.OrderTypesPropertyEditorPanel;
import org.opentcs.guing.components.properties.panel.PropertiesPanelFactory;
import org.opentcs.guing.components.properties.panel.SymbolPropertyEditorPanel;
import org.opentcs.guing.components.properties.table.CellEditorFactory;
import org.opentcs.guing.components.properties.type.AbstractComplexProperty;
import org.opentcs.guing.components.properties.type.KeyValueProperty;
import org.opentcs.guing.components.properties.type.KeyValueSetProperty;
import org.opentcs.guing.components.properties.type.LinkActionsProperty;
import org.opentcs.guing.components.properties.type.LocationTypeActionsProperty;
import org.opentcs.guing.components.properties.type.OrderTypesProperty;
import org.opentcs.guing.components.properties.type.SymbolProperty;

/**
 * A Guice module for this package.
 *
 * @author Stefan Walter (Fraunhofer IML)
 */
public class PropertiesInjectionModule
    extends PlantOverviewInjectionModule {

  @Override
  protected void configure() {

    install(new FactoryModuleBuilder().build(PropertiesPanelFactory.class));
    install(new FactoryModuleBuilder().build(CellEditorFactory.class));
    install(new FactoryModuleBuilder().build(PropertiesComponentsFactory.class));

    MapBinder<Class<? extends AbstractComplexProperty>, DetailsDialogContent> dialogContentMapBinder
        = MapBinder.newMapBinder(binder(),
                                 new TypeLiteral<Class<? extends AbstractComplexProperty>>() {
                             },
                                 new TypeLiteral<DetailsDialogContent>() {
                             });
    dialogContentMapBinder
        .addBinding(KeyValueProperty.class)
        .to(KeyValuePropertyEditorPanel.class);
    dialogContentMapBinder
        .addBinding(KeyValueSetProperty.class)
        .to(KeyValueSetPropertyEditorPanel.class);
    dialogContentMapBinder
        .addBinding(LocationTypeActionsProperty.class)
        .to(LocationTypeActionsEditorPanel.class);
    dialogContentMapBinder
        .addBinding(LinkActionsProperty.class)
        .to(LinkActionsEditorPanel.class);
    dialogContentMapBinder
        .addBinding(SymbolProperty.class)
        .to(SymbolPropertyEditorPanel.class);
    dialogContentMapBinder
        .addBinding(OrderTypesProperty.class)
        .to(OrderTypesPropertyEditorPanel.class);

    bind(SelectionPropertiesComponent.class)
        .in(Singleton.class);

  }

}
