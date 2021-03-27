/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.util.persistence;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.junit.*;
import static org.junit.Assert.assertTrue;
import org.opentcs.util.persistence.v003.AllowedOperationTO;
import org.opentcs.util.persistence.v003.BlockTO;
import org.opentcs.util.persistence.v003.GroupTO;
import org.opentcs.util.persistence.v003.LocationTO;
import org.opentcs.util.persistence.v003.LocationTO.Link;
import org.opentcs.util.persistence.v003.LocationTypeTO;
import org.opentcs.util.persistence.v003.MemberTO;
import org.opentcs.util.persistence.v003.PathTO;
import org.opentcs.util.persistence.v003.V003PlantModelTO;
import org.opentcs.util.persistence.v003.PointTO;
import org.opentcs.util.persistence.v003.PointTO.OutgoingPath;
import org.opentcs.util.persistence.v003.PropertyTO;
import org.opentcs.util.persistence.v003.VehicleTO;
import org.opentcs.util.persistence.v003.VisualLayoutTO;
import org.opentcs.util.persistence.v003.VisualLayoutTO.ModelLayoutElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author Martin Grzenia (Fraunhofer IML)
 */
public class V003DrivingCoursePersistenceTest {

  private static final Logger LOG = LoggerFactory.getLogger(V003DrivingCoursePersistenceTest.class);

  public V003DrivingCoursePersistenceTest() {
  }

  @Before
  public void setUp() {
  }

  @After
  public void tearDown() {

  }

  @Test
  public void testToXml()
      throws IOException {
    V003PlantModelTO model = new V003PlantModelTO();
    model.setVersion("0.0.3");
    model.setName("Demo");

    PointTO point1 = createPoint("Point1", 1, 2, 3, Float.NaN, "HALT_POSITION",
                                 Arrays.asList(createOutgoingPath("Path1")),
                                 new ArrayList<>());
    PointTO point2 = createPoint("Point2", 4, 5, 6, Float.NaN, "PARK_POSITION",
                                 Arrays.asList(createOutgoingPath("Path2"),
                                               createOutgoingPath("Path3")),
                                 new ArrayList<>());
    model.setPoints(Arrays.asList(point1, point2));

    PathTO path1 = createPath();
    model.setPaths(Arrays.asList(path1));

    VehicleTO vehicle = createVehicle();
    model.setVehicles(Arrays.asList(vehicle));

    LocationTypeTO locType = createLocationType();

    model.setLocationTypes(Arrays.asList(locType));

    LocationTO loc = createLocation();
    model.setLocations(Arrays.asList(loc));

    BlockTO block = createBlock();
    model.setBlocks(Arrays.asList(block));

    GroupTO group = createGroup();
    model.setGroups(Arrays.asList(group));

    VisualLayoutTO vl = createVisualLayout();
    model.setVisualLayouts(Arrays.asList(vl));

    StringWriter writer = new StringWriter();
    model.toXml(writer);

    String xmlOutput = writer.toString();
    LOG.info(xmlOutput);

    try {
      assertTrue(validateXml(new StringReader(xmlOutput)));
    }
    catch (SAXException | IOException ex) {
      LOG.error("", ex);
    }
  }

  private boolean validateXml(Reader reader)
      throws SAXException, IOException {
    Source schemaFile = new StreamSource(getClass().getResourceAsStream(
        "/org/opentcs/util/persistence/model-0.0.3.xsd"));
    SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Schema schema = schemaFactory.newSchema(schemaFile);
    try {
      schema.newValidator().validate(new StreamSource(reader));
      LOG.info("XML is valid.");
      return true;
    }
    catch (SAXException e) {
      LOG.info("XML is NOT valid.", e);
      return false;
    }
  }

  private PointTO createPoint(String name, long xPosition, long yPosition, long zPosition,
                              Float vehicleOrientationAngle, String type,
                              List<OutgoingPath> outgoingPaths,
                              List<PropertyTO> properties) {
    PointTO p = new PointTO();
    p.setName(name);
    p.setxPosition(xPosition);
    p.setyPosition(yPosition);
    p.setzPosition(zPosition);
    p.setVehicleOrientationAngle(vehicleOrientationAngle);
    p.setType(type);
    p.setOutgoingPaths(outgoingPaths);
    p.setProperties(properties);
    return p;
  }

  private OutgoingPath createOutgoingPath(String name) {
    OutgoingPath op = new OutgoingPath();
    op.setName(name);
    return op;
  }

  private PathTO createPath() {
    PathTO p = new PathTO();
    p.setName("Point1 --- Point2");
    p.setSourcePoint("Point1");
    p.setDestinationPoint("Point2");
    p.setLength(100L);
    p.setMaxVelocity(1000L);
    p.setMaxReverseVelocity(200L);
    p.setLocked(false);

    PropertyTO property = new PropertyTO();
    property.setName("Property1");
    property.setValue("Value1");
    p.setProperties(Arrays.asList(property));
    return p;
  }

  private LocationTypeTO createLocationType() {
    LocationTypeTO locType = new LocationTypeTO();

    locType.setName("Transfer station");

    AllowedOperationTO ao1 = new AllowedOperationTO();
    ao1.setName("NOP");
    AllowedOperationTO ao2 = new AllowedOperationTO();
    ao2.setName("Load cargo");
    locType.setAllowedOperations(Arrays.asList(ao1, ao2));

    PropertyTO property = new PropertyTO();
    property.setName("tcs:defaultLocationSymbol");
    property.setValue("LOAD_TRANSFER_GENERIC");
    locType.setProperties(Arrays.asList(property));
    return locType;
  }

  private LocationTO createLocation() {
    LocationTO loc = new LocationTO();

    loc.setName("Storage 02");
    loc.setxPosition(100L);
    loc.setyPosition(200L);
    loc.setzPosition(300L);
    loc.setType("Transfer station");

    Link link = new Link();
    link.setPoint("Point1");
    loc.setLinks(Arrays.asList(link));

    return loc;
  }

  private BlockTO createBlock() {
    BlockTO block = new BlockTO();
    block.setName("Block-001");

    MemberTO member = new MemberTO();
    member.setName("Point1 --- Point2");
    block.setMembers(Arrays.asList(member));

    return block;
  }

  private VisualLayoutTO createVisualLayout() {
    VisualLayoutTO vl = new VisualLayoutTO();
    vl.setName("VLayout-01");
    vl.setScaleX(50.0f);
    vl.setScaleY(50.0f);

    ModelLayoutElement mle = new ModelLayoutElement();
    mle.setVisualizedObjectName("Block-0002");
    mle.setLayer(0L);
    PropertyTO property = new PropertyTO();
    property.setName("COLOR");
    property.setValue("#FF0000");
    mle.setProperties(Arrays.asList(property));
    vl.setModelLayoutElements(Arrays.asList(mle));

    property = new PropertyTO();
    property.setName("tcs:vehicleThemeClass");
    property.setValue("...");
    vl.setProperties(Arrays.asList(property));

    return vl;
  }

  private VehicleTO createVehicle() {
    VehicleTO vehicle = new VehicleTO();
    vehicle.setName("Vehicle-01");
    vehicle.setLength(1000L);
    vehicle.setEnergyLevelCritical(30L);
    vehicle.setEnergyLevelGood(90L);
    return vehicle;
  }

  private GroupTO createGroup() {
    GroupTO group = new GroupTO();
    group.setName("Group1");
    MemberTO member = new MemberTO();
    member.setName("Member1");
    group.setMembers(Arrays.asList(member));
    return group;
  }
}
