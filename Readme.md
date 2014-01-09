This repository is a submodule of mapfish-print and contains e2e tests for the pdf and image generation.  It contains the code to start a Geoserver instance and 
run junit integration tests against the server to test as many of the mapfish-print options as possible.  

Due to the difficulty of verification there are two modes of execute.  

An interactive mode and a automated mode.  The interactive mode contains steps where a developer has to validate the responses.

To use module:

    git clone --recurse-submodules https://github.com/mapfish/mapfish-print.git
    cd mapfish-print
    ./gradlew e2e-interactive # for interactive and automated tests
    ./gradlew e2e-headless # for automated tests only 
    