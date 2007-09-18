package net.matthaynes.xml.dirlist;

import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


// Log4j
import org.apache.log4j.*;

//SAX classes.
import org.xml.sax.*;
import org.xml.sax.helpers.*;

//JAXP 1.1
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.sax.*;

// Jakarta Regex
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

/**
 * XML Directory Listing Class
 */

public final class XmlDirectoryListing {
	
	// SAX stuff, comment better, understand more!
	protected static StreamResult streamResult;
	protected static SAXTransformerFactory tf;
	protected static TransformerHandler hd;
	protected static Transformer serializer;
	protected static AttributesImpl atts;
	
	/** Sort method for the directory listing. Defaults to "name". */
	public String sort = "name"; 
	
	/** Reverse sort method. Defaults to false. */
	public boolean reverse = false;
	
	/** Date format for date attributes. Defaults to "yyyyMMdd'T'HHmmss" */
	public DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
	
	/** Depth control for directory recursion. Defaults to "false" */
	protected boolean depthControl = false;
	
	/** How deep to recurse into directory structure. */
	public int depth;
	
    /** The regular expression for the include pattern. */
	public RE includeRE;
    
    /** The regular expression for the exclude pattern. */
	public RE excludeRE;
	
	/** Log4j logger */ 
    public org.apache.log4j.Logger log = Logger.getLogger(XmlDirectoryListing.class);
	
    /** Log4j appender */
    public Appender myAppender;
    
    /** Log4j PatternLayout */
    public PatternLayout myLayout;
    
    /**
     * Consturctor. Thus far only sets up log4j appender.
     */
    public XmlDirectoryListing() {
    	
    	/* Instantiate a layout and an appender, assign layout to appender programmatically */
    	myLayout = new PatternLayout();
    	myAppender = new ConsoleAppender(myLayout);    // Appender is
 
    	// Assign appender to the logger programmatically 
    	log.addAppender(myAppender); 

    }
    
	/**
	 * Starts generation of XML directory listing.
	 * @param dir The directory to begin listing.
	 * @param out The output stream to write XML file to.
	 */
	public void generateXmlDirectoryListing (final File dir, final OutputStream out) {
		
		if (dir.isDirectory()) {
			
			try {
				
				log.info("Generating listing for " + dir.getAbsolutePath());
				
				// Acts as an holder for a transformation result, which may be XML, plain Text, 
				// HTML, or some other form of markup.
				streamResult = new StreamResult(out);

				// This class extends TransformerFactory to provide SAX-specific factory methods. It 
				// provides two types of ContentHandlers, one for creating Transformers, the other 
				// for creating Templates objects.
				tf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
				
				// This class provides a default implementation of the SAX2 Attributes interface,
				// with the addition of manipulators so that the list can be modified or reused.
				// SAX Helper
				atts = new AttributesImpl();
				
				// A TransformerHandler listens for SAX ContentHandler parse events and 
				// transforms them to a Result.
				hd = tf.newTransformerHandler();
				
				// An instance of this abstract class can transform a source tree into a
				// result tree.
				serializer = hd.getTransformer();
				
				// Set an output property that will be in effect for the transformation.
				serializer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
				serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://xml-dir-listing.googlecode.com/svn/tags/version_0.1/lib/xml-dir-listing.dtd");
				serializer.setOutputProperty(OutputKeys.INDENT,"yes");
	
				// Enables the user of the TransformerHandler to set the to set the Result 
				// for the transformation.
				hd.setResult(streamResult);
				
				// SAX ContentHandler parse event
				hd.startDocument();
				log.debug("Starting XML document");
				
				createElement(dir, depth, true);
							
				// SAX ContentHandler parse event
				hd.endDocument();
				log.debug("Ending XML document");
				log.info("XML document generated.");
							
			} catch (Exception e) {
				log.fatal("Fatal error in generating document: " +e);
			}
			
		} else {
			log.error(dir.getAbsolutePath() + " is not a valid directory.");
		}
		
	}
	
