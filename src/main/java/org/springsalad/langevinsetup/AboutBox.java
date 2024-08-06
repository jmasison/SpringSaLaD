package org.springsalad.langevinsetup;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class AboutBox extends JPanel {

	private JLabel iconLabel = null;
	private JLabel copyright = null;
	private JLabel appName = null;
	private JLabel buildNumber = null;
	private JLabel version = null;
	
	private JDialog dialog = null;
	
	public AboutBox() {
		
		setLayout(new GridBagLayout());

		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridheight = GridBagConstraints.REMAINDER;
		gbc.insets = new Insets(0,0,4,4);
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		add(getIconLabel(), gbc);

		gbc = attributionConstraints(2,gridy);
		add(getAppName(), gbc);

		gbc = attributionConstraints(0,++gridy);
		add(getVersion(), gbc);

		gbc = attributionConstraints(0,++gridy);
		add(getBuildNumber(), gbc);

		gbc = attributionConstraints(0,++gridy);
		add(getCopyright(), gbc);

		gbc = attributionConstraints(10,++gridy);
		add(new JLabel("<html>Virtual Cell is Supported by NIH Grant R24 GM137787 from the<br/> National Institute for General Medical Sciences.</html>"), gbc);

		gbc = attributionConstraints(10,++gridy);
		for(Action action : actions) { 
			add(new JButton(action), gbc); 
		}
		

		setFocusable(true);
	}	
	
	
	
	
	private GridBagConstraints attributionConstraints(int topOffset, int gridy) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(topOffset,4,0,4);
		gbc.anchor = GridBagConstraints.LINE_START;
		return gbc;
}	
	
	private JLabel getIconLabel() {
		if (iconLabel == null) {
			try {
				iconLabel = new JLabel();
				iconLabel.setName("IconLabel");
				iconLabel.setIcon(new ImageIcon(getClass().getResource("/images/springSaLaDLarge.png")));
				iconLabel.setText("");
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return iconLabel;
	}
	
	private JLabel getCopyright() {
		if (copyright == null) {
			try {
				copyright = new JLabel();
				copyright.setName("Copyright");
				copyright.setText("(c) Copyright 1998-2020 UConn Health");
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return copyright;
	}

	private JLabel getAppName() {
		if (appName == null) {
			try {
				appName = new JLabel();
				appName.setName("AppName");
				appName.setText("<html><u>Virtual Cell</u></html>");
				appName.setForeground(Color.blue);
				appName.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
//						DialogUtils.browserLauncher(DocumentWindowAboutBox.this, BeanUtils.getDynamicClientProperties().getProperty(PropertyLoader.VCELL_URL), "Failed to open VCell web page (" + System.getProperty(PropertyLoader.VCELL_URL) + ")");
						
					}
				});
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return appName;
	}

	private JLabel getVersion() {
		if (version == null) {
			try {
				version = new JLabel("2.2");
				version.setName("Version");
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return version;
	}

	private JLabel getBuildNumber() {
		if (buildNumber == null) {
			try {
				buildNumber = new JLabel("2.2");
				buildNumber.setName("BuildNumber");
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return buildNumber;
	}

	
	protected Action closeAction = new AbstractAction("Cancel") {
		public void actionPerformed(ActionEvent e) {
			AboutBox.this.disposeDialog();
		}
	};
	protected final List<Action> actions = Arrays.asList(closeAction);

	
	
	private void handleException(Throwable exception) {
		System.out.println("--------- UNCAUGHT EXCEPTION ---------");
		exception.printStackTrace(System.out);
	}

	public void showDialog(JFrame parent, String title) {
		boolean modal = true;
		dialog = new JDialog(parent, title, modal);
		dialog.add(this);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
	}
	
	public void disposeDialog() { if(dialog != null) { dialog.dispose(); } }

	
}
