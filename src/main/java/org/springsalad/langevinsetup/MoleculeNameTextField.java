/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import java.awt.event.*;

public class MoleculeNameTextField extends NameTextField {
    
    private final Molecule molecule;
    
    public MoleculeNameTextField(Molecule molecule, Global g){
        super(molecule.getName(), 10);
        this.molecule = molecule;
        for(Molecule mol : g.getMolecules()){
            if(mol != molecule){
                super.addDisallowedName(mol.getName());
            }
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent event){
        this.removeFocusListener(this);
        if(super.nameOK()){
            molecule.setName(this.getText());
        } else {
            this.requestFocusInWindow();
        }
        this.addFocusListener(this);
    }
    
    @Override
    public void focusLost(FocusEvent event){
        if(super.nameOK()){
            molecule.setName(this.getText());
        } else {
            this.requestFocusInWindow();
        }
    }
    
    
}
