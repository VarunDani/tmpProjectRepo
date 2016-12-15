package com.walmart.intern.test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ItrAprchTestSuit {

	

    private ByteArrayOutputStream outStream;
    private ByteArrayOutputStream errStream;
    private PrintStream outOrig;
    private PrintStream errOrig;
    
	
	 	@Before
	    public void setUp() throws Exception {
	        outStream = new ByteArrayOutputStream();
	        PrintStream out = new PrintStream(outStream);
	        errStream = new ByteArrayOutputStream();
	        PrintStream err = new PrintStream(errStream);
	        outOrig = System.out;
	        errOrig = System.err;
	        System.setOut(out);
	        System.setErr(err);
	    }
	 	
	 	
	 	 @After
	     public void tearDown() throws Exception {
	         System.setOut(outOrig);
	         System.setErr(errOrig);
	     }
	 
	 	 
	 	private String getFileContent(String filename) {
	        String content = null;
	        try {
	            content = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return content;
	    }
	 	
	 	

	    @Test
	    public void mainTest1() throws Exception {
	    	
	    	String args[] = {};
	    	
	    	assertEquals("", errStream.toString().trim());
	    	
	    }
	 
}
