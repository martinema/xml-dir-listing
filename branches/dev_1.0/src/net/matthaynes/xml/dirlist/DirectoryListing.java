package net.matthaynes.xml.dirlist;

import java.io.*;
import java.util.*;

public final class DirectoryListing {

	// Main function
	public static void main(String aArguments[]) throws FileNotFoundException, IOException {

		// Get specified directory
		File dir = new File(aArguments[0]);


		// Set output stream for generated file
		FileOutputStream out = new FileOutputStream("test.xml");

		// Begin listing
		XmlDirectoryListing lister = new XmlDirectoryListing();
		lister.generateXmlDirectoryListing(dir, out);

		// Close output stream
		out.close();

	}

}