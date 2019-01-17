# Fuzzy-Image-Processing-Tool
Image processing tool that uses Java’s jFuzzyLogic library and Fuzzy Control Language (FCL).

## Procedure
Environment: Eclipse MARS.2

Run src/ImgTool.java

In the 'File' menu, click on 'Open File' to browse and choose a file.

Select from 'Edit' menu any of the following image processing functionalities:
* conversion to grayscale
* grayscale-image contrast enhancement
* colour-image contrast enhancement
* edge detection
* image segmentation.

In the File menu, click on 'Save Files As' to save the tranformed image.

## Results

Original image:

![Image cannot be loaded](https://i.imgur.com/eNQGupX.jpg)

Image after contrast enhancement:

![Image cannot be loaded](https://i.imgur.com/G5c91yA.jpg)

Segments demarcated in image:

![Image cannot be loaded](https://i.imgur.com/750FM7D.jpg)

Edges detected in image:

![Image cannot be loaded](https://i.imgur.com/XKYJesU.jpg)

Grayscale image:

![Image cannot be loaded](https://i.imgur.com/hJ2AntR.jpg)

Grayscale image after grayscale contrast enhancement:

![Image cannot be loaded](https://i.imgur.com/ItPFd4v.jpg)

## Files used for fuzzy logic inference
* contrastfuzzy.fcl (for grayscale-image contrast enhancement)
* cc.fcl (for colour-image contrast enhancement)
* edge.fcl (for edge detection)
* segment.fcl (for image segmentation)