	/**
	 * Creates a new element based on the file based through.
	 * @param file The file on which the element is based.
	 */
	public void createElement(final File file, final int depth, boolean isRoot) {
		
		log.debug("Analysing "+ file.getAbsolutePath());
		
		if (file.isDirectory() || this.isIncluded(file) && !this.isExcluded(file) || isRoot == true) {
			
			if (isRoot) {
				this.setAttributes(file, true);
			} else {
				this.setAttributes(file);
			}
			
			String fileType = (file.isDirectory()) ? "directory" : "file"; 
			
			try{
				
				log.debug("Starting element " + file.getAbsolutePath());
				
				// Output details of the file
				hd.startElement("","",fileType,atts);
				
				if (fileType == "directory") {
					this.parseDirectory(file, depth);
				}
				
				log.debug("Ending element " + file.getAbsolutePath());
				
				hd.endElement("","",fileType);
			
			} catch (SAXException e) {
			
			}			
		}
	
	}
	
    /**
     * Determines if a given File shall be visible.
     * 
     * @param path  the File to check
     * @return true if the File shall be visible or the include Pattern is <code>null</code>,
     *         false otherwise.
     */
    public boolean isIncluded(File path) {
    	 boolean incExB = (this.includeRE == null) ? true : this.includeRE.match(path.getName());
    	 if (!incExB) log.debug("Excluding: " + path.getAbsolutePath());
    	 return incExB;
    }

    /**
     * Determines if a given File shall be excluded from viewing.
     * 
     * @param path  the File to check
     * @return false if the given File shall not be excluded or the exclude Pattern is <code>null</code>,
     *         true otherwise.
     */
    public boolean isExcluded(File path) {
    	boolean incExB = (this.excludeRE == null) ? false : this.excludeRE.match(path.getName());
    	if (incExB) log.debug("Excluding: " + path.getAbsolutePath());
    	return incExB;
   	}
	
	/**
	 * Sort all files in directory, create an element for each.
	 * 
	 * @param dir The directory to parse. 
	 * @param depth Current depth of directory structure. 
	 */
	public void parseDirectory(File dir, final int depth) {
		
		if (!this.depthControl || depth > 0) {

			// Catch for null pointer, occurs when trying to access restricted dir
			try {
				// Store all files in an array and sort.
				File[] files = sortFiles(dir.listFiles());
			
				// Loop through array and create element for each file.
				for (int i=0;i<files.length;i++) {
						createElement(files[i], depth -1, false);
				}
				
			} catch (Exception e) {
				log.error("Error listing directory contents: " + e);
			}
		}
	}
	
	/**
	 * Sets all attributes for the file 
	 * @param file The file to set attributes for
	 */
	public void setAttributes(File file) {

		log.debug("Setting attributes for: " + file.getAbsolutePath());
		
		// Clear current attributes
		atts.clear();
		
		atts.addAttribute("","","name","CDATA",file.getName());
		atts.addAttribute("","","size","CDATA",String.valueOf(file.length()));
		atts.addAttribute("","","lastModified","CDATA",String.valueOf(file.lastModified()));
		atts.addAttribute("","","date","CDATA",this.dateFormat.format(new Date(file.lastModified())));
		atts.addAttribute("","","absolutePath","CDATA",file.getAbsolutePath());
		
	}
	
	/**
	 * Sets all attributes for the root node 
	 * @param file The file to set attributes for
	 * @param isRoot Is this the root node
	 */
	public void setAttributes(File file, boolean isRoot) {

		if (isRoot) {
			
			String reverse = (this.reverse) ? "true" : "false";
			
			log.debug("Setting attributes for root node: " + file.getAbsolutePath());
			
			// Clear current attributes
			atts.clear();
			
			atts.addAttribute("","","name","CDATA",file.getName());
			atts.addAttribute("","","size","CDATA",String.valueOf(file.length()));
			atts.addAttribute("","","lastModified","CDATA",String.valueOf(file.lastModified()));
			atts.addAttribute("","","date","CDATA",this.dateFormat.format(new Date(file.lastModified())));
			atts.addAttribute("","","absolutePath","CDATA",file.getAbsolutePath());
			atts.addAttribute("","","sort","CDATA",this.sort);
			atts.addAttribute("","","reverse","CDATA",reverse);
		}
		
	}
	
	/**
	 * Sets the date format for the class, must be valid SimpleDateFormat
	 * @param format The Java SimpleDateFormat string.
	 */
	public void setDateFormat(String format) {
		
		// Set date format
		try {
			this.dateFormat = new SimpleDateFormat(format);
			log.info("Setting date format to: " + format);
		} catch (Exception e) {
			log.error("Error parsing date format: " + e);
		}		
	}
	

