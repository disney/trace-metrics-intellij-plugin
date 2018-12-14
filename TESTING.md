## Manual Exercise of Trace Metrics

These manual tests walk through Trace Metrics behavior so that we catch issues as we update the code. 
For automated tests run `gradle test`.

### Easy Exercises

Given that we have a single project configured with New Relic AppID and key.

1. Find a metrics pane line with a busy (high count) metric. Hit "Refresh", watch the "working" animation, and see the number change.
2. Use "View -> Tool Windows -> Trace Metrics" to make the metrics pane disappear and reappear. 
3. Navigate from a `@Trace` annotation in project source to the corresponding line in the metrics pane. 
4. Navigate from a metrics pane line to the corresponding line in project source.
5. Scroll the metrics pane. Resize the metrics pane. Select lines and see a single row highlighted.
6. Navigate from a metrics pane line to the New Relic Web UI, signing in if necessary. (Does the SAML response lose the original page?)


### Multiple Projects

1. Open two projects in IntelliJ. Click "New Window" when asked whether to open the second project in a new window or this window.
2. Navigate to the Trace Metrics preferences pane while focused on the first project window.
3. Observe the application name.
4. Switch to the second project window and navigate to the Trace Metrics preferences pane.
5. Observe the application name. It should be different than the first project's application name.


### More Work

Given a new IntelliJ instance without the plugin installed.

1. Install the plugin.
2. Observe the metrics pane populated with trace metrics but no metrics.
3. Configure the New Relic AppID and key. See the metrics pane refresh and fill in.


