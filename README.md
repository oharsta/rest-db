# Introduction

The drop wizard rest-db application was developed for a workshop / training about REST. It was subsequently used as a generic table- and db-agnostic backend API in a training teaching the JavaScript development workflow [yeoman](http://yeoman.io/) and the JavaScript frameworks [AngularJS](http://angularjs.org/) and [Ember](http://emberjs.com/).

# Overview

The standalone rest-db application offers a generic CRUD REST API (acronyms...) for any underlying DataSource. Configure in ./rest-db-local.yml the driver, url, username and password for a JDBC compliant DataSource driver and you will be able to query the underlying database and tables and POST / PATCH new records and updates.

# Running The Application

To bootstrap the rest-db application run the following commands.

* To package the example run (maven3 is a prerequisite to install the rest-db app).

        mvn package

* To run the server run (in debug mode).

         java -jar -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000 target/rest-db-0.1-SNAPSHOT.jar

# Usage

First configure the rest-db-local.yml with your own database. Then point your browser at the following:

* http://localhost:8080/rest/meta

The list of tables of your database is listed. You then can list the contents of specific tables with:

* http://localhost:8080/rest/${table_name}
* http://localhost:8080/rest/${table_name}/${id}

Or query for specific records:

* http://localhost:8080/rest/${table_name}?${column}=${column_value}&${column2}=${column2_value}

And POST / PATCH updates (with either [curl](http://curl.haxx.se/) or [Postmen](http://www.getpostman.com/)) at:

* http://localhost:8080/rest/${table_name}

# Disclaimer

This is not something you would want to run in production. The rest-db app was only developed as part of a JavaScript training to serve as a simple, generic CRUD API for any Database of choice to function as a backend.
For more information see `rest-db/src/test/java/com/zilverline/rest/db/resource/RestDbResourceTest.java`