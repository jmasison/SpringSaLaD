
package org.springsalad.langevinsetup;

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;

public class ReactionNameTextField extends NameTextField {
    
    private final Reaction reaction;
    
    public ReactionNameTextField(Global g, Reaction reaction){
        super(reaction.getName(), 10);
        this.reaction = reaction;
        for(BindingReaction bindingReaction : g.getBindingReactions()){
            if(reaction != bindingReaction){
                super.addDisallowedName(bindingReaction.getName());
            }
        }
        for(TransitionReaction transitionReaction : g.getTransitionReactions()){
            if(reaction != transitionReaction){
                super.addDisallowedName(transitionReaction.getName());
            }
        }
        for(Molecule mol : g.getMolecules()){
            DecayReaction decayReaction = mol.getDecayReaction();
            if(reaction != decayReaction){
                super.addDisallowedName(decayReaction.getName());
            }
        }
    }
    
     @Override
    public void actionPerformed(ActionEvent event){
        this.removeFocusListener(this);
        if(super.nameOK()){
            reaction.setName(this.getText());
        } else {
            this.requestFocusInWindow();
        }
        this.addFocusListener(this);
    }
    
    @Override
    public void focusLost(FocusEvent event){
        if(super.nameOK()){
            reaction.setName(this.getText());
        } else {
            this.requestFocusInWindow();
        }
    }
    
}
