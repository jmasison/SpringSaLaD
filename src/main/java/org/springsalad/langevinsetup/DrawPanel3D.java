/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.langevinsetup;

import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.behaviors.vp.OrbitBehavior;
import org.jogamp.java3d.utils.geometry.*;
import org.jogamp.java3d.utils.picking.PickCanvas;
import org.jogamp.java3d.utils.picking.PickResult;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.java3d.utils.universe.ViewingPlatform;
import org.jogamp.vecmath.*;
import org.springsalad.helpersetup.Colors;
import org.springsalad.helpersetup.NamedColor;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;


public class DrawPanel3D extends Canvas3D implements MouseListener, 
        KeyListener, MoleculeSelectionListener, RotateUpdateListener, MouseMotionListener {

    // The molecule we're showing
    private final Molecule molecule;
    
    // Arrays of the current spheres and cylinders
    private final ArrayList<SiteSphere> spheres = new ArrayList<>();
    private final ArrayList<LinkCylinder> cylinders = new ArrayList<>();
    
    /* ********  Arrays of selected sites and links ******/
    private final ArrayList<Site> selectedSites = new ArrayList<>();
    private final ArrayList<Link> selectedLinks = new ArrayList<>();
    
    /* *******  Map from site to sphere  **/
    // The sphere holds a reference to the site, so it's easy to go the other way.
    private final HashMap<Site, SiteSphere> siteToSphere = new HashMap<>(100);
    
    /* ******* Map from link to cylinder **/
    private final HashMap<Link, LinkCylinder> linkToCylinder = new HashMap<>(100);
    
    /* We need a simple universe, a branchgroup, and a bounding sphere */
    SimpleUniverse su;
    BranchGroup rootBG;
    BoundingSphere bounds;
    
    /* ******* The branchgroup holding the membrane ********************/
    private final BranchGroup membraneGroup = new BranchGroup();
    
    /* **** A pick canvas to pick with the mouse *******/
    private final PickCanvas pickCanvas;
    
    /* **** Easiest to make the graphics configuration static *******/
    static GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
    
    // Pre-defined vectors
    public final static Vector3d x_axis = new Vector3d(1.0, 0, 0);
    public final static Vector3d y_axis = new Vector3d(0, 1.0, 0);
    public final static Vector3d z_axis = new Vector3d(0, 0, 1.0);
    
    // Membrane size
    private float xsize = 25;  // half the membrane size
    private float ysize = 25;  // half the membrane size
    
    /* Boolean to indicate if the ctrl key is pressed */
    private boolean ctrlPressed = false;
    
    /* ********* Array of MoleculeSelectionListeners to notify *******/
    private final ArrayList<MoleculeSelectionListener> listeners = new ArrayList<>();
    
    /* **** Integration **** */
    private ActionListener intListener;
    private boolean drag = false; //drag event
    private Matrix3f m3 = new Matrix3f();
    
    public DrawPanel3D(Molecule molecule){
        super(config);
        this.setPreferredSize(new Dimension(1000,500));
                
        this.molecule = molecule;
        if(molecule.hasAnchorSites()){
            molecule.translate(0, 0, -molecule.membranePosition());
        }
        
        // Adjust the size of the membrane to contain the molecule's anchors
        for(Site site : molecule.getAnchorSites()){
            if(Math.abs(site.getX() + site.getRadius()) > xsize){
                xsize = (float)Math.abs(site.getX() + site.getRadius());
            }
            if(Math.abs(site.getY() + site.getRadius()) > ysize){
                ysize = (float)Math.abs(site.getY() + site.getRadius());
            }
        }
        xsize = 1.1f*xsize;
        ysize = 1.1f*ysize;
        
        su = new SimpleUniverse(this);
        rootBG = new BranchGroup();
        rootBG.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        rootBG.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        rootBG.setCapability(BranchGroup.ALLOW_DETACH);
        
        makeMembrane();
        
        // View properties
        View view = this.getView();
        view.setFrontClipDistance(0.001);
        view.setBackClipDistance(4000);
        
        // bounding sphere
        bounds = new BoundingSphere(new Point3d(0,0,0), 10000f);
        
        // make directional white light
        Vector3f lightDirection = new Vector3f(-1.0f, -2.0f, -1.0f);
        DirectionalLight light1 = new DirectionalLight(Colors.WHITE3D, lightDirection);
        light1.setInfluencingBounds(bounds);
        rootBG.addChild(light1);
        
        // make ambient dim light
        AmbientLight ambientLight = new AmbientLight(Colors.GRAY3D);
        ambientLight.setInfluencingBounds(bounds);
        rootBG.addChild(ambientLight);
        
        setInitialPosition(new Point3d(0, 0, 30));
        setOrbitControls();
        
        su.addBranchGraph(rootBG);
        
        pickCanvas = new PickCanvas(this, rootBG);
        pickCanvas.setMode(PickCanvas.GEOMETRY);
        
        moleculeSetUp();
        
        // This panel should listen for its own events
        this.addKeyListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
       
    }
    
    private void moleculeSetUp(){
        for(Link link : molecule.getLinkArray()){
            addLink(link);
        }
        for(Site site : molecule.getSiteArray()){
            addSite(site);
        }
    }
    
    void setInitialPosition(Point3d point){
    	ViewingPlatform vp = su.getViewingPlatform();
        TransformGroup vpTG = vp.getViewPlatformTransform();
        
        Transform3D vpt3d = new Transform3D();
        vpTG.getTransform(vpt3d);
        
        vpt3d.lookAt(point, new Point3d(0,0,0), new Vector3d(0,1,0));
        vpt3d.invert();
        vpTG.setTransform(vpt3d);
    }
    
    private void setOrbitControls(){
        OrbitBehavior orbit = new OrbitBehavior(this, OrbitBehavior.REVERSE_ROTATE | OrbitBehavior.PROPORTIONAL_ZOOM | OrbitBehavior.STOP_ZOOM | OrbitBehavior.REVERSE_TRANSLATE);
        orbit.setSchedulingBounds(bounds);
        orbit.setMinRadius(2.0);
        orbit.setTransFactors(100.0, 100.0);
        su.getViewingPlatform().setViewPlatformBehavior(orbit);
    }
    
    private void makeMembrane(){
        float zmem = 0.1f;
        Appearance boxTopAp = new Appearance();
        Appearance boxSideAp = new Appearance();
        Color3f yellow = Colors.YELLOW3D;
        Color3f black = Colors.BLACK3D;
        Color3f cyan = Colors.CYAN3D;
        Material top = new Material(yellow, black, yellow, black, 50.0f);
        Material sides = new Material(cyan, black, cyan, black, 30.0f);
        boxTopAp.setMaterial(top);
        boxSideAp.setMaterial(sides);
        Box box = new Box(xsize, ysize, zmem, boxSideAp);
        box.setPickable(false);
        box.getShape(Box.FRONT).setAppearance(boxTopAp);
        
        // Make box transformgroup
        Transform3D boxt3d = new Transform3D();
        boxt3d.setTranslation(new Vector3f(0f,0f,-zmem));
        TransformGroup boxTG = new TransformGroup(boxt3d);
        
        boxTG.addChild(box);
        membraneGroup.addChild(boxTG);
    }
    
    public void addMembrane(boolean bool){
        if(bool){
            rootBG.addChild(membraneGroup);
        } else {
            membraneGroup.detach();
        }
    }
    
    private Group makeAxis(double length, NamedColor col){
        TransformGroup topTG = new TransformGroup();
        // All these will glow
        Appearance ap = new Appearance();
        ap.setMaterial(new Material(col.getColor3f(), Colors.BLACK3D, col.getColor3f(), Colors.BLACK3D, 40.0f));
        
        RenderingAttributes ra = new RenderingAttributes();
        ra.setDepthBufferEnable(true);
        ap.setRenderingAttributes(ra);
        
        // Make a cylinder
        Cylinder y_cyl = new Cylinder(0.02f, (float) length, ap);
        for(double i=-length/2;i<=length/2;i++){
            Vector3d shift = new Vector3d(0,i,0);
            Transform3D t3d = new Transform3D();
            t3d.setTranslation(shift);
            TransformGroup xTG = new TransformGroup(t3d);
            xTG.addChild(new Cone(0.02f, 0.05f, ap));
            topTG.addChild(xTG);
        }
        
        topTG.addChild(y_cyl);
        
        return topTG;
        
    }
    
    private BranchGroup makeAxes(double lengthY, double lengthX, double lengthZ){
        TransformGroup topTG = new TransformGroup();
        // Add y-axis directly
        topTG.addChild(makeAxis(lengthY, Colors.GREEN));
        // Make and add x-axis
        Transform3D xt3d = new Transform3D();
        xt3d.setRotation(new AxisAngle4d(x_axis, Math.PI/2));
        TransformGroup xTG = new TransformGroup(xt3d);
        xTG.addChild(makeAxis(lengthZ,Colors.BLUE));
        topTG.addChild(xTG);
        
        // Make and add z-axis
        Transform3D zt3d = new Transform3D();
        zt3d.setRotation(new AxisAngle4d(z_axis, -Math.PI/2));
        TransformGroup zTG = new TransformGroup(zt3d);
        zTG.addChild(makeAxis(lengthX,Colors.MAGENTA));
        topTG.addChild(zTG);
        
        BranchGroup tempBG = new BranchGroup();
        tempBG.addChild(topTG);
        return tempBG;
    }
    
    public void systemSetup(){
        addMembrane(molecule.getLocation().equals(SystemGeometry.MEMBRANE));
        rootBG.addChild(makeAxes(molecule.getMaxY() + 10, molecule.getMaxX() + 10, molecule.getMaxZ() + 10));
    }
    
    private void unHighlightSelected(){
        for(Site aSite : selectedSites){
            siteToSphere.get(aSite).highlight(false);
        }
        for(Link link : selectedLinks){
            linkToCylinder.get(link).highlight(false);
        }
    }
    
    private void clearSelectedLists(){
        selectedSites.clear();
        selectedLinks.clear();
    }
    
    /* ************ MOUSE LISTENER METHODS ******************************/
    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mousePressed(MouseEvent e) {
        pickCanvas.setShapeLocation(e);
        PickResult result = pickCanvas.pickClosest();
        
        if(result != null){      
            Primitive p = (Primitive)result.getNode(PickResult.PRIMITIVE);
            
            try{
                Sphere aSphere = (Sphere) p;
                for(SiteSphere sphere : spheres){
                    if(sphere.getSphere() == aSphere){
                        Site site = sphere.getSite();
                        if(ctrlPressed){
                            sphere.highlight(!sphere.isHighlighted());
                            if(!selectedSites.contains(site)){
                                selectedSites.add(site);
                            } else {
                                selectedSites.remove(site);
                            }
                        } else {
                            if(sphere.isHighlighted()){
                                unHighlightSelected();
                                clearSelectedLists();
                            } else {
                                unHighlightSelected();
                                clearSelectedLists();
                                sphere.highlight(true);
                                selectedSites.add(site);
                            }
                        }
                        notifyListeners();
                        break;
                    }
                }
                
            } catch(ClassCastException cce){
                // Not a sphere. Do nothing.
            }
            
            try{
                Cylinder aCylinder = (Cylinder)p;
                for(LinkCylinder cylinder : cylinders){
                    if(cylinder.getCylinder() == aCylinder){
                        Link link = cylinder.getLink();
                        if(ctrlPressed){
                            cylinder.highlight(!cylinder.isHighlighted());
                            if(!selectedLinks.contains(link)){
                                selectedLinks.add(link);
                            } else {
                                selectedLinks.remove(link);
                            }
                        } else {
                            if(cylinder.isHighlighted()){
                                unHighlightSelected();
                                clearSelectedLists();
                            } else {
                                unHighlightSelected();
                                clearSelectedLists();
                                cylinder.highlight(true);
                                selectedLinks.add(link);
                            }
                        }
                        break;
                    }
                }
            } catch(ClassCastException cce){
                // Not a cylinder. Do nothing.
            }
        } else {
            if(!ctrlPressed){
                unHighlightSelected();
                clearSelectedLists();
            }
        }
       
        notifyListeners();
    }

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
    
    /* ************** KEY LISTENER METHODS **************************/
    
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.isControlDown() && !ctrlPressed){
            ctrlPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(ctrlPressed){
            ctrlPressed = false;
        }
    }
    
     /* ************ LISTEN FOR MOLECULE SELECTION EVENTS ************/
    
    @Override
    public void selectionOccurred(MoleculeSelectionEvent event){
        if(rootBG != membraneGroup.getParent()){
            addMembrane(molecule.getLocation().equals(SystemGeometry.MEMBRANE));
        }
        ArrayList<Site> currentSites = molecule.getSiteArray();
        ArrayList<Link> currentLinks = molecule.getLinkArray();
        // See if any sites were removed
        for(SiteSphere sphere : spheres){
            if(!currentSites.contains(sphere.getSite())){
                sphere.detach();
                siteToSphere.put(sphere.getSite(), null);
            }
        }
        // See if any links were removed
        for(LinkCylinder cylinder : cylinders){
            if(!currentLinks.contains(cylinder.getLink())){
                cylinder.detach();
                linkToCylinder.put(cylinder.getLink(), null);
            }
        }
        
        // Add any new sites or links
        for(Site site : molecule.getSiteArray()){
            if(siteToSphere.get(site) == null){
                addSite(site);
            }
        }
        for(Link link : molecule.getLinkArray()){
            if(linkToCylinder.get(link) == null){
                addLink(link);
            }
        }
        
        unHighlightSelected();
        clearSelectedLists();
        selectedSites.addAll(event.getSelectedSites());
        selectedLinks.addAll(event.getSelectedLinks());

        for(Site site : selectedSites){
            siteToSphere.get(site).highlight(true);
        }
        for(Link link : selectedLinks){
            linkToCylinder.get(link).highlight(true);
        }
        this.repaint();
    }
    
    /* MOLECULE SELECTION LISTENER NOTIFICATION AND RELATED METHODS *********/
    
    public void addMoleculeSelectionListener(MoleculeSelectionListener listener){
        listeners.add(listener);
    }
    
    public void removeMoleculeSelectionListener(MoleculeSelectionListener listener){
        listeners.remove(listener);
    }
    
    private void notifyListeners(){
        MoleculeSelectionEvent me = new MoleculeSelectionEvent(selectedSites, selectedLinks);
        for(MoleculeSelectionListener listener : listeners){
            listener.selectionOccurred(me);
        }
        
        if(drag){
        	RotationUpdateEvent re = new RotationUpdateEvent(this.m3, false); //replace 10.0 with coordinates knowledge of rotation
        	this.intListener.actionPerformed(re);
        	drag = false;
        }
        
    }
    
    /* ************* ADD AND REMOVE SITES ****************************/
    
    public void addSite(Site site){
        SiteSphere sphere = new SiteSphere(site);
        
        siteToSphere.put(site, sphere);
        spheres.add(sphere);
        
        rootBG.addChild(sphere);
    }
    
    
    /* ***************** ADD AND REMOVE LINKS ****************************/
    
    public void addLink(Link link){
        
        LinkCylinder cylinder = new LinkCylinder(link);
        
        linkToCylinder.put(link, cylinder);
        cylinders.add(cylinder);
        
        rootBG.addChild(cylinder);    
    }
    
    public void removeLink(Link link){
        
        LinkCylinder cylinder = linkToCylinder.get(link);
        
        linkToCylinder.remove(link);
        cylinders.remove(cylinder);
        
        rootBG.removeChild(cylinder);
    }
    
    
    /* ************ SHIFT SELECTED SITES *****************************/
    
    public void shiftSites(double dx, double dy, double dz){
       
        for(Site site : selectedSites){
            site.setX(site.getX() + dx);
            site.setY(site.getY() + dy);
            if(!site.getTypeName().equals(SiteType.ANCHOR)){
                site.setZ(site.getZ() + dz);
            }
            Vector3d vec = new Vector3d(site.getX(), site.getY(), site.getZ());
            SiteSphere sphere = siteToSphere.get(site);
            sphere.translate(vec);
        }
        
        for(Link link : molecule.getLinkArray()){
            LinkCylinder cylinder = linkToCylinder.get(link);
            cylinder.updatePosition();
        }
           
        notifyListeners();    
    }
    
