# Trace Metrics

Trace Metrics is an IntelliJ plugin which makes use of New Relic trace annotations to make navigable connections 
between source code and live production metrics. While in the source, a developer can make informed decisions about 
code paths which may have high usage or no usage at all. 


## Build & Installation

To build the project from source... 

1. Install Gradle
1. Run `gradle build` 
1. Find the `trace-metrics-x.x.x.zip` artifact under `./build/distributions`

To install the plugin in IntellJ...

1. Navigate to `Preferences > Plugins > Install plugin from disk`
1. Find and select the plugin jar file
1. Restart your IDE

## Usage

##### Navigate

To open the tool window, right click on a `@Trace` annotated line and select Trace Metrics from the context menu, or 
navigate to `View > Tool Windows > Trace Metrics`. Double click on a row in the tool window to navigate back to source.

<img width="600" alt="example-contextmenu" src="https://raw.githubusercontent.com/disney/trace-metrics-intellij-plugin/master/example/example-contextmenu.png">

##### Configure

Configure your New Relic connection settings. Click on the configuration button in the tool window or navigate to 
`Preferences > Trace Metrics`. 

##### Reload

To run the query at any time, click the refresh button in the tool window. The numbers will update if there is any 
change. Note, there is a search result limit of 1000. The query will run as a background task and can be cancelled via 
the IntelliJ progress bar. The number of days to query is configurable and can be set to 0 to disable the request. 

##### Open Insights

To load a query in New Relic Insights, right click on a row in the tool window and select `Open in Browser`.

<img width="600" alt="example-toolwindow" src="https://raw.githubusercontent.com/disney/trace-metrics-intellij-plugin/master/example/example-toolwindow.png">

## Compatibility

Compatible with IntelliJ CE and IntelliJ Ultimate versions 2017.1 and later. 

## Support

Email the developers at [corp.trace-metrics@disney.com](mailto:corp.trace-metrics@disney.com)
 
## License
Copyright 2018 Disney Worldwide Services, Inc.

Licensed under the Apache License, Version 2.0 (the "Apache License") with modifications. See LICENSE.txt for details

## Developer Notes


##### Troubleshooting

During development, pay attention for runtime exceptions. IntelliJ shows an alert dialog on the lower right hand corner 
of the screen which can be expanded for more details. 

##### Testing
Run `gradle test`. See also [TESTING.md](TESTING.md) for common manual tests. 
