package TorrentSeeker.UserInterface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Point;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import TorrentSeeker.Parser.Log;
import TorrentSeeker.Service.Service;

public class MainWindow extends JFrame {

	public static final String ver = "1.0 beta";
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField palabrasClave;
	private JTable tableTorrents;
	private JSpinner spinnerBuscar;
	private JProgressBar progressBar;
	private JLabel progressDoneLabel;
	private Panel panelOpciones;
	private static JButton btnAnalizar;
	private JPanel panelLog;
	private TextArea logArea;
	@SuppressWarnings("unused")
	private Log logger;
	private JTextField buscadorTexfield;
	private JTable table;
	public static String googleUrl;
	private static AtomicBoolean stop;
	private static JButton btnCancelar;
	public static boolean analizando;
	public static Label errorLabel;
	private static TextArea errorLogArea;
	public static JTabbedPane tabbedPane;
	private static JSpinner spinnerTimeout;
	private static int timeout;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public MainWindow() {
		setResizable(false);
		setTitle("Torrent Seeker");
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// This will only be seen on standard output.
				cerrarChiringuito();
			}
		});
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 992, 490);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnMenu = new JMenu("Menu");
		menuBar.add(mnMenu);

		JMenuItem mntmReiniciarPrograma = new JMenuItem("Reiniciar Programa");
		mntmReiniciarPrograma.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				reiniciar();
				reiniciarTablas();
			}
		});

		JMenuItem mntmAcercaDeDestripator = new JMenuItem(
				"Acerca de Torrent Seeker");
		mntmAcercaDeDestripator.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showver();
			}
		});
		mnMenu.add(mntmAcercaDeDestripator);
		mnMenu.add(mntmReiniciarPrograma);
		contentPane = new JPanel();
		contentPane.setToolTipText("Resultados que se mostraran en la tabla");
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		String[] cabeceras = new String[3];
		cabeceras[0] = "PAGE TITLE";
		cabeceras[1] = "TORRENT";
		cabeceras[2] = "URL";
		TableModel modelTorrentTable = new DefaultTableModel(new Object[][] {},
				cabeceras) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] { String.class, String.class,
					String.class };

			@SuppressWarnings({ "rawtypes", "unchecked" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}

			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBorder(null);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		contentPane.add(tabbedPane);

		panelOpciones = new Panel();
		tabbedPane.addTab("Buscar Torrents", null, panelOpciones, null);
		panelOpciones.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setBackground(Color.WHITE);
		panel.setBounds(10, 9, 317, 176);
		panelOpciones.add(panel);
		panel.setLayout(null);

		spinnerBuscar = new JSpinner();
		spinnerBuscar.setBounds(241, 65, 60, 18);
		panel.add(spinnerBuscar);
		spinnerBuscar.setToolTipText("N\u00FAmero de paginas");
		spinnerBuscar.setModel(new SpinnerNumberModel(20, 10, 1000, 10));

		Label labelresultados = new Label("N\u00BA de resultados a analizar");
		labelresultados.setFont(new Font("Dialog", Font.BOLD, 12));
		labelresultados.setBounds(10, 61, 205, 22);
		panel.add(labelresultados);

		JLabel lblOcionesDeBusqueda = new JLabel(
				"Opciones de busqueda principal");
		lblOcionesDeBusqueda.setHorizontalAlignment(SwingConstants.CENTER);
		lblOcionesDeBusqueda.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblOcionesDeBusqueda.setBounds(10, 0, 291, 55);
		panel.add(lblOcionesDeBusqueda);

		JEditorPane dtrpnSeleccionaElNmero_1 = new JEditorPane();
		dtrpnSeleccionaElNmero_1.setFont(new Font("Arial", Font.PLAIN, 11));
		dtrpnSeleccionaElNmero_1
				.setText("Selecciona el n\u00FAmero de resultados de google que el \r\nprograma utlizar\u00E1 para extraer las las palabras relacionadas.\r\n");
		dtrpnSeleccionaElNmero_1.setEditable(false);
		dtrpnSeleccionaElNmero_1.setBounds(10, 94, 301, 38);
		panel.add(dtrpnSeleccionaElNmero_1);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_1.setBackground(Color.WHITE);
		panel_1.setBounds(656, 11, 305, 296);
		panelOpciones.add(panel_1);
		panel_1.setLayout(null);

		Label buscador = new Label("http://");
		buscador.setBounds(10, 123, 41, 22);
		panel_1.add(buscador);

		buscadorTexfield = new JTextField();
		buscadorTexfield.setBounds(57, 123, 238, 20);
		panel_1.add(buscadorTexfield);
		buscadorTexfield.setText("www.google.es");
		buscadorTexfield.setColumns(10);

		JScrollPane scrollPaneGoogle = new JScrollPane();
		scrollPaneGoogle.setBounds(10, 151, 285, 108);
		panel_1.add(scrollPaneGoogle);

		table = new JTable();
		scrollPaneGoogle.setViewportView(table);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				buscadorTexfield.setText((String) table.getValueAt(
						table.getSelectedRow(), 0));
			}
		});
		table.setModel(new DefaultTableModel(new Object[][] {
				{ "www.google.com" }, { "www.google.es" },
				{ "www.google.co.uk" }, { "www.google.fr" },
				{ "www.google.co.in" }, },
				new String[] { "Dominios de google" }

		) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] { String.class };

			@SuppressWarnings({ "rawtypes", "unchecked" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}

			public boolean isCellEditable(int row, int column) {
				return false;
			}
		});

		JLabel lblOcionesDeBusqueda_1 = new JLabel(
				"Opciones de busqueda en google");
		lblOcionesDeBusqueda_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblOcionesDeBusqueda_1.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblOcionesDeBusqueda_1.setBounds(10, 0, 280, 43);
		panel_1.add(lblOcionesDeBusqueda_1);

		JEditorPane dtrpnSeleccionaElDominio = new JEditorPane();
		dtrpnSeleccionaElDominio.setFont(new Font("Arial", Font.PLAIN, 11));
		dtrpnSeleccionaElDominio
				.setText("Selecciona el dominio de google en el que se buscar\u00E1n las \r\npalabras clave para encontrar los torrents:");
		dtrpnSeleccionaElDominio.setBounds(10, 70, 291, 47);
		panel_1.add(dtrpnSeleccionaElDominio);

		Label label_3 = new Label("New label");
		label_3.setBounds(906, -340, 62, 22);
		panel_1.add(label_3);

		JPanel panel_2 = new JPanel();
		panel_2.setBackground(Color.WHITE);
		panel_2.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_2.setBounds(337, 11, 309, 174);
		panelOpciones.add(panel_2);
		panel_2.setLayout(null);

		JLabel lblOcionesDeParseo = new JLabel("Opciones de parseo web");
		lblOcionesDeParseo.setBounds(27, 6, 257, 19);
		lblOcionesDeParseo.setHorizontalAlignment(SwingConstants.CENTER);
		lblOcionesDeParseo.setFont(new Font("Tahoma", Font.BOLD, 15));
		panel_2.add(lblOcionesDeParseo);

		Label label_2 = new Label(
				"Tiempo m\u00E1ximo en analizar una p\u00E1gina:");
		label_2.setFont(new Font("Dialog", Font.BOLD, 12));
		label_2.setBounds(10, 60, 225, 22);
		panel_2.add(label_2);

		spinnerTimeout = new JSpinner();
		spinnerTimeout.setModel(new SpinnerNumberModel(10, 1, 300, 5));
		spinnerTimeout.setToolTipText("N\u00FAmero de paginas");
		spinnerTimeout.setBounds(242, 60, 47, 18);
		panel_2.add(spinnerTimeout);

		JEditorPane dtrpnSeleccionaElTiempo = new JEditorPane();
		dtrpnSeleccionaElTiempo.setFont(new Font("Arial", Font.PLAIN, 11));
		dtrpnSeleccionaElTiempo
				.setText("Selecciona el tiempo m\u00E1ximo (en segundos) que va a \r\nemplear el programa en analizar una pagina web.\r\nSi la respuesta supera este tiempo, el programa pasa\r\na la siguiente web.");
		dtrpnSeleccionaElTiempo.setBounds(10, 88, 291, 75);
		panel_2.add(dtrpnSeleccionaElTiempo);

		JPanel panel_3 = new JPanel();
		panel_3.setBounds(10, 239, 636, 68);
		panelOpciones.add(panel_3);
		panel_3.setLayout(null);

		Label labelPalabrasClave = new Label("Busqueda de Torrents:");
		labelPalabrasClave.setFont(new Font("Dialog", Font.PLAIN, 17));
		labelPalabrasClave.setBounds(10, 16, 171, 32);
		panel_3.add(labelPalabrasClave);

		palabrasClave = new JTextField();
		palabrasClave.setFont(new Font("Tahoma", Font.PLAIN, 15));
		palabrasClave.setBounds(187, 16, 301, 32);
		panel_3.add(palabrasClave);
		palabrasClave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				click();
			}
		});
		palabrasClave.setColumns(10);

		btnAnalizar = new JButton("Buscar Torrents");
		btnAnalizar.setFont(new Font("Arial", Font.BOLD, 11));
		btnAnalizar.setBounds(498, 16, 128, 32);
		panel_3.add(btnAnalizar);
		btnAnalizar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				click();
			}

		});

		tableTorrents = new JTable();
		tableTorrents.setToolTipText("Haz doble click en la tabla para abrir el navegador");
		tableTorrents.setColumnSelectionAllowed(true);
		tableTorrents.setCellSelectionEnabled(true);
		tableTorrents
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		tableTorrents.setModel(modelTorrentTable);

		JScrollPane panelTorrents = new JScrollPane(tableTorrents);
		panelTorrents.setToolTipText("Haz doble click en la tabla para abrir el navegador");
		tableTorrents.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2){ 
				 	int row = tableTorrents.rowAtPoint(new Point(e.getX(), e.getY()));
		            int col = tableTorrents.columnAtPoint(new Point(e.getX(), e.getY()));
		            String url = (String) tableTorrents.getModel().getValueAt(row, col);
		            if (col == 2 || col == 1){
			            try {
							Desktop.getDesktop().browse(new URL(url).toURI());
						} catch (Exception ex) {
							MainWindow.report(ex.getMessage(), 1);
						}
		            }
				}
				else{
					MainWindow.errorLabel.setText("Haz doble click en un link para abrir el navegador");
				}
			}
		});
		tabbedPane.addTab("Torrents encontrados", null, panelTorrents, null);

		panelLog = new JPanel();
		tabbedPane.addTab("Log", null, panelLog, null);
		GridBagLayout gbl_panelLog = new GridBagLayout();
		gbl_panelLog.columnWidths = new int[] { 963, 0 };
		gbl_panelLog.rowHeights = new int[] { 56, 56, 56, 0, 0, 0, 0, 56, 0,
				56, 56, 56, 0 };
		gbl_panelLog.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_panelLog.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panelLog.setLayout(gbl_panelLog);

		JLabel label_4 = new JLabel("");
		GridBagConstraints gbc_label_4 = new GridBagConstraints();
		gbc_label_4.fill = GridBagConstraints.BOTH;
		gbc_label_4.insets = new Insets(0, 0, 5, 0);
		gbc_label_4.gridx = 0;
		gbc_label_4.gridy = 2;
		panelLog.add(label_4, gbc_label_4);

		Label label_1 = new Label("Log de ejecuci\u00F3n");
		GridBagConstraints gbc_label_1 = new GridBagConstraints();
		gbc_label_1.fill = GridBagConstraints.BOTH;
		gbc_label_1.insets = new Insets(0, 0, 5, 0);
		gbc_label_1.gridx = 0;
		gbc_label_1.gridy = 4;
		panelLog.add(label_1, gbc_label_1);

		logArea = new TextArea();
		GridBagConstraints gbc_logArea = new GridBagConstraints();
		gbc_logArea.fill = GridBagConstraints.BOTH;
		gbc_logArea.insets = new Insets(0, 0, 5, 0);
		gbc_logArea.gridx = 0;
		gbc_logArea.gridy = 5;
		panelLog.add(logArea, gbc_logArea);

		logger = new Log(logArea);

		Label label = new Label("Log de Errores:");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.fill = GridBagConstraints.BOTH;
		gbc_label.insets = new Insets(0, 0, 5, 0);
		gbc_label.gridx = 0;
		gbc_label.gridy = 6;
		panelLog.add(label, gbc_label);
		errorLogArea = new TextArea();
		GridBagConstraints gbc_errorLogArea = new GridBagConstraints();
		gbc_errorLogArea.fill = GridBagConstraints.BOTH;
		gbc_errorLogArea.insets = new Insets(0, 0, 5, 0);
		gbc_errorLogArea.gridx = 0;
		gbc_errorLogArea.gridy = 7;
		panelLog.add(errorLogArea, gbc_errorLogArea);

		JLabel label_6 = new JLabel("");
		GridBagConstraints gbc_label_6 = new GridBagConstraints();
		gbc_label_6.fill = GridBagConstraints.BOTH;
		gbc_label_6.gridx = 0;
		gbc_label_6.gridy = 11;
		panelLog.add(label_6, gbc_label_6);

		JPanel panel_4 = new JPanel();
		contentPane.add(panel_4, BorderLayout.SOUTH);
		panel_4.setLayout(new BorderLayout(0, 0));

		JPanel panel_5 = new JPanel();
		panel_4.add(panel_5, BorderLayout.SOUTH);
		panel_5.setLayout(null);

		JPanel panel_6 = new JPanel();
		panel_4.add(panel_6, BorderLayout.NORTH);

		errorLabel = new Label("");
		panel_6.add(errorLabel);
		errorLabel.setForeground(Color.red);

		JPanel panel_7 = new JPanel();
		panel_4.add(panel_7, BorderLayout.EAST);

		btnCancelar = new JButton("Cancelar");
		panel_7.add(btnCancelar);
		btnCancelar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnCancelar.setEnabled(false);
				MainWindow.reiniciar();
				MainWindow.setStop(true);
			}
		});
		btnCancelar.setEnabled(false);

		JPanel panel_8 = new JPanel();
		panel_4.add(panel_8, BorderLayout.WEST);

		progressDoneLabel = new JLabel("");
		panel_8.add(progressDoneLabel);

		progressBar = new JProgressBar(0, 100);
		panel_4.add(progressBar, BorderLayout.CENTER);
		progressBar.setFont(new Font("Tahoma", Font.BOLD, 12));
		progressBar.setStringPainted(true);
		stop = new AtomicBoolean();
		timeout = 30;
	}

	public JTable getTablaMonogramas() {
		return tableTorrents;
	}

	public static void reiniciar() {
		btnAnalizar.setEnabled(true);
		btnCancelar.setEnabled(false);
		analizando = false;
	}

	public void reiniciarTablas() {
		DefaultTableModel model = (DefaultTableModel) tableTorrents.getModel();
		model.setRowCount(0);
	}

	public void cerrarChiringuito() {
	}

	public static void setBoton(Boolean estado) {
		btnAnalizar.setEnabled(estado);
	}

	public static Boolean stopped() {
		return stop.get();
	}

	public static void setStop(Boolean estado) {
		stop.set(estado);
	}

	public static void report(String s, int prioridad) {
		String err = "";
		switch (prioridad) {
		case 1:
			MainWindow.errorLabel.setForeground(Color.red);
			err = "Error Grave: ";
			break;
		case 2:
			MainWindow.errorLabel.setForeground(Color.getHSBColor((float) 0.1,
					(float) 1.0, (float) 0.51));
			err = "Error Leve: ";
			break;
		default:
			MainWindow.errorLabel.setForeground(Color.black);
			err = "Error: ";
			break;
		}
		MainWindow.errorLabel.setText(err + s);
		MainWindow.errorLogArea.append(err + s + "\n");
	}

	public static void cambiarPestaña(int i) {
		try {
			MainWindow.tabbedPane.setSelectedIndex(i);
		} catch (Exception e) {
			MainWindow.tabbedPane.setSelectedIndex(-1);
		}
	}

	public static int getTimeout() {
		return MainWindow.timeout;
	}

	public static void showver() {
		JFrame frame = new JFrame();
		JOptionPane.showMessageDialog(frame, "Versión actual: " + ver);
	}

	public void click() {
		if (analizando || palabrasClave.getText().equalsIgnoreCase("")) {
			MainWindow.errorLabel.setForeground(Color.getHSBColor((float) 0.1,
					(float) 1.0, (float) 0.51));
			MainWindow.errorLabel
					.setText("Debes de poner las palabras claves a buscar");
			return;
		}
		try {
			MainWindow.timeout = (Integer) MainWindow.spinnerTimeout.getValue() * 1000;
			Log.log("Timeout set to: " + MainWindow.timeout + " miliseconds");
			analizando = true;

			MainWindow.setBoton(false);
			btnCancelar.setEnabled(true);
			setStop(false);
			googleUrl = this.buscadorTexfield.getText();
			String palabras = palabrasClave.getText() + " torrent";
			int busqueda = (Integer) spinnerBuscar.getValue();
			DefaultTableModel model = (DefaultTableModel) tableTorrents
					.getModel();
			model.setRowCount(0);
			new Service(tableTorrents, palabras, busqueda,
					progressBar, progressDoneLabel).start();
		} catch (Exception e) {
			progressBar.setValue(0);
			progressDoneLabel.setText("Terminado.");
			MainWindow.reiniciar();
		}
	}
}
