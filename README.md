## SpringSaLaD

### Description
This is a Java implementation of the SpringSaLaD algorithm, which is a spatially resolved, stochastic simulation algorithm 
for biochemical systems.  The algorithm is based on the Langevin equation, and is implemented in a particle-based framework.  

The underlying solver is found at https://github.com/cam-center/LangevinNoVis01.  

### For more information or to download SpringSaLaD
Website:  https://vcell.org/ssalad

The algorithm is described in ["SpringSaLaD: A Spatial, Particle-Based Biochemical Simulation Platform 
with Excluded Volume"](https://pubmed.ncbi.nlm.nih.gov/26840718/)  by Paul J Michalski and Leslie M Loew.

### Download Latest Version - 2.3.4
* [SpringSaLaD 2.3.4 for macOS (Intel and Apple Silicon)](https://github.com/cam-center/SpringSaLaD/releases/download/2.3.4/SpringSaLaD_macos_2_3_2.dmg)
* [SpringSaLaD 2.3.4 for Linux](https://github.com/cam-center/SpringSaLaD/releases/download/2.3.2/SpringSaLaD_unix_2_3_4.tar.gz)
* [SpringSaLaD 2.3.4 for Windows](https://github.com/cam-center/SpringSaLaD/releases/download/2.3.2/SpringSaLaD_windows-x64_2_3_4.zip)

### Building SpringSaLaD

Requirements
* Java 17
* Maven

```bash
mvn clean install dependency:copy-dependencies
```