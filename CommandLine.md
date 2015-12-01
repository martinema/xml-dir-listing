# Using XML Directory Listing from the command line #

If you've installed XML Directory Listing as described in the [Installation Guide](InstallationGuide.md), running the app from the command-line is simple: just type `xml-dir-listing`.

This should produce the following output:

```
$ xml-dir-listing

usage: xml-dir-list [options] source

options:
 -c,--encoding <arg>     sets character encoding definition for XML file
 -d,--depth <arg>        depth of directory listings
 -e,--excludes <arg>     excludes regEx for directory listings
 -f,--dateformat <arg>   date format for listings
 -h,--help               print this message
 -i,--includes <arg>     includes regEx for directory listings
 -o,--output <arg>       output file
 -r,--reverse            sort reverse
 -s,--sort <arg>         sort method
 -v,--verbose            verbose logging
```

## Running the application ##

Only two command line arguments are required for the xml-dirlisting application, an output file and a directory to list.

```
xml-dir-listing -o list.xml "C:\Some Directory"
```

More command line options may be specified to further configure the XML output. In this example the application is used with verbose output, with the output sorted by file size and the date formats set to just yyyy-MM-dd (e.g. 2007-09-18).

```
xml-dir-listing -v -s filesize -f "yyyy-MM-dd" -o list.xml "C:\Some Directory"
```


## Command line options in more detail ##

| **Argument** | **Description** | **Required** |
|:-------------|:----------------|:-------------|
| -c, --encoding | The character encoding attribute for XML declaration, defaults to UTF-8 | No           |
| -o, --output | The output XML file. | Yes          |
| -d, --depth  | How deep into the directory structure to list | No           |
| -e, --excludes | A regex pattern to exclude files from listing | No           |
| -i, --includes | A regex pattern to include files from listing | No           |
| -r, -reverse | If present sort order is reversed. | No           |
| -s, --sort   | Sort order. Options are `directory`, `lastmodified`, `name`, `size`. Defaults to `name` | No           |
| -f, --dateformat | The date format for date based attributes, [Java SimpleDateFormat](http://java.sun.com/j2se/1.5.0/docs/api/java/text/SimpleDateFormat.html). Defaults to `yyyyMMdd'T'HHmmss` | No           |
| -v, --verbose | Verbose output on | No           |

## Doctype ##

The doctype for the output XML is as follows:

```
<?xml version="1.0" encoding="ISO-8859-1"?>

 <!ELEMENT directory (directory|file)*>
 <!ATTLIST directory
    name         CDATA #REQUIRED
    lastModified CDATA #REQUIRED
    date         CDATA #REQUIRED
    size         CDATA #REQUIRED
    absolutePath CDATA #REQUIRED
    sort         CDATA #IMPLIED
    reverse	 CDATA #IMPLIED>

 <!ELEMENT file EMPTY>
 <!ATTLIST file
    name         CDATA #REQUIRED
    lastModified CDATA #REQUIRED
    date         CDATA #REQUIRED
    size         CDATA #REQUIRED
    absolutePath CDATA #REQUIRED>
```









