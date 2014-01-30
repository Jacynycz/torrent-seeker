package TorrentSeeker.Parser;

import java.util.ArrayList;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import TorrentSeeker.Exceptions.VoidListException;
import TorrentSeeker.Service.Service;
import TorrentSeeker.Tables.VisitTable;
import TorrentSeeker.UserInterface.MainWindow;

public class Parser {
	private static String rootURL;
	private static final String ua = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.122 Safari/534.30";
	private String actualUrl;
	private VisitTable links;
	private int profundidad;

	public Parser() {
	}

	private void getTorrents(Document doc, ArrayList<String> torrentList) {
		Elements links = doc.select("a[href~=.torrent]");
		String torrentLink;
		for (Element link : links) {
			if (MainWindow.stopped())
				return;
			torrentLink = link.attr("href");
			if (torrentLink.endsWith(".torrent")
					|| torrentLink.contains(".torrent?")) {
				if(torrentLink.startsWith("http://"))
					torrentList.add(torrentLink);
				else {
					torrentList.add(Parser.rootURL+torrentLink);
				}
			}
		}
	}

	private void extractRootURL(String url) {
		String[] aux = url.split("/");
		if(aux.length<1) return;
		else{
			if(aux[0].equalsIgnoreCase("http:")){
				Parser.rootURL = "http://"+aux[2];	
			}
		}
	}

	public static String getRootURL(){
		return Parser.rootURL;
	}
	
	public static Boolean checkDomainURL(String s) {
		String rootURL=getRootURL();
		String altRootURL=rootURL.replace("www.", "");
		if (s.length() == 0)
			return false;
		if (s.startsWith(rootURL))
			return true;
		if (s.startsWith(altRootURL))
			return true;
		else
			return false;
	}

	private void extractLinks(String url, VisitTable table,
			ArrayList<String> torrentList) {
		Document doc = null;
		int timeout = MainWindow.getTimeout();
		try {
			doc = Jsoup.connect(url).userAgent(ua).timeout(timeout).get();
		} catch (Exception e) {
			MainWindow.report(e.getMessage() + " en " + url, 2);
			return;
		}
		Service.setPageName(doc.title());
		Elements links = doc.getElementsByTag("a");

		for (Element link : links) {
			String linkHref = link.attr("href");
			table.addOne(linkHref);
		}

		this.getTorrents(doc, torrentList);
	}

	public void extractAllTorrents(ArrayList<String> torrentList, String url, int profundidad) {

		this.links = new VisitTable();
		this.extractRootURL(url);
		this.actualUrl = url;
		this.extractLinks(actualUrl, links, torrentList);
		this.profundidad=profundidad;
		try {
			this.actualUrl = this.links.visitNext();
		} catch (VoidListException lv) {
			this.actualUrl = "";
		}
		this.profundidad=0;
		iteraBusqueda(torrentList, this.profundidad);
	}

	private void iteraBusqueda(ArrayList<String> torrentList, int i) {
		if (this.actualUrl.equalsIgnoreCase("") || this.profundidad >= i)
			return;
		this.profundidad++;
		this.extractLinks(actualUrl, links, torrentList);

		try {
			this.actualUrl = this.links.visitNext();
		} catch (VoidListException e) {
			this.actualUrl = "";
		}

		iteraBusqueda(torrentList,this.profundidad);
	}

}
