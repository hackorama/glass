Glass (2005)
=====

Java3D psuedo transparent window hack

Java GUI API's (AWT/SWING) or Java3D does not provide transparent windows or shaped windows like other platform specfic API's like X/Motif or MS Windows API.

![tag screenshot](https://github.com/hackorama/glass/blob/master/screenshot.png)

This is a hack for a psuedo transaprent window using Java Robot API to do a screen capture and using it as the background on the Java 3D wiew on a JWindow created without any window decorations.

How does it work

1.  Use Robot API and do a screen capture and store it into a BufferedImage
2.  Extend JWindow override paint() and update()
3.  Create a Canvas3D with default GraphicsConfiguration
4.  Add the Canvas3D to the Content Pane of JWindow created in Step 2
5.  Create a Java3D Scene Graph with the BufferedImage captured by Robot in Step 1 as the background texture.
6.  Create a SimpleUniverse with Canvas3D from Step 3 and Scene Graph from Step 5