/* ************ Link/site methods for Mol *****************************/
    
    public void addLinkToMol(Link link){
    	molecule.getLinkArray().add(link);
    	this.addLink(link);
        
        notifyListeners();   
    }
    
    public void removeLinkToMol(Link link){
        int index = link.getIndex();
    	molecule.getLinkArray().remove(index);
    	this.removeLink(link);
        
    	if(selectedLinks.contains(link)){
            selectedLinks.remove(link);
        }
    	
        notifyListeners(); 
    }
    
    public ArrayList<Site> getSelectedSites(){
    	return selectedSites;
    }
    
    public ArrayList<Link> getSelectedLinks(){
    	return selectedLinks;
    }

    public void updateRadius(ArrayList<Site> selectedSites, Double newRadius) {
    	ArrayList<Site> temp = new ArrayList<>();
    	for(Site s: selectedSites){
    		for(Site m: molecule.getSiteArray()){
    			if( m.getTypeName().equals(s.getTypeName()) )
    				temp.add(m);
    		}}

    	for(Site s: temp){
    		selectedSites.add(s);
    	}

    	for(Site s: selectedSites){
    		spheres.remove(siteToSphere.get(s));
    		rootBG.removeChild(siteToSphere.get(s));
    		siteToSphere.remove(s);

    		molecule.getSite(s.getIndex()).getType().setRadius(newRadius);

    		SiteSphere sphere = new SiteSphere(s);
    		siteToSphere.put(s, sphere);
    		spheres.add(sphere);
    		rootBG.addChild(sphere);
    	}
    		
    	notifyListeners();
	}
    
    /* ************ Rotation integration methods *****************************/
    
    //integration listener
    public void setintListener(ActionListener al){
		this.intListener = al;
	}
    
    @Override
	public void rotationOccurred(RotationUpdateEvent event) {
    	//2
    	// update knowledge of rotation, to be accessed in notify listeners
    	this.m3 = event.getM3();
		if(event.notifyPanel()){
			notifyListeners();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		//1
		// get position
    	ViewingPlatform vp = su.getViewingPlatform();
        TransformGroup vpTG = vp.getViewPlatformTransform();
        
        Transform3D vpt3d = new Transform3D();
        vpTG.getTransform(vpt3d);
        vpt3d.get(m3); //sets m3
		m3.transpose();
				
		//TODO maybe redraw the view based on order
		//this.repaint();
		
		drag = true;
		this.rotationOccurred(new RotationUpdateEvent(m3, true));	    
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}
	
	/* ************ Overlap warning methods *****************************/
	
	public HashMap<Site, Integer> getOverlaps(){
		HashMap<Site, Integer> tReturn = new HashMap<Site, Integer>();
		int size = this.molecule.getSiteArray().size();
		
		for(int i = 0; i < size; i++){
			for(int j = i + 1; j < size; j++){
				Site s1 = this.molecule.getSite(i);
				Site s2 = this.molecule.getSite(j);
				if(s1.getRadius() + s2.getRadius() >= distanceToEU(s1, s2)){
					if(tReturn.containsKey(s1)) {
						int val = tReturn.get(s1) + 1;
						tReturn.put(s1, val);
					}else {
						tReturn.put(s1, 1);
					}
					
					if(tReturn.containsKey(s2)) {
						int val = tReturn.get(s2) + 1;
						tReturn.put(s2, val);
					}else {
						tReturn.put(s2, 1);
					}
				}
			}
		}
		return tReturn;
	}
	
	private double distanceToEU(Site s1, Site s2){
		return Math.sqrt(
				Math.pow(s1.getX() - s2.getX(), 2) + 
				Math.pow(s1.getY() - s2.getY(), 2) + 
				Math.pow(s1.getZ() - s2.getZ(), 2)
				);
	}

	
	/* ************ Overlap warning methods *****************************/
	
	public void flip(int i){
		for(Link l: molecule.getLinkArray()){
			LinkCylinder cylinder = linkToCylinder.get(l);
	        cylinders.remove(cylinder);   
	        rootBG.removeChild(cylinder);
		}
		
		if(i == 0){
			for(Site s: molecule.getSiteArray()){
				if(!s.getTypeName().equals(SiteType.ANCHOR)){
					spheres.remove(siteToSphere.get(s));
					rootBG.removeChild(siteToSphere.get(s));
					siteToSphere.remove(s); 

					s.x = s.x * -1;

					SiteSphere sphere = new SiteSphere(s);
					siteToSphere.put(s, sphere);
					spheres.add(sphere);
					rootBG.addChild(sphere);
				}
			}
		}else if(i == 1){
			for(Site s: molecule.getSiteArray()){
				if(!s.getTypeName().equals(SiteType.ANCHOR)){
					spheres.remove(siteToSphere.get(s));
					rootBG.removeChild(siteToSphere.get(s));
					siteToSphere.remove(s);

					s.y = s.y * -1;

					SiteSphere sphere = new SiteSphere(s);
					siteToSphere.put(s, sphere);
					spheres.add(sphere);
					rootBG.addChild(sphere);
				}
			}
		}else if(i == 2){
			double adjZ = 0; // we will flip z about center off mass in z
			int count = 0;
			for(Site s: molecule.getSiteArray()){
				if(!s.getTypeName().equals(SiteType.ANCHOR)){
					adjZ += s.getZ();
					count++;
				}
			}
			
			adjZ = adjZ / count;
			
			for(Site s: molecule.getSiteArray()){
				spheres.remove(siteToSphere.get(s));
	    		rootBG.removeChild(siteToSphere.get(s));
	    		siteToSphere.remove(s);
	    		
	    		if(!s.getTypeName().equals(SiteType.ANCHOR)){
					s.z = (adjZ - s.z) + adjZ;
	    		}
	    		
				SiteSphere sphere = new SiteSphere(s);
	    		siteToSphere.put(s, sphere);
	    		spheres.add(sphere);
	    		rootBG.addChild(sphere);
			}
		}
		
		for(Link l: molecule.getLinkArray()){	        
	        LinkCylinder cylinder = new LinkCylinder(l);
	        linkToCylinder.put(l, cylinder);
	        cylinders.add(cylinder);
	        rootBG.addChild(cylinder);
		}
		
    	notifyListeners();
	}
	
	
}
