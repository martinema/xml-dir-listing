package net.matthaynes.xml.dirlist;

import java.io.*;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
* Ant controller for Directory Listing to XML.
* Matt Haynes, November 2006
*
* Creates an Ant task for the Directory Listing to XML app
*
* Example Usage (in Ant):
*    <xml-dir-list verbose="true" help="true" srcDir="C:\Program Files\Apache Ant\apache-ant-1.6.5\lib" destFile="C:\hello.xml" />
*
**/

public final class AntFileListing extends Task {

		// Global var's passed in from Ant Task
		public String directory;
		public String xmlFile;
		public String verb = "";
		public String help = "";

		// Get arguments from Task, not sure how this works!!!!!
	    public void setSrcDir(String directory) {
	    	this.directory = directory;
	    }

	    public void setDestFile(String xmlFile) {
	    	this.xmlFile = xmlFile;
	    }

	    public void setVerbose(String verb) {
	       this.verb = verb;
	    }

	    public void setHelp(String help) {
	        this.help = help;
	    }

	    // Main function
	    public void execute() {

	    	// Check we have a directory!!
	    	if(directory == null){
	            throw new BuildException("No Directory set.");
	    	}

	    	// Check we have a file!!
	        if(xmlFile == null){
	            throw new BuildException("No output file set.");
	        }

	        // Is verbiose on?
	        if(verb.equals("true")) {
	            verb = "-v";
	        }
	         // Is help on?
	        if(help.equals("true")) {
	            help = "-h";
	        }

	        // Output some stuff.
	        log(directory);
	        log(xmlFile);

	        /*
	        // Call the FileListing class, passing it our arguments
        	try {
				 FileListing.main(verb, help, directory, xmlFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/

    }


}

