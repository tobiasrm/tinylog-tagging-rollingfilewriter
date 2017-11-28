package com.github.tobiasrm;

import org.pmw.tinylog.Logger;


/** Main class for color demo. 
 */
public class Main {
	
	public static void main(String[] args) {
		final String customTag1  = "[[customTag1]]";
		final String customTag2  = "[[customTag2]]";
		final String customTag3  = "[[customTag3]]";
		final String customTag4  = "[[customTag4]]";
		final String customTag5  = "[[customTag5]]";
		final String customTag6  = "[[customTag6]]";
		final String customTag7  = "[[customTag7]]";
		final String customTag8  = "[[customTag8]]";
		final String customTag9  = "[[customTag9]]";
		final String customTag10 = "[[customTag10]]";
		
		String msg = "Lorem ipsum " + customTag1 + "dolor sit amet" + customTag2 + ", consectetur adipiscing \u001B[33m" + customTag3 + "\u001B[37melit,";
		System.out.println("Original output (via sysout)     - " + msg);
		Logger.info(       "Written with tagging-filewriter  - Lorem ipsum " + customTag1 + "dolor sit amet" + customTag2 + ", consectetur adipiscing " + customTag3 + "elit,");

		// template for individual testing of all tags
//		Logger.info(customTag1 + " " + 
//				customTag2+ " " + 
//				customTag3 + " " + 
//				customTag4 + " " + 
//				customTag5 + " " + 
//				customTag6 + " " + 
//				customTag7 + " " + 
//				customTag8 + " " + 
//				customTag9 + " " + 
//				customTag10 );
		
		System.out.println("Compare this log output with the generated 'log.txt'");
	}

}
