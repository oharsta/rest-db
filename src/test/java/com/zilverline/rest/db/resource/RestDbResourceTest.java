package com.zilverline.rest.db.resource;

import com.googlecode.flyway.core.Flyway;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.sql.DataSource;
import javax.ws.rs.core.UriInfo;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RestDbResourceTest {

  public static final String HOUSES = "HOUSES";

  private RestDbResource resource;

  private final ObjectMapper objectMapper = new ObjectMapper().enable(
      DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY).setSerializationInclusion(
      JsonSerialize.Inclusion.NON_NULL);

  @Test
  public void testMeta() throws Exception {
    String meta = this.objectMapper.writeValueAsString(resource.meta().getEntity());
    assertTrue(meta.contains(HOUSES));
  }

  @Test
  public void testGetAll() throws Exception {
    UriInfo uriInfo = mock(UriInfo.class);
    when(uriInfo.getQueryParameters()).thenReturn(new MultivaluedMapImpl());
    List entity = (List) resource.getAll(HOUSES, uriInfo).getEntity();
    assertEquals(2, entity.size());

    String houses = this.objectMapper.writeValueAsString(entity);
    assertTrue(houses.contains("some desc"));
    assertTrue(houses.contains("some other desc"));
  }

  @Test
  public void testGetWithWhereClause() throws Exception {
    UriInfo uriInfo = mock(UriInfo.class);
    MultivaluedMapImpl queryParameters = new MultivaluedMapImpl();
    queryParameters.add("description", "some other desc");
    when(uriInfo.getQueryParameters()).thenReturn(queryParameters);
    List entity = (List) resource.getAll(HOUSES, uriInfo).getEntity();
    assertEquals(1, entity.size());

    String houses = this.objectMapper.writeValueAsString(entity);
    assertTrue(houses.contains("some other desc"));
  }

  @Test
  public void testGet() throws Exception {
    Map entity = (Map) resource.get(HOUSES, 1).getEntity();
    assertEquals(4, entity.size());
    String house = this.objectMapper.writeValueAsString(entity);
    assertTrue(house.contains("some desc"));
    assertFalse(house.contains("some some desc"));
  }

  @Test
  public void testDelete() throws Exception {
    resource.delete(HOUSES, 1);
    try {
      resource.get(HOUSES, 1).getEntity();
      fail();
    } catch (EmptyResultDataAccessException e) {
    }
  }

  @Test
  public void testUpdate() throws Exception {
    Map<String, Object> house = (Map<String, Object>) resource.get(HOUSES, 1).getEntity();
    house.put("DESCRIPTION", "new desc");
    resource.update(HOUSES, house);
    resource.get(HOUSES, 1).getEntity();
    assertEquals("new desc", house.get("DESCRIPTION"));
  }

  @Test
  public void testSave() throws Exception {
    Map<String, Object> house = new HashMap<>();
    //driver will do the type conversions
    house.put("description", "new desc");
    house.put("price", "77.77");
    house.put("creation_date", "2013-10-25 09:18:10.35");
    house = (Map<String, Object>) resource.save(HOUSES, house).getEntity();
    assertEquals("new desc", house.get("DESCRIPTION"));
    assertNotNull(house.get("ID"));

  }

  @Before
  public void before() throws Exception {
    DataSource ds = dataSource();
    this.resource = new RestDbResource(ds);
    Flyway flyway = flyway(ds);
    flyway.migrate();
  }

  @After
  public void after() throws Exception {
    Flyway flyway = flyway(dataSource());
    flyway.clean();
  }

  @AfterClass
  public static void afterClass() throws Exception {
    FileUtils.deleteDirectory(new File("./target/db"));
  }

  private Flyway flyway(DataSource dataSource) {
    Flyway flyway = new Flyway();
    flyway.setInitOnMigrate(true);
    flyway.setDataSource(dataSource);
    flyway.setLocations("db/hsqldb/migrations");
    return flyway;
  }

  private DataSource dataSource() {
    BasicDataSource ds = new BasicDataSource();
    ds.setDriverClassName("org.hsqldb.jdbcDriver");
    ds.setUrl("jdbc:hsqldb:file:target/db/db;shutdown=true;hsqldb.lock_file=false");
    ds.setUsername("sa");
    ds.setPassword("");
    return ds;
  }


}
