# Getting Started
This app can help you to copy all files from one directory to your needed by filtering by files extensions.

This is classic terminal application. Use this template to run

``java -jar FilesCollector.jar --source 'C:\Users\user\Downloads' --dest 'D:\img' -e '.png' -e '.pdf'``

## Documentation

### Versions
+ Java 8

#### List of arguments

+ _--extension_ or _-e_ to writhe extensions. Count not limited
+ _--source_ set your source directory
+ _--dest_ set your destination directory
+ _--depth_ depth of subdirectories. Default _Integer.MAX_VALUE_

### Warning!

App will fail if run it with RECYCLE.BIN in your source directory (usually in disk root)