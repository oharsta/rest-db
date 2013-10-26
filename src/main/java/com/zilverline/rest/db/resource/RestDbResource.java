package com.zilverline.rest.db.resource;

import com.google.common.base.Joiner;
import com.zilverline.rest.db.DbConfiguration;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Path("/rest")
@Produces(MediaType.APPLICATION_JSON)
public class RestDbResource {

  private final JdbcTemplate template;
  private final NamedParameterJdbcTemplate namedTemplate;

  public RestDbResource(DbConfiguration db) {
    this(dataSource(db));
  }

  public RestDbResource(DataSource ds) {
    template = new JdbcTemplate(ds);
    namedTemplate = new NamedParameterJdbcTemplate(template);
  }

  @GET
  @Path("/meta")
  public Response meta() throws SQLException {
    return Response.ok(template.execute(new ConnectionCallback<List<String>>() {
      @Override
      public List<String> doInConnection(Connection connection) throws SQLException, DataAccessException {
        final List<String> result = new ArrayList<>();
        ResultSet tables = connection.getMetaData().getTables(null, null, null, null);
        while (tables.next()) {
          result.add(tables.getString(3));
        }
        return result;
      }
    })).build();
  }

  @GET
  @Path("/{object}")
  public Response getAll(@PathParam("object") String table) {
    return Response.ok(template.queryForList("select * from " + table)).build();
  }

  @GET
  @Path("/{object}/{id}")
  public Response get(@PathParam("object") String table, @PathParam("id") long id) {
    return Response.ok(template.queryForMap("select * from " + table + " where id = ?", id)).build();
  }

  @DELETE
  @Path("/{object}/{id}")
  public Response delete(@PathParam("object") String table, @PathParam("id") long id) {
    template.update("delete from " + table + " where id = ?", id);
    return Response.noContent().build();
  }

  @PUT
  @Path("/{object}")
  public Response update(@PathParam("object") String table, Map data) {
    namedTemplate.update(getUpdateStatement(table, data.keySet()), data);
    return this.get(table, getPk(data));
  }

  @POST
  @Path("/{object}")
  public Response save(@PathParam("object") String table, Map data) {
    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
    namedTemplate.update(getInsertStatement(table, new ArrayList<String>(data.keySet())), new MapSqlParameterSource(data), keyHolder);
    return get(table, keyHolder.getKey().longValue());
  }

  private String getUpdateStatement(String table, Set<String> keys) {
    StringBuilder sql = new StringBuilder("update " + table + " set ");
    Map<String, String> joinedMap = new HashMap<>();
    for (String key : keys) {
      if (!key.equalsIgnoreCase("id")) {
        joinedMap.put(key, key);
      }
    }
    sql.append(Joiner.on(", ").withKeyValueSeparator(" = :").join(joinedMap));
    return sql.append(" where id = :ID").toString();
  }

  private String getInsertStatement(String table, List<String> keys) {
    String columns = Joiner.on(", ").join(keys);
    List<String> params = new ArrayList<>();
    for (String key : keys) {
      params.add(":" + key);
    }
    String paramValues = Joiner.on(", ").join(params);
    return "insert into " + table + " ( " + columns + " ) values ( " + paramValues + " )";
  }

  private long getPk(Map<String, ?> data) {
    Object id = data.get("ID");
    return id instanceof Long ? (Long) id : (id instanceof String ? Long.valueOf((String) id) : null);
  }

  private static BasicDataSource dataSource(DbConfiguration db) {
    BasicDataSource ds = new BasicDataSource();
    ds.setDriverClassName(db.getDriverClassName());
    ds.setUrl(db.getUrl());
    ds.setUsername(db.getUsername());
    ds.setPassword(db.getPassword());
    return ds;
  }
}
