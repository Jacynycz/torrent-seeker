package TorrentSeeker.Service;

import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import TorrentSeeker.Parser.GoogleSearch;
import TorrentSeeker.Parser.Log;
import TorrentSeeker.Parser.Parser;
import TorrentSeeker.UserInterface.MainWindow;

public class Service extends Thread {

	private int maxBusqueda;

	private JTable torrentTable;

	private String palabrasClave;

	private JProgressBar progreso;

	private JLabel info;
	
	public static String pageName;

	public Service(JTable torrentTable, String palabrasClave,
			int maxBusqueda, JProgressBar progreso, JLabel info) {
		this.palabrasClave = palabrasClave;
		this.torrentTable = torrentTable;
		this.maxBusqueda = (int) maxBusqueda / 10;
		this.progreso = progreso;
		this.info = info;
		pageName="";
	}

	private void introduceDatos(JTable output, ArrayList<String> input,
			String url) {
		int i = 0;
		for (String torrent : input) {
			if (output.getRowCount() <= i) {
				DefaultTableModel model = (DefaultTableModel) output.getModel();
				model.addRow(new Object[] { (String)pageName,(String)torrent,(String) url });
		}
			i++;
		}

	}

	public static void setPageName(String name){
		pageName=name;
	}
	
	public void run() {
		Parser parser = new Parser();
		String busqueda = palabrasClave;
		info.setText("Buscando los primeros " + maxBusqueda * 10
				+ " resultados en " + MainWindow.googleUrl);
		ArrayList<String> urls = null;
		try {
			urls = GoogleSearch.busquedaGoogle(busqueda, maxBusqueda);
		} catch (Exception e) {
			Log.log("Error Buscando en google " + e.getMessage());
			MainWindow.report("Error: " + e.getMessage(), 1);
			MainWindow.reiniciar();
			progreso.setValue(0);
			info.setText("Terminado.");
			return;
		}
		int max = urls.size();
		int act = 0;
		ArrayList<String> torrentList = new ArrayList<String>();
		progreso.setMaximum(max);
		info.setText("Analizando Urls de Google(" + act + "/" + max + ")...");
		progreso.setValue(0);
		for (String url : urls) {
			act++;
			if (MainWindow.stopped()) {
				MainWindow.reiniciar();
				progreso.setValue(0);
				info.setText("Terminado.");
				introduceDatos(torrentTable, torrentList, url);
				MainWindow.cambiarPestaña(1);
				return;
			}
			info.setText("Analizando Urls de Google(" + act + "/" + max
					+ ")...");
			Log.log("Analizando pagina: " + url);
			parser.extractAllTorrents(torrentList, url, 3);
			progreso.setValue(progreso.getValue() + 1);
			introduceDatos(torrentTable, torrentList, url);
		}
		info.setText("Analisis terminado.");
		progreso.setValue(progreso.getMinimum());
		MainWindow.reiniciar();
		MainWindow.cambiarPestaña(1);
	}
}
