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
By default the end date of the query is today at the time of the query, so "1 day ago" would equate to the last 24 hours.
You can choose to set a different end date, in which case the time will based on the end of that date in UTC.  

##### Open Insights

To load a query in New Relic Insights, right click on a row in the tool window and select `Open in Browser`.

<img width="600" alt="example-toolwindow" src="https://raw.githubusercontent.com/disney/trace-metrics-intellij-plugin/master/example/example-toolwindow.png">

## Compatibility

Compatible with IntelliJ CE and IntelliJ Ultimate versions 2020.2 and later. 

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


## Release Notes

### 1.0.0

 * Initial release.
 
### 1.1.0

 * Fix query retry logic.
 * Updated Jackson.
 
### 1.2.0

 * Build with stable intellij project.
 
### 1.3.0

 * Fixed issues running against IntelliJ 2020.1
 * Added ability to configure query by date, and to combine multiple queries for date ranges.
 * Now showing File Name to differentiate when the same Trace Name is used multiple times in code.
 * Updating progress indicator as multiple calls to New Relic are made.
 * Updating counts as multiple calls to New Relic are made.
 * Adjustments to plugin configuration UI.
 * Fixing issue saving Days to Query configuration.
 * No longer auto-running New Relic query on load.  Must click refresh to run whatever has been saved in the configuration.

### 2.0.0

 * Converted to use New Relic NerdGraph API for NRQL queries.
 * Updating IDE compatibility to 2020.2.
 * Updating various dependency versions.
 * Renamed logo files to avoid possible conflicts with other plugins.

