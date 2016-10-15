[![Build Status](https://travis-ci.org/dmitry-zhuravlev/kobalt-runner-teamcity-plugin.svg?branch=master)](https://travis-ci.org/dmitry-zhuravlev/kobalt-runner-teamcity-plugin)
[![Apache 2.0](https://img.shields.io/badge/license-Apache%202.0-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)

TeamCity runner plugin for Kobalt build system.

Features:

- Autodetect Kobalt builds if the following files ```kobalt/src/Build.kt```,```kobaltw.bat/kobaltw``` are present.
- Run wrapper or choose from available Kobalt versions. 
If some version specified the agent-side of the plugin perform download and install it to machine where agent is running.
- Kobalt settings can be specified in Kobalt build step configuration.
- Adjusting a build by passing some custom properties like ```--log 3``` and etc.
- Artifacts in ```kobaltBuild/libs``` would be published like build artifacts.


Note: To start TeamCity agent in debug mode the ```agentOptions``` should be specified. This options supported only by 
   ```com.github.rodm:gradle-teamcity-plugin:0.10-SNAPSHOT```. Download [gradle-teamcity-plugin](https://github.com/rodm/gradle-teamcity-plugin) and build ```0.10-SNAPSHOT``` instead of using ```0.9.1``` 
   

