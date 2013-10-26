package com.zilverline.rest.db;

import com.yammer.dropwizard.config.Configuration;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class RestDbConfiguration extends Configuration {

  @Valid
  @NotNull
  @JsonProperty
  private DbConfiguration db = new DbConfiguration();

  public DbConfiguration getDb() {
    return db;
  }

  public void setDb(DbConfiguration db) {
    this.db = db;
  }
}
