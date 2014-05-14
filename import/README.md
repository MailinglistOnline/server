# Server-import module.
======

Server-import is a module of the component [server](..) capable of importing emails from terminal and .mbox files right to the MongoDB database.

### Configuration and running
To configure the import instance, you have change the information in the configuration files saved in the folder src/main/resource.
This folder consists of 3 files:
-	**database.properties:** Configuration file containing the information about the database instance
-	**mailinglists.properties:** Configuration file containing the information about the allowed mailinglists. When emails contains more addresses matching these mailinglists, duplicate for each of them will be saved in the database. If an email doesn't match any of the provided email addresses, the email will not be saved and the information will be logged to console.
-	**searchisko.properties:** Configuration file containing the information about the [searchisko](https://github.com/searchisko/searchisko) instance

Then run <pre><code>mvn clean install</code></pre> and use a script **import.sh** to import emails into the database instance. The script can be called
on a single mbox file or the whole folder containing the mbox files.
### Email format
TODO

