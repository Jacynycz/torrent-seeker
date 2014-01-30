package TorrentSeeker.Exceptions;

import TorrentSeeker.Parser.Log;

public class NotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String que;
	private String donde;
	public NotFoundException(String que,String donde){
		this.que=que;
		this.donde=donde;
	}
	
	public void print(){
		Log.log(que+" not found in "+ donde);
	}
}
