# MapFish 2 

Please read the documentation available here:
http://www.mapfish.org/doc/index.html

## Build

Execute the following command:
```bash
./gradlew build
```

This will build three artifacts:  ``print-servlet-xxx.war``, ``print-lib.jar``, ``print-standalone.jar``

## Run from commandline

The following command will run the mapfish printer.  If you do no supply any -Dxxx args then all argument options will be listed.

```bash
./gradlew run -Dconfig=samples/config.yaml -Dspec=samples/spec.json -Doutput=/tmp/print-out.pdf
```
## Deploy

1. Create ``~/.gradle`` file based on template ``gradle.properties`` provided
2. Use osgeo id credentials to deploy to repo.osgeo.org
   ```
   enablePublishing=true
   osgeoUsername=gtbuild
   osgeoPassword=....
   ```
3. The following command will build and upload all artifacts to the osgeo repository.

   ```bash
   ./gradlew upload 
   ```

## Eclipse

To build in eclipse:

1. Create eclipse project metadata:
   ```bash
   ./gradlew eclipse
   ```
   
2. Import project into eclipse


To run in eclipse:

1. Create new Java Run Configuration
2. Main class is ``org.mapfish.print.ShellMapPrinter``
3. Program arguments: ``--config=samples/config.yaml --spec=samples/spec.json --output=$HOME/print.pdf``