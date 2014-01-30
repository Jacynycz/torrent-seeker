package TorrentSeeker.Parser;

import java.awt.TextArea;

public class Log {
	
	private static TextArea text;
	
	public Log(TextArea logArea){
		Log.text=logArea;
	}
	
	public static void log(String s){
		text.append(s+"\n");
	}

}
