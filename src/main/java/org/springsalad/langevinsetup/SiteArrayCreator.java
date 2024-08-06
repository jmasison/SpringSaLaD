/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import java.awt.Color;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JOptionPane;

import org.springsalad.helpersetup.SiteArrayParser;

public class SiteArrayCreator extends javax.swing.JFrame {

    private final HashMap<String, SiteType> typeMap;
    private final HashMap<String, State> stateMap;
    private final ArrayList<SiteType> types;
    // private final ArrayList<Site> siteArray;
    private final ArrayList<Link> linkArray;
    
    private final ArrayList<Site> newSites;
    private final ArrayList<Link> newLinks;
    
    private final Molecule molecule;
    
    public SiteArrayCreator(Molecule molecule) {
        initComponents();
        this.molecule = molecule;
        this.types = molecule.getTypeArray();
        // this.siteArray = molecule.getSiteArray();
        this.linkArray = molecule.getLinkArray();
        String location = molecule.getLocation();
        typeMap = new HashMap<>(100);
        stateMap = new HashMap<>(100);
        newSites = new ArrayList<>();
        newLinks = new ArrayList<>();
        // System.out.println(location);
        if(location.equals(SystemGeometry.INSIDE)){
            extraTF.setEnabled(false);
            extraTF.setBackground(Color.lightGray);
        } else if(location.equals(SystemGeometry.OUTSIDE)){
            intraTF.setEnabled(false);
            intraTF.setBackground(Color.lightGray);
        }
        
        setup();
        
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
    
    private void setup(){
       
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<types.size();i++){
            SiteType type = types.get(i);
            String index = Integer.toString(i+1);
            typeMap.put(index, type);
            sb.append(index).append(" : ").append(type.getName()).append("\n");
            ArrayList<State> states = type.getStates();
            // If no letter is given we use the first defined state.
            stateMap.put(index, states.get(0));
            for(int j=0;j<states.size();j++){
                State state = states.get(j);
                String letter = SiteArrayParser.lowercaseString(j);
                typeMap.put(index+letter, type);
                stateMap.put(index+letter, state);
                sb.append("     ").append(letter).append(" : ").append(state.getName()).append("\n");
            }
            sb.append("\n");
        }
        typeTA.setText(sb.toString());
    }
    
    private boolean checkInputString(String s){
        boolean passed = true;
        
        // First check to make sure it expands correctly.
        if(!SiteArrayParser.checkInput(s)){
            JOptionPane.showMessageDialog(this, "Error: Text was not in the proper format.", "String Format Error", JOptionPane.ERROR_MESSAGE);
            passed = false;
        } else {
            // Now expand it and make sure that each value maps to a type and a state
            Scanner input = new Scanner(SiteArrayParser.expandInput(s));
            while(input.hasNext()){
                String key = input.next();

                SiteType stype = typeMap.get(key);
                State sstate = stateMap.get(key);
                
                if(stype == null || sstate == null){
                    JOptionPane.showMessageDialog(this, "Error: The key " + key + " does not map to a type or a state.", "Input Key Error", JOptionPane.ERROR_MESSAGE);
                    passed = false;
                }    
            }
            input.close();
        }
        // System.out.println("checkInputString returned " + passed);
        return passed;
    }
    