	/**
	 * Sets the depth of the directory listing.
	 * @param newDepth How deep into the directory structure to list.
	 */
	public void setDepth(int newDepth) {
		this.depthControl = true;
		this.depth = newDepth;
		log.info("Setting directory listing depth to: " + Integer.toString(newDepth));
	}
	
	
	/**
	 * Sets the exclude regular expression
	 * @param rePattern The regular expression.
	 */
	public void setExcluded(String rePattern) {
        
		try {           
            this.excludeRE = new RE(rePattern);
            log.info("Setting excludes regular expression to: " + rePattern);
        } catch (RESyntaxException rese) {
        	log.error("Error parsing regular expression: " + rese);
	   }
	}
	
	
	/**
	 * Sets the include regular expression
	 * @param rePattern The regular expression.
	 */
	public void setIncluded(String rePattern) {
        
		try {   
			this.includeRE  = (rePattern == null) ? null : new RE(rePattern);
			log.info("Setting includes regular expression to: " + rePattern);
        } catch (RESyntaxException rese) {
        	log.error("Error parsing regular expression: " + rese);
        }
	}
	
	
	/**
	 * Sets the sort type, returns error if bad type specified.
	 * @param newSort The type of sort.
	 */
	public void setSort(String newSort) {
		
		if (newSort.equals("directory")||
				newSort.equals("name")||
				newSort.equals("size")||
				newSort.equals("lastmodified")) {
			
			this.sort = newSort;
			log.info("Setting sort to: " + newSort);			
	
		} else {
			log.error("Error setting sort. '" + newSort + "' is not a recognized sort parameter.");
		}
		
	}
	
	
	/**
	 * Sets the sort reverse attribute.
	 * @param newReverse The value of new reverse, true / false
	 */
	public void setSortReverse(boolean newReverse) {
		this.reverse = newReverse;	
		log.info("Setting reverse sorting to: " + (newReverse ? "on" : "off"));
	}
	
	
	/**
	 * Sorts the array of files based upon the specified sort order. Defaults to directory.
	 * Most of this method ripped from Cocoon's DirectoryGenerator class, the justification for this being
	 * that it seems the only logical way of coding the method!! 
	 * @param files The array of files to sort 
	 */
	public File[] sortFiles(File[] files) {
		
        if (this.sort.equals("name")) {
            Arrays.sort(files, new Comparator() {
                public int compare(Object o1, Object o2) {
                    if (reverse) {
                        return ((File)o2).getName().compareToIgnoreCase(((File)o1).getName());
                    }
                    return ((File)o1).getName().compareToIgnoreCase(((File)o2).getName());
                }
            });
        } else if (this.sort.equals("size")) {
            Arrays.sort(files, new Comparator() {
                public int compare(Object o1, Object o2) {
                    if (reverse) {
                        return new Long(((File)o2).length()).compareTo(
                            new Long(((File)o1).length()));
                    }
                    return new Long(((File)o1).length()).compareTo(
                        new Long(((File)o2).length()));
                }
            });
        } else if (this.sort.equals("lastmodified")) {
            Arrays.sort(files, new Comparator() {
                public int compare(Object o1, Object o2) {
                    if (reverse) {
                        return new Long(((File)o2).lastModified()).compareTo(
                            new Long(((File)o1).lastModified()));
                    }
                    return new Long(((File)o1).lastModified()).compareTo(
                        new Long(((File)o2).lastModified()));
                }
            });
        } else if (this.sort.equals("directory")) {
            Arrays.sort(files, new Comparator() {
                public int compare(Object o1, Object o2) {
                    File f1 = (File)o1;
                    File f2 = (File)o2;

                    if (reverse) {
                        if (f2.isDirectory() && f1.isFile())
                            return -1;
                        if (f2.isFile() && f1.isDirectory())
                            return 1;
                        return f2.getName().compareToIgnoreCase(f1.getName());
                    }
                    if (f2.isDirectory() && f1.isFile())
                        return 1;
                    if (f2.isFile() && f1.isDirectory())
                        return -1;
                    return f1.getName().compareToIgnoreCase(f2.getName());
                }
            });
        }

		return files;
	}
		
}
