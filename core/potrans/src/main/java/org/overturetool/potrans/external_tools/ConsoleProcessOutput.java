/**
 * 
 */
package org.overturetool.potrans.external_tools;

/**
 * @author miguel_ferreira
 *
 */
public class ConsoleProcessOutput {
	
	protected StringBuffer outputBuffer = new StringBuffer();
	
	public void appendOutput(String output) {
		outputBuffer.append(output);
	}
	
	public void appendErrorTrace(Exception e) {
		outputBuffer.append(e.getStackTrace());
	}
	
	public String getOutput() {
		return outputBuffer.toString();
	}
}
