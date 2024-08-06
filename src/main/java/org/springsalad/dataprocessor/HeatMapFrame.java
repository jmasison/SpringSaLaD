

package org.springsalad.dataprocessor;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.*;

import org.springsalad.helpersetup.IOHelp;

public class HeatMapFrame extends JFrame implements ActionListener, ItemListener {
  
    private final File file;
    private int [][] counts;
    private int maxCount = 0;
    
    /************* PANELS ***************************/
    private JPanel linearPanel;
    private JPanel logPanel;
    
    /***************** MENU BAR COMPONENTS **************/
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem saveImageItem;
    private JMenu scaleMenu;
    private JCheckBoxMenuItem logItem;
    
    public HeatMapFrame(File file){
        super("Heat Map Frame");
        this.file = file;
        buildMenu();
        readCounts();
        buildLinearPanel();
        buildLogPanel();
        addPanel();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);
    }
    
    /* ************ BUILD MENU BAR **************************/
    private void buildMenu(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        menuBar = new JMenuBar();
        
        fileMenu = new JMenu("File");
        scaleMenu = new JMenu("Scale");
        
        saveImageItem = new JMenuItem("Save Imaage");
        logItem = new JCheckBoxMenuItem("Log scale");
        logItem.setSelected(false);
        
        saveImageItem.addActionListener(this);
        logItem.addItemListener(this);
        
        fileMenu.add(saveImageItem);
        scaleMenu.add(logItem);
        
        menuBar.add(fileMenu);
        menuBar.add(scaleMenu);
        
        this.setJMenuBar(menuBar);
        // </editor-fold>
    }
    
    private void readCounts(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        int totalSites = 0;
        BufferedReader br = null;
        FileReader fr = null;
        Scanner sc;
         try{
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            sc = new Scanner(br);
            // get the total number of sites
            Scanner firstLine = new Scanner(sc.nextLine());
            firstLine.useDelimiter(",");
            // Turns out the scanner knows to skip the blank entry
            // firstLine.next();
            while(firstLine.hasNext()){
                if(firstLine.next().trim().length() > 0){
                    totalSites += 1;
                }
            }
            counts = new int[totalSites][totalSites];
            int row = 0;
            while(sc.hasNextLine()){
                Scanner line = new Scanner(sc.nextLine());
                line.useDelimiter(",");
                line.next();
                int col = 0;
                while(line.hasNext()){
                    String next = line.next().trim();
                    if(next.length() > 0){
                        int count = Integer.parseInt(next);
                        counts[row][col] = count;
                        if(count > maxCount){
                            maxCount = count;
                        }
                        col++;
                    }
                }
                row++;
            }
        } catch(FileNotFoundException ioe){
           ioe.printStackTrace(System.out);
        } finally {
            if(br != null){
                try{
                    br.close();
                } catch(IOException bioe){
                    bioe.printStackTrace(System.out);
                }
            }
            if(fr != null){
                try{
                    fr.close();
                } catch(IOException fioe){
                    fioe.printStackTrace(System.out);
                }
            }
        }
        // </editor-fold>
    }
    
    private void buildLinearPanel(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        linearPanel = new JPanel();
        linearPanel.setBackground(Color.BLACK);
        linearPanel.setLayout(new GridLayout(counts[0].length, counts[0].length));
        for(int i=0;i<counts.length;i++){
            for(int j=0;j<counts.length;j++){
                JPanel p = new JPanel();
                int count = counts[i][j];
                // Normalize to 255
                count = (255*count)/maxCount;
                p.setBackground(new Color(255,255-count,255-count));
                p.setPreferredSize(new Dimension(10,10));
                linearPanel.add(p);
            }
        }
        // </editor-fold>
    }
    
    private void buildLogPanel(){
        logPanel = new JPanel();
        logPanel.setBackground(Color.BLACK);
        logPanel.setLayout(new GridLayout(counts[0].length, counts[0].length));
        for(int i=0;i<counts.length;i++){
            for(int j=0;j<counts.length;j++){
                JPanel p = new JPanel();
                int count = counts[i][j];
                // Normalize to 255
                if(count != 0){
                    double ratio = Math.log(count)/Math.log(maxCount);
                    count = (int)Math.round(255*ratio);
                }
                p.setBackground(new Color(255,255-count,255-count));
                p.setPreferredSize(new Dimension(10,10));
                logPanel.add(p);
            }
        }
    }
    
    public void addPanel(){
        Container c = this.getContentPane();
        c.removeAll();
        if(logItem.isSelected()){
            c.add(logPanel);
        } else {
            c.add(linearPanel);
        }
        c.validate();
        c.repaint();
    }
    
    @Override
    public void actionPerformed(ActionEvent event){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        JMenuItem source = (JMenuItem)event.getSource();

        if(source == saveImageItem){
            JFileChooser jfc = new JFileChooser();
            int pick = jfc.showSaveDialog(null);
            if(pick == JFileChooser.APPROVE_OPTION){
                File f = jfc.getSelectedFile();
                f = IOHelp.setFileType(f, "png");
                savePanelImage(f);
            }
        }
        // </editor-fold>
    }
    
    @Override
    public void itemStateChanged(ItemEvent event) {
        addPanel();
    }
    
    public void savePanelImage(File file){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        JPanel p;
        if(logItem.isSelected()){
            p = logPanel;
        } else {
            p = linearPanel;
        }
        Rectangle r = p.getBounds();
        try{
            BufferedImage bi = new BufferedImage(r.width, r.height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bi.createGraphics();
            p.paint(g);
            ImageIO.write(bi, "png", file);
            g.dispose();
        } catch(IOException ioe){
            ioe.printStackTrace(System.out);
        }
        // </editor-fold>
    }
    
    public static void main(String [] args){
        new HeatMapFrame(new File("C://Users/Paul/Documents/LangevinFolder/PolymerTest_40_SIMULATIONS/Strong Interaction_SIM_FOLDER/data/Run0/HeatMap.csv"));
    }
    
}
