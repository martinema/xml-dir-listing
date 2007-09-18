package net.matthaynes.xml.dirlist;

import java.util.*;
import java.io.*;

/**
* Directory Listing to XML.
* Matt Haynes, October 2006
*
* Recursively searches a given directory and all subdirectories and produces an XML representation.
*
* Example Usage:
*    java FileListing -v -h "C:\Program Files\Test Directory" "output.xml"
*
* To do:
*  - Get code reviewed
*  - Move write output file functionality to it's own function
*  - Do all checks before any processing, ie. is given output file valid?
*  - Allow the directory argument to take an ending /
*  - Possibly improve on sort functionality, so all directories appear at top, then files, all alphabetical.
*  - Tidy, better comments, optimize
*  - Improve checks for correct arguments.
*  - Improve error messages
*  - Improve verbose info
*  - Version info
*  - A filter so only certain files or folders are indexed. Could be regEx with eval().
**/

public final class FileListing {

	// Global variables
	public static String output = "";
	public static String outputFile = "";
	public static Boolean verbose = false;


	// Main function
	public static void main(String... aArguments) throws FileNotFoundException {

			// Get output file
			outputFile = aArguments[aArguments.length - 1];

			// Get Arguments and flags
			for(int i=0; i < aArguments.length; i++) {

				String thisArg = aArguments[i];

				if (thisArg.equals("-v") || thisArg.equals("-verbose") ) {
					// Verbose
					verbose = true;

				} else if (thisArg.equals("-help") || thisArg.equals("-h") || thisArg.equals("-?")) {

					// Run Help function
					generateHelpInformation();

				} else if (i ==  aArguments.length - 2) {

			     	// Call generation function, pass it directory name
					generateXmlListing(thisArg);

				}
			}
	}


  public static void generateXmlListing(String directory) {

		// Get directory
	 	File dir =  new File(directory);

	  	// Check dir is a directory
		if (dir.isDirectory()) {

			// Tell the command whats going on, because it can take a while!

			System.out.println("\nAnalysing directory...");

			output += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
			output += "<fileList>";

			// Call main function
			visitAllDirsAndFiles(dir);

			output += "</fileList>";

			// Get the directory the output file is in
			String outputParent = new File(outputFile).getParent();

			// Check it exists, if not create it
			if (outputParent != null && new File(outputParent).isDirectory() == false) {

				// Output information on command line if verbose is true
				if (verbose == true) {
					System.out.println("\nAttempting to generate the directory "+ outputParent);
				}

				// Make directories
				new File(outputParent).mkdirs();
			}

			// Output information on command line if verbose is true
			if (verbose == true) {
				System.out.println("\nAttempting to write to "+ outputFile);
			}

			// Write directory listing to xml file
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));

				out.write(output);
				out.close();

				System.out.println("\nXML Directory Listing successfully generated!");

			} catch (IOException e) {
				System.out.println("\nERROR: Cannot write to file. Please ensure you have specified a valid file name.");
			}

	} else {

		System.out.println("ERROR: Please ensure that you have specified a directory.");

	}

  }

  // Recursively search directory and output details as xml in a string
  public static void visitAllDirsAndFiles(File dir) {

		// Store all files and folder names in an array and sort alphabetically.
		String[] children = dir.list();
		Arrays.sort(children);

		// Loop through all files and folders
		for (int i=0; i<children.length; i++) {

			// Get file or directory
			File tempFile = new File(dir, children[i]);

			// Get the number of bytes in the file
			long length = tempFile.length();

			// Get the last modified date
			long lastModified = tempFile.lastModified();

			// Get absolute path
			String absolutePath = tempFile.getAbsolutePath();

			// Get path
			String path = tempFile.getPath();

			// Output information on command line if verbose is true
			if (verbose == true) {
				System.out.println("ANALYSING:  " + absolutePath);
			}

			// If the file is a directory output it's details and call this function again to recurse through files
			if (tempFile.isDirectory()) {

				output += "<directory>";
				output += "<name>" + children[i] + "</name>";
				output += "<absolutePath>" + absolutePath + "</absolutePath>";
				output += "<path>" + path + "</path>";

				visitAllDirsAndFiles(new File(dir, children[i]));

				output += "</directory>";

			} else {
				// Output details of the file
				output += "<file>";
				output += "<name>" + children[i] + "</name>";
				output += "<absolutePath>" + absolutePath + "</absolutePath>";
				output += "<path>" + path + "</path>";
				output += "<fileSize>" + length + "</fileSize>";
				output += "<lastModified>" + new Date(lastModified) + "</lastModified>";
				output += "</file>";
			}
		}

		// Garbage Collection
	}

	// Print help information
	public static void generateHelpInformation() {

		System.out.println("\n Usage: java FileListing <options> <directory> <output file>");
		System.out.println("\n Where possible options include:");
		System.out.println("     -v, -verbose:       Print a synopsis of standard options");
		System.out.println("     -h, -help, -?:      Output messages about what the program is doing \n");

	}

}
