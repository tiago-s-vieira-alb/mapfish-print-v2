This repository is a submodule of mapfish-print and contains e2e tests for the pdf and image generation.  It contains the code to
start a Geoserver instance and run junit integration tests against the server to test as many of the mapfish-print options as possible.

Due to the difficulty of verification there are two modes of execute.  

An interactive mode and a automated mode.  The interactive mode contains steps where a developer has to validate the responses.

To use module:

    git clone --recurse-submodules https://github.com/mapfish/mapfish-print.git
    cd mapfish-print
    ./gradlew testInteractive # for interactive and automated tests
    ./gradlew test # for automated tests only
    
    
Both tasks are gradle test tasks and more details on how to run single tests or subgroups of tests can be understood by referring to:

    http://www.gradle.org/docs/current/userguide/java_plugin.html#sec:java_test


# Writing Tests

There are two types of tests.  All classes that end in _Interactive_ will be ran only during the testInteractive task.  All other junit
 tests will be ran during the normal test task.