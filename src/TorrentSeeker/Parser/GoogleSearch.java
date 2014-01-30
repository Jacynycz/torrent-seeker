package TorrentSeeker.Parser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import TorrentSeeker.UserInterface.MainWindow;

public final class GoogleSearch {

	public static ArrayList<String> busquedaGoogle(String busqueda, int paginas)
			throws IOException {

		String[] args = busqueda.split(" ");

		if (args.length == 0) {
			
			Log.log("usage: GoogleSearch query ...");
			
			return null;
		}
		
		ArrayList<String> lista = new ArrayList<String>(); 
		
		for (int i = 1; i <= paginas; i++) {
			final URL url = encodeGoogleQuery(args, i);
 
			Log.log("Downloading [" + url + "]...");
			
			final String html = downloadString(url);
 
			final List<URL> links = parseGoogleLinks(html);

			for (final URL link : links)
				lista.add(link.toString());
		}

		return lista;
	}

	private static String downloadString(final InputStream stream)
			throws IOException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		int ch;
		while (-1 != (ch = stream.read()))
			out.write(ch);
		return out.toString();
	}

	private static String downloadString(final URL url) throws IOException {
		final String agent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.122 Safari/534.30";
		final URLConnection connection = url.openConnection();
		connection.setRequestProperty("User-Agent", agent);
		final InputStream stream = connection.getInputStream();
		return downloadString(stream);
	}

	 
	private static URL encodeGoogleQuery(final String[] args, int pagina) {
		try {
			final StringBuilder localAddress = new StringBuilder();
			localAddress.append("/search?q=");

			for (int i = 0; i < args.length; i++) {
				final String encoding = URLEncoder.encode(args[i], "UTF-8");
				localAddress.append(encoding);
				if (i + 1 < args.length)
					localAddress.append("+");
			}
			int queryPagina = (pagina - 1) * 10;

			localAddress.append("&start=" + queryPagina);

			return new URL("http", MainWindow.googleUrl,
					localAddress.toString());

		} catch (final IOException e) { 
			throw new RuntimeException(
					"An error occurred while encoding the query arguments.");
		}
	}

	 
	private static List<URL> parseGoogleLinks(final String html)
			throws IOException {

 
		final String token1 = "<h3 class=\"r\">";
		final String token2 = "<a href=\"";
		final String token3 = "\"";

		final List<URL> links = new ArrayList<URL>();

		try { 
			int index = 0;
			while (-1 != (index = html.indexOf(token1, index))) {
				final int result = html.indexOf(token2, index);
				final int urlStart = result + token2.length();
				final int urlEnd = html.indexOf(token3, urlStart);
				final String urlText = html.substring(urlStart, urlEnd);
				final URL url = new URL(urlText);
				links.add(url);

				index = urlEnd + token3.length();
			}

			return links;

		} catch (final MalformedURLException e) {
			return links;
		} catch (final IndexOutOfBoundsException e) {
			throw new IOException("Failed to parse Google links.");
		}
	}
}