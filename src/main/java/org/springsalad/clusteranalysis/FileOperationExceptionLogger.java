package org.springsalad.clusteranalysis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;

public class FileOperationExceptionLogger {
	public static void main(String[] args) {
		FileOperationExceptionLogger logger = new FileOperationExceptionLogger("");
		logger.logFileReadParseException("blah                  read", new Exception("can't read file: blah blah blah path"));
		logger.logFileWriteException(new Exception("can't write file: blah blah blah path"));
		logger.displayLogGUI();
		logger.writeLogFile("C:\\Users\\imt_w\\Downloads");
				
	}
	
    public static void justDisplayException(String extraMessage, Exception exception){
    	StringBuilder sb = new StringBuilder();
		Throwable t = exception;
		while (true) {
			sb.append(t.toString());
			if (t.getCause() != null) {
				sb.append(System.lineSeparator()).append("Caused by: ");
				t = t.getCause();
			}
			else {
				break;
			}
		}
    	JOptionPane.showMessageDialog(null, sb, extraMessage, JOptionPane.ERROR_MESSAGE);
    }
    
    private final List<Object[]> fileReadParseExceptionLog;
    private final List<Exception> fileWriteExceptionLog;
    private final String name;
    
    public FileOperationExceptionLogger(String name) {
    	this.name = name;
    	fileReadParseExceptionLog = new ArrayList<>();
    	fileWriteExceptionLog = new ArrayList<>();
    }
    
    public void logFileReadParseException(String locationStr, Exception exception) {
    	fileReadParseExceptionLog.add(new Object[] {locationStr,exception});
    }
    
    public void logFileWriteException(Exception exception) {
    	fileWriteExceptionLog.add(exception);
    }
    
    public void displayLogGUI() {
    	if (fileReadParseExceptionLog.size() == 0 && fileWriteExceptionLog.size() == 0) {
    		return;
    	}
    	
    	JFrame jFrame = new JFrame(name + " exception logger");
    	jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));
    	jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	
    	if (fileReadParseExceptionLog.size() != 0) {
	    	JLabel rLabel = new JLabel(String.format("<html>There are %d file READ/PARSE exceptions logged in the %s exception logger."
	    			+ "<br>If you want to, you can fix the data and have the data recalculated.</html>", fileReadParseExceptionLog.size(), name));
	    	rLabel.setIcon(UIManager.getIcon("OptionPane.errorIcon"));
	    	rLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    	
	    	JTable rTable = new JTable(new FileReadParseExceptionLogTableModel());
	    	rTable.setFillsViewportHeight(true);
	    	JScrollPane rScrollPane = new JScrollPane(rTable);
	    	rScrollPane.setPreferredSize(new Dimension(350,200));
	    	
	    	JPanel rPanel = new JPanel();
	    	//rPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	    	rPanel.setLayout(new BorderLayout());
	    	rPanel.add(rLabel, BorderLayout.NORTH);
	    	rPanel.add(rScrollPane, BorderLayout.CENTER);   
	    	jFrame.add(rPanel);
    	}
    	
    	if (fileWriteExceptionLog.size() != 0) {
	    	JLabel wLabel = new JLabel(String.format("<html>There are %d file WRITE exceptions logged in the %s exception logger."
	    			+ "<br>If you want to, you can have the data recalculated.</html>", fileWriteExceptionLog.size(), name));
	    	wLabel.setIcon(UIManager.getIcon("OptionPane.errorIcon"));
	    	wLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    	JTable wTable = new JTable(new FileWriteExceptionLogTableModel());
	    	wTable.setFillsViewportHeight(true);
	    	JScrollPane wScrollPane = new JScrollPane(wTable);
	    	wScrollPane.setPreferredSize(new Dimension(350,200));
	    	
	    	JPanel wPanel = new JPanel();
	    	wPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	    	wPanel.setLayout(new BorderLayout());
	    	wPanel.add(wLabel, BorderLayout.NORTH);
	    	wPanel.add(wScrollPane, BorderLayout.CENTER);  
	    	jFrame.add(wPanel);
    	}

    	jFrame.pack();
    	jFrame.setVisible(true);
    }
    
    public void writeLogFile(String dataFolder) {
    	if (fileReadParseExceptionLog.size() == 0 && fileWriteExceptionLog.size() == 0) {
    		return;
    	}
    	String currentTimeStr = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).replace(':','.');
    	if (fileReadParseExceptionLog.size() != 0) {
	    	Path filePath = Paths.get(dataFolder, currentTimeStr + "_" + name + "_file_read_parse_exception_log.txt");
	    	try {
	    		PrintWriter pw = new PrintWriter(filePath.toString());
	    		for (Object[] entry: fileReadParseExceptionLog) {
	    			((Exception) entry[1]).printStackTrace(pw);
	    			pw.println();
	    		}
	    		if (pw.checkError()) {
	    			throw new IOException();
	    		}
	    	}
	    	catch (IOException ioe) {
	    		JOptionPane.showMessageDialog(null, "Some or all of the log might not have been written into the log file: \n" + filePath.toString(),
	    				"IO Exception When Writing Exception Log File", JOptionPane.ERROR_MESSAGE);
	    	}
    	}
    	if (fileWriteExceptionLog.size() != 0) {
	    	Path filePath = Paths.get(dataFolder,  currentTimeStr + "_" + name + "_file_write_exception_log.txt");
	    	try {
	    		PrintWriter pw = new PrintWriter(filePath.toString());
	    		for (Exception entry: fileWriteExceptionLog) {
	    			entry.printStackTrace(pw);
	    			pw.println();
	    		}
	    		if (pw.checkError()) {
	    			throw new IOException();
	    		}
	    	}
	    	catch (IOException ioe) {
	    		JOptionPane.showMessageDialog(null, "Some or all of the log might not have been written into the log file: \n" + filePath.toString(),
	    				"IO Exception When Writing Exception Log File", JOptionPane.ERROR_MESSAGE);
	    	}
    	}
    }

    private class FileReadParseExceptionLogTableModel extends AbstractTableModel{
   	
		@Override
		public int getRowCount() {
			return fileReadParseExceptionLog.size();
		}

		@Override
		public int getColumnCount() {
			return 3;
		}
		
		@Override
		public String getColumnName(int col) {
			switch (col) {
				case 0:
					return "Location";
				case 1:
					return "Exception";
				case 2:
					return "Details";
				default:
					return "";
			}
		}
		
		@Override 
		public Class<?> getColumnClass(int col) {
			return String.class;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch (columnIndex) {
				case 0:
					return (String) fileReadParseExceptionLog.get(rowIndex)[0];
				case 1:
					return ((Exception) fileReadParseExceptionLog.get(rowIndex)[1]).getClass().getName();
				case 2:
					return ((Exception) fileReadParseExceptionLog.get(rowIndex)[1]).getMessage();
				default:
					return "";
			}
		}
    }

    private class FileWriteExceptionLogTableModel extends AbstractTableModel{
       	
		@Override
		public int getRowCount() {
			return fileWriteExceptionLog.size();
		}

		@Override
		public int getColumnCount() {
			return 2;
		}
		
		@Override
		public String getColumnName(int col) {
			switch (col) {
				case 0:
					return "Exception";
				case 1:
					return "Details";
				default:
					return "";
			}
		}
		
		@Override 
		public Class getColumnClass(int col) {
			return String.class;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch (columnIndex) {
				case 0:
					return fileWriteExceptionLog.get(rowIndex).getClass().getName();
				case 1:
					return fileWriteExceptionLog.get(rowIndex).getLocalizedMessage();
				default:
					return "";
			}
		}
    }

}
