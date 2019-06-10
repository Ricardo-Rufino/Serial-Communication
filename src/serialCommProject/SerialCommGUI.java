package serialCommProject;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.fazecast.jSerialComm.SerialPort;

import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.Scanner;
import java.awt.event.ActionEvent;

public class SerialCommGUI extends JFrame {

	private JPanel contentPane;
	static SerialPort chosenPort;
	static double x;
	static double y;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SerialCommGUI frame = new SerialCommGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public SerialCommGUI() {
		
		XYSeries series = new XYSeries("Arduino Reading");
		XYSeriesCollection dataset = new XYSeriesCollection(series);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 580, 388);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel leftPanel = new JPanel();
		contentPane.add(leftPanel, BorderLayout.WEST);
		leftPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		// Adding COM port list to comboBox-------------------------------------------------------------------------------------------------------------
		JComboBox portList = new JComboBox();
		leftPanel.add(portList);
		
		SerialPort portNames[] = SerialPort.getCommPorts();
		for(int i =  0; i < portNames.length; i++){
			portList.addItem(portNames[i].getSystemPortName());
		}
		//----------------------------------------------------------------------------------------------------------------------------------------------
		
		
		JButton connectButton = new JButton("Connect");
		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(connectButton.getText().equals("Connect")){
					//Will attempt to connect to serial port
					chosenPort = SerialPort.getCommPort(portList.getSelectedItem().toString());
					chosenPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
					
					if(chosenPort.openPort()){
						connectButton.setText("Disconnect");
						portList.setEnabled(false);
					}
					
					//Listens for incoming data and generates the graph
					Thread thread = new Thread(){
						@Override
						public void run(){
							Scanner scanner = new Scanner(chosenPort.getInputStream());
							
							while(scanner.hasNextLine()){
								try{
									String line   = scanner.nextLine();
									String[] data = line.split(",");
									
									x = Double.parseDouble(data[0]);
									y = Double.parseDouble(data[1]);
									series.add(x, y);
								}catch(Exception e){}
							}
							scanner.close();
						}
					};
					thread.start();
				}
				else{
					//Will attempt to disconnect from serial port
					chosenPort.closePort();
					portList.setEnabled(true);
					connectButton.setText("Connect");
					series.clear();
				}
			}
		});
		leftPanel.add(connectButton);
		
		JFreeChart chart = ChartFactory.createXYLineChart("Arduino Data", "Time (seconds)", "Voltage", dataset);
		contentPane.add(new ChartPanel(chart), BorderLayout.CENTER);
		
		
		
	}

}
