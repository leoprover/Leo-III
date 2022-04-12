## Requirements

Leo-III requires the Java 1.8 Runtime (JRE) for execution. Leo-III works on any common operating system, including Windows, Mac OS and linux derivatives. Leo-III has not been tested with other operating systems, but it might of course still work.

> :warning: __Note:__ *External cooperation (that is, usage of external (first-order) reasoning systems for increasing the reasoning effectivity of Leo-III) is so-far only tested on Linux and Mac systems. If you use Windows, you might want to try running Leo-III using Cygwin or similar. Also see the note on bulding Leo-III from source on Windows systems below.*

## Installation

### Using pre-built binaries (Linux)

A current release of Leo-III (v1.6) can be downloaded from GitHub:

> wget https://github.com/leoprover/Leo-III/releases/download/v1.6/leo3-v1.6.jar

If the pre-built version does not work for you, consider building Leo-III from source. Its simple and 
only takes a minute or two (see below).

### Building from source
#### Requirements for building from source

The following requirements (dependencies) are not managed by the SBT build tool and hence need to be present at the system:

 - Java JDK >= 1.8
 - make
 - [SBT](http://www.scala-sbt.org/) (Scala Build Tool) >= 1.x

#### How to build Leo-III from source

Leo-III uses [SBT](http://www.scala-sbt.org/) for building the Scala sources. SBT will download an appropriate
version of Scala (and further dependencies) automatically. From the user's perspective, the actual build process
in managed using `make`.

Proceed as follows to build Leo-III from source:

1) __Getting the source__
    
    Download the source distribution of the latest stable version (here: 1.6) from GitHub and unpack the archive, e.g.
    ```Shell
    > wget https://github.com/leoprover/Leo-III/archive/refs/tags/v1.6.tar.gz
    > tar -xzf Leo-III-1.6.tar.gz
    ```
    
    You'll find the latest stable version of Leo-III under https://github.com/leoprover/Leo-III/releases/latest.
    Alternatively, you can use the most current development version of Leo-III (if you dare)
    using https://github.com/leoprover/Leo-III/archive/master.tar.gz.
    
2) __Build Leo-III sources__

   Step into the newly created directory and run `make` (that's all!)
   ```Shell
   > cd Leo-III-1.6/
   > make
   ```
   The building process might take some time, depending on your computer.
   
3) __Checking if everything is fine__

   If no error occurred, you should find a `bin` directory at top-level:
   ```Shell
   > cd bin/
   > ls
   leo3 leo3.jar
   ```
   where `leo3` is the executable of Leo-III. A jar file `leo3.jar` is also produced
   in case you want to include Leo-III as a library to some other application. 
   
   You can now run Leo-III:
   ```Shell
   > ./leo3 -h
   Leo III -- A Higher-Order Theorem Prover.
   Christoph Benzmüller, Alexander Steen, Max Wisniewski and others.
   
   Usage: leo3 problem [option ...]
   [...]
   ```
   
   See [USAGE.md](USAGE.md) for details on how to use Leo-III, including examples, parameter settings, etc.
   
4) __*Optionally* install Leo-III__ 

   Install (i.e. copy) the Leo-III binaries to a dedicated location using
   ```Shell
   > make install
   ```
   The default install destination is `$HOME/bin`. This will copy the `leo3` executable there.
   The install destination can be modified using the `DESTDIR` modifier.

### Native Image

In order to create a native image, please install [GraalVM](https://www.graalvm.org/docs/getting-started/) and
follow the instructions found there.

Also make sure to install `zlib` and `libc` in the static linked equivalents for your operating system of choice.

After that feel free to execute

      make native

In the root directory which will produce a native binary called leo3-bin in the bin folder.


### Using nix

We support using [Nix](https://nixos.org) for creating a reliable and reproducible execution environment for Leo-III. The source distribution contains a `.nix` file that can be used to run Leo-III within a `nix` shell (see `contrib/default.nix`).
