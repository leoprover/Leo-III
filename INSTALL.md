## Requirements

Leo-III requires the Java 1.8 Runtime (JRE) for execution. Leo-III works on any common OS (including Windows*, Mac OS, linux derivatives).

*) *External cooperation so-far only works on Linux and Mac systems. If you use Windows, you might want to try running Leo-III using Cygwin or similar.*

## Installation

#### Using pre-built binaries (Linux)

A current release of Leo-III 1.2 can be downloaded from GitHub:

> wget https://github.com/leoprover/Leo-III/releases/download/v1.2/leo3.jar

Then call

> java -jar leo3.jar

Note that this binary was built on a Debian-based system and might not work for all Linux derivatives.
If the pre-build does not work for you, consider building Leo-III from source. Its simple and 
only takes a minute or two (see below).

#### Requirements for building from source

The following requirements (dependencies) are not managed by the SBT build tool and hence need to be present at the system:

 - Java JDK 1.8 
 - gcc (any reasonably current version)
 - [SBT](http://www.scala-sbt.org/) (Scala Build Tool) >= 1.x

#### Building the project from source

Leo-III uses [SBT](http://www.scala-sbt.org/) for building the Scala sources. SBT will download an appropriate version of Scala (and further dependencies) automatically. The actual build process in wrapped inside a `Makefile`. 
Proceed as follows to build Leo-III from source:

1) Download the source distribution and unpack the archive
    ```Shell
    > wget https://github.com/leoprover/Leo-III/archive/v1.2.tar.gz
    > tar -xzf Leo-III-1.2.tar.gz
    ```
2) Step into the newly created directory and run `make`
   ```Shell
   > cd Leo-III-1.2/
   > make
   ```
   The building process might take some time, depending on your computer.
3) If no error occurred, you should find a `bin` directory at top-level:
   ```Shell
   > cd bin/
   > ls
   leo3 leo3.jar
   ```
   where `leo3.jar` is the executable jar of Leo-III. The `leo3` file is just a bash
   script short-cut calling `java -jar leo3.jar` with further technical parameters.
   Note that `leo3` assumes that the jar file resides in the same directory as the script itself.
4) (*Optional*) Install (i.e. copy) the Leo-III binaries to a dedicated location using
   ```Shell
   > make install
   ```
   The default install destination is `$HOME/bin`. This will install the jar as well as the
   leo3 executable there. The install destination can be modified using the `DESTDIR` modifier.
   
   Another possibility is to move the jar file somewhere (say `path/to/leo3.jar`)      and to define an alias
   ```alias leo3='java -jar /path/to/leo3.jar'```.


#### Using nix

We support using [Nix](https://nixos.org) for creating a reliable and reproducible execution environment for Leo-III. The source distribution contains a `.nix` file that can be used to run Leo-III within a `nix` shell (see `contrib/default.nix`).
