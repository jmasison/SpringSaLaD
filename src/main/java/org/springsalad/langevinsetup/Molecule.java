/**
 * Store information on each molecule type in the system. 
 *
 *  Also store information on molecule creation/decay rates. Originally I 
 *  defined a separate class called MoleculeReaction, but it just held a 
 *  molecule and two rates.  The code is much cleaner if I just put those rates 
 *  here.
 */

package org.springsalad.langevinsetup;

import org.springsalad.helpersetup.IOHelp;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Molecule {
    
    // Name of the molecule
    private String name;
    private String filename;    
    
    // Default location is inside
    private String location = SystemGeometry.INSIDE;

    // Arrays to store sites, links, and types. 
    private ArrayList<Site> siteArray = new ArrayList<>();
    private ArrayList<Link> linkArray = new ArrayList<>();
    private ArrayList<SiteType> typeArray = new ArrayList<>();
    
    // It helps to keep a second reference to membrane anchor sites
    private final ArrayList<Site> anchorSites = new ArrayList<>();
    
    // The initial condition stores the molecule number. Maybe other stuff in the future.
    private final InitialCondition initialCondition;
    
    // Each molecule has its own MoleculeCounter class
    private final MoleculeCounter moleculeCounter;
    
    // Every molecule stores information about its own creation/decay reaction
    private final DecayReaction decayReaction;
    
    // Molecules can be designated as 2D, which fixes their distance from the membrane
    private boolean is2d = false;
    
    // Molecules have annotations
    private final Annotation annotation;

    // Constructor accepts the name.  That's it. 
    public Molecule(String name){
        this.name = name;
        // Make the intial condition that belongs to this molecule
        initialCondition = new InitialCondition(this);
        // Make the decay reaction that belongs to this molecule
        decayReaction = new DecayReaction(this);
        // Make the MoleculeCounter that belongs to this molecule
        moleculeCounter = new MoleculeCounter(this);
        // Make the annotation
        annotation = new Annotation();
    }
    
    public void setFile(String s){
    	this.filename = s;
    }
    public String getFilename(){
    	return this.filename;
    }
    
    
    /* ****************  METHODS FOR HANDLING SITE TYPES  ****************/
    
    // Get total type number
    public int typeNumber(){
        return typeArray.size();
    }
    
    // Get full type array
    public ArrayList<SiteType> getTypeArray(){
        return typeArray;
    }
    
    // Get a type from its index
    public SiteType getType(int index){
        return typeArray.get(index);
    }
    
    // Get a type from its name
    public SiteType getType(String typeName){
        // <editor-fold defaultstate="collapsed" desc="Method Code">  
        SiteType tempType = null;
        for (SiteType type : typeArray) {
            if (type.getName().equals(typeName)) {
                tempType = type;
                break;
            }
        }
        if(tempType == null){
            System.out.println("Invalid typeName supplied to molecule.getType().");
        }
        return tempType;
        // </editor-fold>
    }
    
    // Set the entire type array
    public void setTypeArray(ArrayList<SiteType> types){
        this.typeArray = types;
    }
    
    // Add a single type
    public void addType(SiteType type){
        typeArray.add(type);
    }
    
    // Remove a type, given the type
    public void removeType(SiteType type){
        typeArray.remove(type);
    }
    
    // Remove a type, given its index in the array
    public void removeType(int index){
        typeArray.remove(index);
    }
    
    // Tells us if a type is currently being used by a site
    public boolean typeAssignedToSite(SiteType type){
        // <editor-fold defaultstate="collapsed" desc="Method Code">  
        boolean assigned = false;
        for(Site site: siteArray){
            if(site.getType() == type){
                assigned = true;
                break;
            }
        }
        return assigned;
        // </editor-fold>
    }
    
    public boolean hasAnchorType(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">  
        boolean hasAnchor = false;
        for (SiteType typeArray1 : typeArray) {
            if (typeArray1.getName().equals(SiteType.ANCHOR)) {
                hasAnchor = true;
                break;
            }
        }
        return hasAnchor;
        // </editor-fold>
    }
    
    /* ************** METHODS TO HANDLE SITES ***********************/
    
    // Get total site number
    public int siteNumber(){
        return siteArray.size();
    }
    
    // Get full site array
    public ArrayList<Site> getSiteArray(){
        return siteArray;
    }
    
    // Get a single site
    public Site getSite(int index){
        return siteArray.get(index);
    }
    
    // Set full site array
    public void setSiteArray(ArrayList<Site> sites){
        this.siteArray = sites;
    }
    
    // Add a site
    public void addSite(Site site){
        // <editor-fold defaultstate="collapsed" desc="Method Code">  
        this.siteArray.add(site);
        if(site.getTypeName().equals(SiteType.ANCHOR)){
            if(hasAnchorSites()){
                site.setZ(membranePosition());
            } else {
                site.setZ(DrawPanel.DEFAULT_MEMBRANE_LOCATION);
            }
            anchorSites.add(site);
        } else if(site.getLocation().equals(SystemGeometry.INSIDE) && site.getZ()==0){
            if(hasAnchorSites()){
                site.setZ(membranePosition() + site.getRadius() + 1);
            } else {
                site.setZ(4);
            }
        } else if(site.getLocation().equals(SystemGeometry.OUTSIDE) && site.getZ()==0){
            if(hasAnchorSites()){
                site.setZ(membranePosition() - 1- site.getRadius());
            } else {
                site.setZ(4);
            }
        }
        // </editor-fold>
    }
    
    // We we add a linear site array the positions and locations are already set
    public void addSiteArray(ArrayList<Site> sites){
        for(Site site : sites){
            addSite(site);
        }
    }
    
    // Remove a site
    public void removeSite(Site site){
        // <editor-fold defaultstate="collapsed" desc="Method Code">  
        siteArray.remove(site);
        if(site.getTypeName().equals(SiteType.ANCHOR)){
            anchorSites.remove(site);
        }
        // </editor-fold>
    }
    
    // Remove a site given its index
    public void removeSite(int index){
        // <editor-fold defaultstate="collapsed" desc="Method Code">  
        Site site = siteArray.get(index);
        siteArray.remove(site);
        if(site.getTypeName().equals(SiteType.ANCHOR)){
            anchorSites.remove(site);
        }
        // </editor-fold>
    }
    
    // Tells us if a site has a link
    public boolean siteHasALink(Site site){
        // <editor-fold defaultstate="collapsed" desc="Method Code">  
        boolean hasLink = false;
        for(Link link : linkArray){
            if(site == link.getSite1() || site == link.getSite2()){
                hasLink = true;
                break;
            }
        }
        return hasLink;
        // </editor-fold>
    }
    
    
    /* ****** METHODS RELATED TO MEMBRANE ANCHOR SITES ******************/
    
    // Return anchor sites
    public ArrayList<Site> getAnchorSites(){
        return anchorSites;  
    }
    
    // Tells us if we have any anchor sites
    public boolean hasAnchorSites(){
        return !anchorSites.isEmpty();
    }
    
    // For loading, helps to be able to set the anchor sites
    public void setAnchors(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">  
        for(Site site : siteArray){
            if(site.getTypeName().equals(SiteType.ANCHOR)){
                anchorSites.add(site);
            }
        }
        // </editor-fold>
    }
    
    // Get the anchor z position
    public Double membranePosition(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">  
        if(!anchorSites.isEmpty()){
            return anchorSites.get(0).getZ();
        } else {
            return null;
        }
        // </editor-fold>
    }
    
    
    /* ************** METHODS TO HANDLE LINKS **********************/
    
    // Get total link number
    public int linkNumber(){
        return linkArray.size();
    }
    
    // Get full link array
    public ArrayList<Link> getLinkArray(){
        return linkArray;
    }
    
    // Get link from index
    public Link getLink(int index){
        return linkArray.get(index);
    }
    
    // Set full link array
    public void setLinkArray(ArrayList<Link> links){
        this.linkArray = links;
    }
    
    // Add a single link
    public void addLink(Link link){
        linkArray.add(link);
    }
    
    // Remove a given link
    public void removeLink(Link link){
        linkArray.remove(link);
    }
    
    // Remove a link given its index
    public void removeLink(int index){
        linkArray.remove(index);
    }
    
    /* ******************************************************************\
     *            GET AND SET NAME AND LOCATION                         *
    \********************************************************************/
    
    public String getName(){
        return name;
    }
    
    @Override
    public String toString(){
        return name;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public String getLocation(){
        return location;
    }
    
    public void setLocation(String location){
        // <editor-fold defaultstate="collapsed" desc="Method Code">  
        if(location.equals(SystemGeometry.INSIDE) || location.equals(SystemGeometry.OUTSIDE) || location.equals(SystemGeometry.MEMBRANE)){
            this.location = location;
        } else {
            System.out.println("Tried to set molecule to an invalid location. Given string " + location + ".");
        }
        // </editor-fold>
    }
    
    /* ************* METHODS TO GET AND SET 2D FLAG ************************/
    
    public void set2D(boolean bool){
        is2d = bool;
    }
    
    public boolean is2D(){
        return is2d;
    }
    
    /* ************ METHODS RELATED TO THE INITIAL CONDITION ***************/
    public int getNumber(){
        return initialCondition.getNumber();
    }
    
    public void setNumber(int number){
        this.initialCondition.setNumber(number);
    }
    
    public InitialCondition getInitialCondition(){
        return initialCondition;
    }
    
    /* ** METHOD TO GET THE DECAY REACTION ASSOCIATED WITH THIS MOLECULE  **/
    
    public DecayReaction getDecayReaction(){
        return decayReaction;
    }
    
    /* ** METHOD TO GET THE MOLECULE COUNTER ASSOCIATED WITH THIS MOLECULE **/
    
    public MoleculeCounter getMoleculeCounter(){
        return moleculeCounter;
    }
    
    /* ** METHOD TO GET THE ANNOTATION ASSOCIATED WITH THIS MOLECULE *******/
    public Annotation getAnnotation(){
        return annotation;
    }
    
    /* *** CHECK TO SEE IF ALL OF THE SITES ARE CONNECTED TO EACH OTHER **/
    
    // This is the iterative method we use to check if all of the sites are 
    // connected together.  We mark the given site as connected, then go to
    // the other sites it is connected to and call the same method on those
    // sites. We call this on site 0 and see if every other site gets marked 
    // as checked. If not, then at least one site is not connected to the 
    // others.
    public void siteConnected(Site site){
        // <editor-fold defaultstate="collapsed" desc="Method Code">  
        site.setChecked(true);
        ArrayList<Site> connectedSites = site.getConnectedSites();
        for(Site aSite : connectedSites){
            if(!aSite.getChecked()){
                aSite.setChecked(true);
                siteConnected(aSite);
            }
        }
        // </editor-fold>
    }
    
    // This method checks all of the sites
    public boolean sitesConnected(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">  
        // First clear all of the checked flags
        for(Site site : siteArray){
            site.setChecked(false);
        }
        
        // Now check site connectivity, starting with site 0
        siteConnected(siteArray.get(0));
        
        boolean connected = true;
        for(Site site : siteArray){
            connected = connected && site.getChecked();
        }
        return connected;
        // </editor-fold>
    }
    
    /* *** CHECK TO SEE IF THIS MOLECULE IS 2D OR 3D *****************/
    
    public boolean is3D(){
        boolean is3d = false;
        for(Site site : siteArray){
            if(site.getX() > 1e-6 || site.getX() < -1*(1e-6)){
                is3d = true;
                break;
            }
        }
        return is3d;
    }
    
    /* **** TRANSLATE THE ENTIRE MOLECULE ***************************/
    
    public void translate(double dx, double dy, double dz){
        for(Site site : siteArray){
            site.translate(dx, dy, dz);
        }
    }
    
    public double getMaxX(){
    	double m1 = Integer.MIN_VALUE; //store max value
    	double m2 = Integer.MAX_VALUE; //store min value
    	for(Site site : siteArray){
            if(m1 < site.getType().getRadius() + site.getX())
            	m1 =  site.getType().getRadius() + site.getX();
            if(m2 > site.getX() - site.getType().getRadius())
            	m2 = site.getX() - site.getType().getRadius();
        }
    	m2 = m2 * -1;
    	
    	return m2 > m1 ? m2 : m1;
    }
    public double getMaxY(){
    	double m1 = Integer.MIN_VALUE; //store max value
    	double m2 = Integer.MAX_VALUE; //store min value
    	for(Site site : siteArray){
            if(m1 < site.getType().getRadius() + site.getY())
            	m1 =  site.getType().getRadius() + site.getY();
            if(m2 > site.getY() - site.getType().getRadius())
            	m2 =  site.getY() - site.getType().getRadius();
        }
    	m2 = m2 * -1;
    	
    	return m2 > m1 ? m2 : m1;
    }
    public double getMaxZ(){
    	double m1 = Integer.MIN_VALUE; //store max value
    	double m2 = Integer.MAX_VALUE; //store min value
    	for(Site site : siteArray){
            if(m1 < site.getType().getRadius() + site.getZ())
            	m1 =  site.getType().getRadius() + site.getZ();
            if(m2 > site.getZ() - site.getType().getRadius())
            	m2 = site.getZ() - site.getType().getRadius();
        }
    	m2 = m2 * -1;
    	
    	return m2 > m1 ? m2 : m1;
    }
    
    /*****************************************************************\
     *                    FILE IO METHODS                            *
     * @param p                                                      *
    \*****************************************************************/
    
    public void writeMolecule(PrintWriter p){
        // <editor-fold defaultstate="collapsed" desc="Method Code">   
        p.println("MOLECULE: \"" + this.getName() + "\" " + location + " Number " + this.getNumber() + " Site_Types " + this.typeNumber() + " Total"
                + "_Sites " + this.siteNumber() + " Total_Links " + this.linkNumber() + " is2D " + is2d);
        p.println("{");
        for (SiteType typeArray1 : typeArray) {
            p.print("     ");
            typeArray1.writeType(p);
        }
        p.println();
        for (Site siteArray1 : siteArray) {
            p.print("     ");
            siteArray1.writeSite(p);
        }
        p.println();
        for (Link linkArray1 : linkArray) {
            p.print("     ");
            linkArray1.writeLink(p);
        }
        p.println();
        p.print("     Initial_Positions: ");
        if(this.initialCondition.usingRandomInitialPositions()){
            p.println(InitialCondition.RANDOM);
        } else {
            p.println(InitialCondition.SET);
            p.println("     x: " + IOHelp.printArrayList(initialCondition.getXIC(), 5));
            p.println("     y: " + IOHelp.printArrayList(initialCondition.getYIC(), 5));
            p.println("     z: " + IOHelp.printArrayList(initialCondition.getZIC(), 5));
        }
        p.println("}");
        p.println();
        // </editor-fold> 
    }

    public static Molecule readMolecule(Scanner sc){
        // <editor-fold defaultstate="collapsed" desc="Method Code">   
        Molecule tempMol = new Molecule("TempName");
        // Read all of the lines into an arraylist
        ArrayList<String> line = new ArrayList<>();
        String checkLine = sc.nextLine();
        while(!checkLine.equals("}")){
            line.add(checkLine);
            checkLine = sc.nextLine();
        }
        sc.close();
//        for(int i=0;i<line.size();i++){
//            System.out.println("Line " + i + ": " + line.get(i));
//        }
        
        Scanner sc0 = new Scanner(line.get(0)); 
        // check input line
//        if(!sc0.next().equals("MOLECULE:")){
//            System.out.println("ERROR: Molecule scanner does not begin with \"MOLECULE:\"");
//        }
        
        tempMol.setName(IOHelp.getNameInQuotes(sc0));
        tempMol.setLocation(sc0.next());
        // Now get the total number of these molecules
        if(sc0.next().equals("Number")){
            tempMol.setNumber(sc0.nextInt());
        } else {
            System.out.println("Could not read the total number of molecules.");
        }
        // Now get the total types, sites, and links
        int totalTypes = -1;
        if(sc0.next().equals("Site_Types")){
            totalTypes = sc0.nextInt();
        } else {
            System.out.println("Could not read total types.");
        }
        int totalSites = -1;
        if(sc0.next().equals("Total_Sites")){
            totalSites = sc0.nextInt();
        } else {
            System.out.println("Could not read number of total sites.");
        }
        int totalLinks = -1;
        if(sc0.next().equals("Total_Links")){
            totalLinks = sc0.nextInt();
        } else {
            System.out.println("Could not read total number of links.");
        }
        if(sc0.next().equals("is2D")){
            tempMol.set2D(sc0.nextBoolean());
        } else {
            System.out.println("Could not read is2D field.");
        }
        // check input file one last time
        if(!line.get(1).equals("{")){
            System.out.println("ERROR: Molecule scanner did not find opening \"{\"");
        }
        sc0.close();
        // now read in the site types
        ArrayList<SiteType> types = new ArrayList<>();
        for(int i=0;i<totalTypes;i++){
            types.add(SiteType.readType(tempMol, line.get(i+2)));
        }
//        for(int i=0;i<types.size();i++){
//            System.out.println(types.get(i).getTypeName());
//        }
        
        // Now read in the sites
        ArrayList<Site> sites = new ArrayList<>();
        Site tempSite = null;
        int siteIndex;
        Scanner sc1;
        String tempName;
        String [] siteString = new String[3];
        for(int i=3+totalTypes;i<3+totalTypes+3*totalSites;i+=3){
            siteString[0] = line.get(i);
            siteString[1] = line.get(i+1);
            siteString[2] = line.get(i+2);
//            for(int j=0;j<3;j++){
//                System.out.println("Grabbing line " + i);
//                System.out.println("siteString [" + j + "] = " + siteString[j]);
//            }
            // Get the site index
            sc1 = new Scanner(siteString[0]);
            sc1.next();
            siteIndex = sc1.nextInt();
            sc1.next();
            String location = sc1.next();
            // Skip over the words Initial State
            sc1.next();sc1.next();sc1.next();
            String initialStateName = IOHelp.getNameInQuotes(sc1);
            sc1.close();
            // System.out.println("siteIndex = " + siteIndex);
            // Get the type name
            sc1 = new Scanner(siteString[1]);
            sc1.next();
            sc1.next();
            
            tempName = IOHelp.getNameInQuotes(sc1);
            for (SiteType type : types) {
                String tName = type.getName();
//                System.out.println("types[" + j + "] has name " + tName);
//                System.out.println("Temp name is " + tempName);
//                boolean match = tName.equals(tempName);
//                System.out.println("The name match? " + match);
                if (tempName.equals(tName)) {
                    tempSite = new Site(tempMol, type);
                    break;
                }
            }
            // Make sure the tempsite has been initialized at this point
            if(tempSite == null){
                System.out.println("ERROR: Did not initialize the site with a type!");
                break;
            }
            tempSite.setLocation(location);
            tempSite.setInitialState(tempSite.getType().getState(initialStateName));
            // Now get the x,y, and z coordinates of the site
            sc1.close();
            sc1 = new Scanner(siteString[2]);
            while(sc1.hasNext()){
                String var = sc1.next();
                switch(var){
                    case "x":{ tempSite.setX(sc1.nextDouble());
                    break;
                    }
                    case "y":{ tempSite.setY(sc1.nextDouble());
                    break;
                    }
                    case "z":{ tempSite.setZ(sc1.nextDouble());
                    break;
                    }
                    default:{ System.out.println("ERROR: Couldn't read (x,y,z) values.");
                    }
                }
            }
            sc1.close();
            tempSite.setIndex(siteIndex);
            // Now add the site to the site array
            sites.add(tempSite);
            
        }
//        for(int k = 0;k<sites.size();k++){
//            System.out.println("Index of site " + k + " is " + sites.get(k).getIndex());
//        }
        
        // Finally, we add the links
        ArrayList<Link> links = new ArrayList<>();
        Link tempLink;
        Site site1;
        Site site2;
        Scanner sc2;
        int i1 = -1;
        int i2 = -1;
        for(int i=4+totalTypes+3*totalSites;i<4+totalTypes + 3*totalSites + totalLinks; i++){
            site1 = null;
            site2 = null;
            sc2 = new Scanner(line.get(i));
            sc2.next();
            sc2.next();
            i1 = sc2.nextInt();
            sc2.next();
            sc2.next();
            i2 = sc2.nextInt();
            for (Site site : sites) {
                // System.out.println("Site " + j + " index is " + sites.get(j).getIndex() );
                if (i1 == site.getIndex()) {
                    site1 = site;
                }
                if (i2 == site.getIndex()) {
                    site2 = site;
                }
                if(site1 != null && site2 != null){
                    break;
                }
            }
            tempLink = new Link(site1,site2);
            links.add(tempLink);
            sc2.close();
        }
        
        // Now we add  all of this information to the molecule!
        tempMol.setLinkArray(links);
        tempMol.setSiteArray(sites);
        tempMol.setTypeArray(types);
        
        int initPosLine = 5+totalTypes + 3*totalSites + totalLinks;
        // For backwards compatibility, we check if we actually have the initial
        // position flag.
        if(line.size() > initPosLine){
            Scanner ipScanner = new Scanner(line.get(initPosLine));
            ipScanner.next();
            if(ipScanner.next().equals(InitialCondition.RANDOM)){
                tempMol.getInitialCondition().setUsingRandomInitialPositions(true);
            } else {
                InitialCondition ic = tempMol.getInitialCondition();
                ic.setUsingRandomInitialPositions(false);

                Scanner xScanner = new Scanner(line.get(initPosLine+1));
                xScanner.useDelimiter(":");
                xScanner.next();
                String [] xs = xScanner.next().trim().split(" ");
                xScanner.close();

                Scanner yScanner = new Scanner(line.get(initPosLine+2));
                yScanner.useDelimiter(":");
                yScanner.next();
                String [] ys = yScanner.next().trim().split(" ");
                yScanner.close();

                Scanner zScanner = new Scanner(line.get(initPosLine+3));
                zScanner.useDelimiter(":");
                zScanner.next();
                String [] zs = zScanner.next().trim().split(" ");
                zScanner.close();

                ic.setAllInitialPositions(xs, ys, zs);
            }
            ipScanner.close();
        } else {
            tempMol.getInitialCondition().setUsingRandomInitialPositions(true);
        }
        
        return tempMol;
        // </editor-fold> 
    }
    
    public static ArrayList<Molecule> loadMolecules(String moleculeData){
        // <editor-fold defaultstate="collapsed" desc="Method Code">  
        // BREAK UP THE STRING AT THE WORD 'MOLECULE'
        Scanner sc = new Scanner(moleculeData);
        sc.useDelimiter("MOLECULE:");
        ArrayList<String> moleculeStrings = new ArrayList<>();
        while(sc.hasNext()){
            moleculeStrings.add(sc.next());
        }
        sc.close();
        ArrayList<Molecule> molecules = new ArrayList<>();
        for(String data : moleculeStrings){
            Molecule mol = Molecule.readMolecule(new Scanner(data));
            mol.setAnchors();
            molecules.add(mol);
        }
        return molecules;
        // </editor-fold>
    }

	public static void loadMoleculesFiles(Global g, String lines, ArrayList<Molecule> g_molecules) {
		Scanner sc = new Scanner(lines);
        sc.useDelimiter("MOLECULE:");
        ArrayList<String> moleculeStrings = new ArrayList<>();
        while(sc.hasNext()){
            moleculeStrings.add(sc.next());
        }
        sc.close();
        
        for(String data : moleculeStrings){
        	Scanner sc2 = new Scanner(data);
            sc2.useDelimiter(" ");
            String molName = sc2.next();
            String finalName = sc2.next();
            String fName = "";
            while(sc2.hasNext()) {
            	fName = sc2.next();
            	finalName = finalName + " " + fName;
            }   
            finalName = finalName.trim();
            
            for(Molecule m: g_molecules){
            	if(m.getName().equals(molName)){
            		//check if file exists
            		if(new File(finalName).exists()) {
            			m.setFile(finalName);
            		}else {
            			//try and find file in auto-generated folder
            			String childname = new File(finalName).getName();
            			if(new File(g.getFile().getParent() + File.separator + "structure_files" + File.separator + childname).exists()) {
            				m.setFile(g.getFile().getParent() + File.separator + "structure_files" + File.separator + childname);
            			}else {
            				String msg = "Could not find pdb source file for "+ m.toString() +".\nPlease check model txt file for correct Molecule file.";
//            				PopUp.information(msg);
            				System.out.println(msg);
            			}
            		}
            		break;
            	}
            }
            
            sc2.close();
        }
	}
    
}
