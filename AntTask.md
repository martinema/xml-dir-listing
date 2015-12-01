# XML Directory Listing Ant Task #

**A custom task for [Apache Ant](http://ant.apache.org/) which acts as an interface to the XML Directory Listing program.**

## Description ##

The XML Directory Listing Ant task acts as an interface to the XmlDirectoryListing Java class. The task can be used to generate an XML representation of a directory, this could then be taken and transformed further with Ant for different end uses.

  * Access all features of the XmlDirectoryListing Java class via Apache Ant
  * Tested with Apache Ant 6.5+. Please let us know if the task works with earlier versions!!

## Usage ##

The task needs to be defined in your Ant build file. The definition must point to the xml-dir-listing.jar file which is included the lib directory of your XML Directory listing distribution.

```
<taskdef name="xml-dir-listing"
        classname="net.matthaynes.xml.dirlist.AntFileListing"
        classpath="/path/to/xml-dir-list.jar"/>
```

XML Directory Listing references two external libraries. You will need to ensure that Ant can find these too, and that they are in the same directory as xml-dir-listing task.

The simplest way is to copy the below files into Ant's lib directory:


  * **xml-dir-listing.jar** - The XML Directory Listing package.
  * **jakarta-regexp.jar**  - The [Jakarta regexp pakage](http://jakarta.apache.org/regexp/). May already be referenced by Ant.
  * **log4j.jar**           - The [log4j package](http://logging.apache.org/log4j/). May already be referenced by Ant.

Please see the [External Tasks page](http://ant.apache.org/manual/using.html#external-tasks) in the ant manual for more information on using external tasks and referencing external libraries in [Apache Ant](http://ant.apache.org/)

## Parameters ##

| **Attribute** | **Description** | **Required** |
|:--------------|:----------------|:-------------|
| srcDir        | The directory to generate a listing for. | Yes          |
| destFile      | The output XML file. | Yes          |
| depth         | How deep into the directory structure to list | No           |
| encoding      | The character encoding attribute for XML declaration, defaults to UTF-8 | No           |
| excludesRegEx | A regex pattern to exclude files from listing | No           |
| includesRegEx | A regex pattern to include files from listing | No           |
| reverse       | If `true` sort order is reversed. Defaults to `false` | No           |
| sort          | Sort order. Options are `directory`, `lastmodified`, `name`, `size`. Defaults to `name` | No           |
| dateFormat    | The date format for date based attributes, [Java SimpleDateFormat](http://java.sun.com/j2se/1.5.0/docs/api/java/text/SimpleDateFormat.html). Defaults to `yyyyMMdd'T'HHmmss` | No           |

## Examples ##

Define the task in your build file:

```
<taskdef name="xml-dir-listing"
        classname="net.matthaynes.xml.dirlist.AntFileListing"
        classpath="/path/to/xml-dir-list.jar"/>
```

Using the task on the current directory with depth set to 5 and verbose output:

```
<xml-dir-listing depth="5" verbose="true" srcDir="." destFile="fileList.xml"/>
```


## Support ##

If you have any issues then please let us know, I will see what I can do.  I would also be very interested to hear what platforms and Ant versions people are using as I have not had time to extensivley test this task.




