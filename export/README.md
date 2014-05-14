# Server-export module.
======

Server-export is a module of the component [server](..) capable of exporting all the emails from the MongoDB database. The email format that the database
is handling is documented in the module [import](../import)

### Configuration and running
To configure the export instance, you have change the information in the configuration files saved in the folder src/main/resource.
This folder consists of 3 files:
-	**database.properties:** Configuration file containing the information about the database instance
-	**mailinglists.properties:** Configuration file containing the information about the allowed mailinglists.
-	**searchisko.properties:** Configuration file containing the information about the [searchisko](https://github.com/searchisko/searchisko) instance

Then run <pre><code>mvn clean install</code></pre> and deploy the application to a WildFly instance.
### REST API
TODO