    private void makeNewArrays(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        String intraString = intraTF.getText();
        String extraString = extraTF.getText();
        
        ArrayList<Site> intraSites = new ArrayList<>();
        ArrayList<Site> extraSites = new ArrayList<>();
        
        double gap = Double.parseDouble(gapTF.getText());
        
        Scanner sc;
        
        if(intraTF.isEnabled()){
            if(checkInputString(intraString)){
                // System.out.println("Looked good in makeNewArrays().");
                sc = new Scanner(SiteArrayParser.expandInput(intraString));
                while(sc.hasNext()){
                    String key = sc.next();
                    Site site = new Site(molecule, typeMap.get(key));
                    site.setInitialState(stateMap.get(key));
                    site.setLocation(SystemGeometry.INSIDE);
                    intraSites.add(site);
                }
                sc.close();
            } 
        }
        
        if(extraTF.isEnabled()){
            if(checkInputString(extraString)){
                sc = new Scanner(SiteArrayParser.expandInput(extraString));
                while(sc.hasNext()){
                    String key = sc.next();
                    Site site = new Site(molecule, typeMap.get(key));
                    site.setInitialState(stateMap.get(key));
                    site.setLocation(SystemGeometry.OUTSIDE);
                    extraSites.add(site);
                }
                sc.close();
            }
        }
        
        Site site1;
        Site site2;
        Link link;
        for(int i=0;i<intraSites.size();i++){
            site1 = intraSites.get(i);
            newSites.add(site1);
            if(i == 0){
                site1.setZ(4);
                site1.setY(4);
            } else {
                site2 = intraSites.get(i-1);
                site1.setZ(site2.getZ() + site2.getRadius() + gap + site1.getRadius());
                site1.setY(4);
                link = new Link(site1, site2);
                newLinks.add(link);
            }
        }
        
        if(!intraSites.isEmpty() && !extraSites.isEmpty()){
            // TOD0: Get location of membrane. Shift it to the position
            // between the external and internal sites. 
            // DOING NOTHING FOR NOW.  I guess this means that adding linear
            // array of sites is not implemented for membrane molecules.
        }
        
        for(int i=0;i<extraSites.size();i++){
            site1 = extraSites.get(i);
            newSites.add(site1);
            if(i == 0){
                site1.setZ(4);
                site1.setY(4);
            } else {
                site2 = extraSites.get(i-1);
                site1.setZ(site2.getZ() + site2.getRadius() + gap + site1.getRadius());
                site1.setY(4);
                link = new Link(site1, site2);
                newLinks.add(link);
            }
        }
        // </editor-fold>
    }
    
    private void makeNewHelixArrays(double radius, double pitch){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        String intraString = intraTF.getText();
        String extraString = extraTF.getText();
        
        ArrayList<Site> intraSites = new ArrayList<>();
        ArrayList<Site> extraSites = new ArrayList<>();
        
        double gap = Double.parseDouble(gapTF.getText());
        
        Scanner sc;
        
        if(intraTF.isEnabled()){
            if(checkInputString(intraString)){
                // System.out.println("Looked good in makeNewArrays().");
                sc = new Scanner(SiteArrayParser.expandInput(intraString));
                while(sc.hasNext()){
                    String key = sc.next();
                    Site site = new Site(molecule, typeMap.get(key));
                    site.setInitialState(stateMap.get(key));
                    site.setLocation(SystemGeometry.INSIDE);
                    intraSites.add(site);
                }
                sc.close();
            } 
        }
        
        if(extraTF.isEnabled()){
            if(checkInputString(extraString)){
                sc = new Scanner(SiteArrayParser.expandInput(extraString));
                while(sc.hasNext()){
                    String key = sc.next();
                    Site site = new Site(molecule, typeMap.get(key));
                    site.setInitialState(stateMap.get(key));
                    site.setLocation(SystemGeometry.OUTSIDE);
                    extraSites.add(site);
                }
                sc.close();
            }
        }
        
        Site site1;
        Site site2;
        Link link;
        double oldRadius = 0, currentRadius = 0, s = 0; // s is arclength
        for(int i=0;i<intraSites.size();i++){
            site1 = intraSites.get(i);
            newSites.add(site1);
            if(i == 0){
                site1.setX(radius);
                site1.setY(0);
                site1.setZ(10);
                oldRadius = site1.getRadius();
            } else {
                currentRadius = site1.getRadius();
                site2 = intraSites.get(i-1);
                double distance2 = (currentRadius + oldRadius +gap)*(currentRadius + oldRadius +gap);
                s += find_ds(distance2, radius*radius, pitch*pitch);
                site1.setX(radius*Math.cos(2*Math.PI*s));
                site1.setY(radius*Math.sin(2*Math.PI*s));
                site1.setZ(pitch*s + 10);
                link = new Link(site1, site2);
                newLinks.add(link);
                oldRadius = currentRadius;
            }
        }
        
        if(!intraSites.isEmpty() && !extraSites.isEmpty()){
            // TOD0: Get location of membrane. Shift it to the position
            // between the external and internal sites. 
            // DOING NOTHING FOR NOW.  I guess this means that adding linear
            // array of sites is not implemented for membrane molecules.
        }
        
        for(int i=0;i<extraSites.size();i++){
            site1 = extraSites.get(i);
            newSites.add(site1);
            if(i == 0){
                site1.setX(radius);
                site1.setY(0);
                site1.setZ(10);
                oldRadius = site1.getRadius();
            } else {
                currentRadius = site1.getRadius();
                site2 = intraSites.get(i-1);
                double distance2 = (currentRadius + oldRadius +gap)*(currentRadius + oldRadius +gap);
                s += find_ds(distance2, radius*radius, pitch*pitch);
                site1.setX(radius*Math.cos(2*Math.PI*s));
                site1.setY(radius*Math.sin(2*Math.PI*s));
                site1.setZ(pitch*s + 10);
                link = new Link(site1, site2);
                newLinks.add(link);
                oldRadius = currentRadius;
            }
        }
        // </editor-fold>
    }
    
