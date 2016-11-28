package invertedPendulumControllerWithOutFilterChooise29April;

import java.io.*;
/**
 * A simple data logger that can be used to sample a sequence
 * of integer data values in a flash file. The
 * file is a text consosting of a sequence of lines with
 * a time value and a sample data value separated by a comma.
 * 
 * When data has been sampled the flash file can be transfered to a
 * PC by means of the tool nxjbrowse.
 * 
 * @author  Ole Caprani
 * @version 4.02.13
 */
public class DataLogger 
{
    private File f;
    private FileOutputStream fos;
    private int startTime;

    public DataLogger (String fileName)
    {
        try
        {	        
            f = new File(fileName);
            if( ! f.exists() )
            {
                f.createNewFile();
            }
            else
            {
                f.delete();
                f.createNewFile();
            }
             
            fos = new  FileOutputStream(f);
        }
        catch(IOException e)
        {
        	 e.printStackTrace();
           
            System.exit(0);
        }
        startTime = (int)System.currentTimeMillis();
    }
    
    
    /* Sets the variable startTime to the current time*/
    
    public void start()
    {
    	startTime = (int)System.currentTimeMillis();
    }
	/* Writes a sample to a txt file
	  */
    public void writeSample( int time, int sample )
    {
		
        try
        {
        	// Make a string from the time 
            Integer timeInt = new Integer(time);
            String timeString = timeInt.toString();
            
            // Make a string from the sample
            Integer sampleInt = new Integer(sample);
            String sampleString = sampleInt.toString();
            
            /* Writes the time in the txt file (fos) */
            for(int i=0; i<timeString.length(); i++)
            {
                fos.write((byte) timeString.charAt(i));
            }
            
            // Separate items with comma
            fos.write((byte)(','));
            
            /* Writes the sample to the txt file */
            for(int i=0; i<sampleString.length(); i++)
            {
                fos.write((byte) sampleString.charAt(i));
            }

            // New line, makes no sense why have the both
            fos.write((byte)('\r'));
            fos.write((byte)('\n'));				

        }
        catch(IOException e)
        {
        	  e.printStackTrace();
        }
    }
    
    public void writeSample( int sample )
    {
    	writeSample((int)System.currentTimeMillis() - startTime, sample);
    }
    
    public void close()
    {
        try
        {
            fos.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }		 
    }
    
    /* Prepares a new line for more data to be written */
    public void newLine(){
    	// New line, makes no sense why have the both
    	try
    	{
    		fos.write((byte)('\r'));
            fos.write((byte)('\n'));
    	}
    	catch(Exception e)
    	{
    		 e.printStackTrace();
           
    	}
    }
    
    /* Writes the single sample to the txt file and creates a comma at the end */
    public void writeSingleSample(int sample){
    	// Make a string form the sample
    	Integer sampleInt = new Integer(sample);
        String sampleString = sampleInt.toString();
    	
    	try
    	{
    		for(int i=0; i<sampleString.length(); i++)
            {
                fos.write((byte) sampleString.charAt(i));
            }	
    		 // Separate items with comma
            fos.write((byte)(','));
    	}
    	catch(Exception e)
    	{
    		 e.printStackTrace();
    	}
    }
}