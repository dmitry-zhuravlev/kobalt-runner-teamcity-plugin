[![Build Status](https://travis-ci.org/dmitry-zhuravlev/kobalt-runner-teamcity-plugin.svg?branch=master)](https://travis-ci.org/dmitry-zhuravlev/kobalt-runner-teamcity-plugin)
[![Apache 2.0](https://img.shields.io/badge/license-Apache%202.0-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)

TeamCity runner plugin for Kobalt build system.

<b>Features:</b>

- Autodetect Kobalt builds if the following files `kobalt/src/Build.kt`,`kobaltw.bat/kobaltw` are present.
- Run wrapper or choose from available Kobalt versions. 
If some version specified the agent-side of the plugin perform download and install it to machine where agent is running.
- Kobalt settings can be specified in Kobalt build step configuration.
- Adjusting a build by passing some custom properties like `--log 3` and etc.
- Artifacts in `kobaltBuild/libs` would be published like build artifacts.

<b>For those who connected behind corporate HTTP(S) proxy server:</b>
HTTP(S) Proxy server should be specified in the following places:
  - In server JVM parameters (TEAMCITY_SERVER_OPTS)
  - In agent JVM parameters (TEAMCITY_AGENT_OPTS)

E.g. for HTTP proxy server: 

`-Dhttp.proxyHost=somecompany.com -Dhttp.proxyPort=3128 -Dhttp.nonProxyHosts="localhost|192.168.*"`

E.g. for HTTPS proxy server: 

`-Dhttps.proxyHost=somecompany.com -Dhttps.proxyPort=3128 -Dhttps.nonProxyHosts="localhost|192.168.*"`

   

