package TorrentSeeker.Exceptions;

import TorrentSeeker.Parser.Log;

public class VoidListException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String lista;
	
	public VoidListException(String lista){
		this.lista=lista;
	}
	
	public void print(){
		Log.log(lista+" is void");
	}
}