    private void addNewSitesAndLinks(){
        molecule.addSiteArray(newSites);
        for(Link link : newLinks){
            linkArray.add(link);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jCheckBox1 = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        typeTA = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        extraTF = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        intraTF = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        makeButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        gapTF = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        helixBox = new javax.swing.JCheckBox();
        jLabel7 = new javax.swing.JLabel();
        helixRadiusTF = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        helixPitchTF = new javax.swing.JTextField();

        jCheckBox1.setText("jCheckBox1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Input Site Array");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Availabe Types");

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        typeTA.setColumns(20);
        typeTA.setRows(5);
        jScrollPane2.setViewportView(typeTA);

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Intracellular Array String");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Extracellular Array String");

        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane3.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane3.setViewportView(extraTF);

        jScrollPane4.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane4.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane4.setViewportView(intraTF);

        makeButton.setText("Make Array");
        makeButton.setPreferredSize(new java.awt.Dimension(100, 23));
        makeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                makeButtonActionPerformed(evt);
            }
        });
        jPanel1.add(makeButton);

        cancelButton.setText("Cancel");
        cancelButton.setPreferredSize(new java.awt.Dimension(100, 23));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        jPanel1.add(cancelButton);

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Gap distance between sites (nm):");
        jPanel2.add(jLabel4);

        gapTF.setColumns(6);
        gapTF.setText("1.0");
        gapTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gapTFActionPerformed(evt);
            }
        });
        jPanel2.add(gapTF);

        jLabel2.setText("Create as helix:");
        jPanel3.add(jLabel2);

        helixBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helixBoxActionPerformed(evt);
            }
        });
        jPanel3.add(helixBox);

        jLabel7.setText("Radius (nm):");
        jPanel3.add(jLabel7);

        helixRadiusTF.setColumns(4);
        helixRadiusTF.setText("10");
        helixRadiusTF.setEnabled(false);
        helixRadiusTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helixRadiusTFActionPerformed(evt);
            }
        });
        jPanel3.add(helixRadiusTF);

        jLabel8.setText("Pitch (nm):");
        jPanel3.add(jLabel8);

        helixPitchTF.setColumns(4);
        helixPitchTF.setText("10");
        helixPitchTF.setEnabled(false);
        jPanel3.add(helixPitchTF);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane3)
                    .addComponent(jScrollPane4)
                    .addComponent(jScrollPane2)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 68, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void makeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_makeButtonActionPerformed
        // TODO add your handling code here:
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        if(intraTF.isEnabled() && extraTF.isEnabled()){
            // System.out.println("Both enabled.");
            if(checkInputString(intraTF.getText()) && checkInputString(extraTF.getText())){
                if(helixBox.isSelected()){
                    makeNewHelixArrays(Double.parseDouble(helixRadiusTF.getText()),
                            Double.parseDouble(helixPitchTF.getText()));
                } else {
                    makeNewArrays();
                }
                addNewSitesAndLinks();
                this.dispose();
            }
        } else if(intraTF.isEnabled()){
            // System.out.println("Only intra enabled.");
            if(checkInputString(intraTF.getText())){
                // System.out.println("About to make new arrays.");
                if(helixBox.isSelected()){
                    makeNewHelixArrays(Double.parseDouble(helixRadiusTF.getText()),
                            Double.parseDouble(helixPitchTF.getText()));
                } else {
                    makeNewArrays();
                }
                // System.out.println("Made new arrays.");
                addNewSitesAndLinks();
                // System.out.println("Added sites and links.");
                this.dispose();
            }
        } else if(extraTF.isEnabled()){
            // System.out.println("Only extra enabled.");
            if(checkInputString(extraTF.getText())){
                if(helixBox.isSelected()){
                    makeNewHelixArrays(Double.parseDouble(helixRadiusTF.getText()),
                            Double.parseDouble(helixPitchTF.getText()));
                } else {
                    makeNewArrays();
                }
                addNewSitesAndLinks();
                this.dispose();
            }
        }
        // </editor-fold>        
    }//GEN-LAST:event_makeButtonActionPerformed

    private void gapTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gapTFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_gapTFActionPerformed

    private void helixBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helixBoxActionPerformed
        // TODO add your handling code here:
        if(helixBox.isSelected()){
            helixRadiusTF.setEnabled(true);
            helixPitchTF.setEnabled(true);
        } else {
            helixRadiusTF.setEnabled(false);
            helixPitchTF.setEnabled(false);
        }
        
    }//GEN-LAST:event_helixBoxActionPerformed

    private void helixRadiusTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helixRadiusTFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_helixRadiusTFActionPerformed

    // Distance between centers, radius of helix, pitch of helix
    // Assume the distance between the centers satisfies d^2 < 4*r^2 + p^2, 
    // otherwise there is no solution.
    // If it does satisfy this, since the distances as a function of t is
    // monotonically increasing in this range, we can just step forward until
    // we pass the d, then turn around in smaller steps, and repeat, until
    // we've converged on the correct t.
    
    // Need the function d^2 = g(t).
    private double g(double t, double r2, double p2){
        return 2.0*r2*(1-Math.cos(2*Math.PI*t)) + p2*t*t;
    }
    
    private double find_ds(double distance2, double r2, double p2){
        double s = 0;
        double ds = 0.01;
        double currentDelta = distance2;  // g(0) = 0
        double newDelta;
        while(currentDelta > 0.0001){
            s += ds;
            newDelta = Math.abs(distance2 - g(s,r2,p2));
            if(newDelta > currentDelta){
                ds = -ds/10;
            }
            if(s < 1e-8){ // It's probably zero
                return Math.abs(s);
            }
            if(0.5 - s < 1e-8){ // Turn around, we hit 0.5
                ds = -ds/10;
            }
            currentDelta = newDelta;
        }
        return s;
    }
    
    public static void main(String [] args){
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextField extraTF;
    private javax.swing.JTextField gapTF;
    private javax.swing.JCheckBox helixBox;
    private javax.swing.JTextField helixPitchTF;
    private javax.swing.JTextField helixRadiusTF;
    private javax.swing.JTextField intraTF;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JButton makeButton;
    private javax.swing.JTextArea typeTA;
    // End of variables declaration//GEN-END:variables
}
