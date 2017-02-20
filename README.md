Leo-III
=======
*A massively parallel higher-order theorem prover*

In the Leo-III project, we
design and implement a state-of-the-art Higher-Order
Logic Theorem Prover, the successor of the well known
LEO-II prover [[2](http://dx.doi.org/10.1007/978-3-540-71070-7_14)]. Leo-III will be based on ordered
paramodulation/superposition.
In contrast to LEO-II, we replace the internal term representation
(the commonly used simply typed lambda-calculus)
by a more expressive system supporting type polymorphism.
In order to achieve a substantial performance speed-up,
the architecture of Leo-III will be based on massive parallelism
(e.g. And/Or-Parallelism, Multisearch) [[3](http://dx.doi.org/10.1023/A:1018932114059)]. The
current design is a multi-agent blackboard architecture
that will allow to independently run agents with our proof
calculus as well as agents for external (specialized) provers.
Leo-III will focus right from the start on compatibility to
the widely used TPTP infrastructure [[8](http://dx.doi.org/10.1007/s10817-009-9143-8)]. Moreover, it
will offer built-in support for specialized external prover
agents and provide external interfaces to interactive provers
such as Isabelle/HOL [[5](http://dx.doi.org/10.1007/3-540-45949-9)]. The implementation will excessively
use term sharing [[6](http://dl.acm.org/citation.cfm?id=1218621), [7](http://dl.acm.org/citation.cfm?id=1218620)] and several indexing techniques
[[4](dx.doi.org/10.1007/3-540-45744-5_19), [9](dx.doi.org/10.1007/978-3-540-71070-7_14)]. Leo-III will also offer special support for
reasoning in various quantified non-classical logics by exploiting
a semantic embedding [[1](dx.doi.org/10.5220/0004324803460351)] approach.

Further information can be found at the [Leo-III Website](http://page.mi.fu-berlin.de/lex/leo3/).


Required Dependencies
----------------

Leo III needs Java >= 1.8 to run.
Scala 2.11.8 is required to build and run the project.
The build tool (sbt, maven) will automatically download Scala and further dependencies.
Alternative, Scala can be downloaded at [Scala-lang.org](http://scala-lang.org/download/).

Building the project (SBT)
----------------

[SBT](http://www.scala-sbt.org/) is now the preferred
build system. To compile and run Leo-III at least SBT 0.13.6
and Java 8 is required. Furthermore, `cmake` and a recent `gcc`
are used to compile PicoSAT and should therefor be present too.

First download PicoSAT version 965 from the
[PicoSAT homepage](http://fmv.jku.at/picosat/) and
extract the archive into `./src/native/` creating a folder
`./src/native/picosat-965`. The following commands do this autmatically:

    > wget http://fmv.jku.at/picosat/picosat-965.tar.gz
    > tar -xf picosat-965.tar.gz -C src/native
    
To build Leo-III run:

    > sbt compile
    > sbt nativeCompile
    
The compiled output will be placed at `./target/classes`.
Alternatively, the build Leo-III for debugging purposes, run

    > sbt debugCompile
    
This will include output for fine-grained debug logging messages which granularity
can be controlled by the `-v` parameter (see Usage below).

Leo-III can then be started by executing

    > sbt run

use quotation marks to pass arguments to LEO-III

    > sbt "run test.thf --seq"
    
Note that `sbt run` is only supported for the non-debug build of Leo-III. To run
the debug version, simply start Leo-III using a stand-alone debug jar (as shown below).

To run the unit tests call

    > sbt test

It is possible to generate a standalone `.jar` file which
contains all dependencies required by Leo-III (analogously
with `sbt debugAssembly`).

    > sbt assembly
    > java -jar target/scala-2.11/Leo\ III-assembly-0.1.jar

Occasionally it might happen that multiple versions of Java are
installed. To enforce to correct version of Jave, overwrite
`JAVA_HOME` and `PATH`:

    > export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/
    > export PATH=$JAVA_HOME/bin:$PATH
    > sbt compile
    > sbt nativeCompile

Many Linux distributions offer a native method to select the
Java version. This often works better then selecting the Java
home via the command line switch. On Ubuntu this is called
`update-alternatives`.

### The PicoSAT bindings

To test the bindings run:

    > sbt "testOnly leo.modules.sat_solver.PicoSATTestSuite"


Usage
----------------
```
Leo III -- A Higher-Order Theorem Prover.
Christoph Benzmüller, Alexander Steen, Max Wisniewski and others.

Usage: ... PROBLEM_FILE [OPTIONS]
Options:
-e name=N, --atp-timout name=N		Timeout for an external prover in seconds.
-s, --sos 		Use SOS heuristic search strategy
-n N		Maximum number of threads
-t N		Timeout in seconds
-a name=call, --atp name=call		Addition of external provers
-v Lvl		Set verbosity: From 0 (No Logging output) to 6 (very fine-grained debug output)
-p		Display proof output
-c Csat		Sets the proof mode to counter satisfiable (Through remote proof
-h		Display this help message
```
Note that verbosity levels greater than 3 are only supported in debug builds.


Project's current structure
--------------

This section is a stub. It will be expanded in the future.

```
└──leo                     -- Where the Main executable is located, root package
    ├── agents             -- Specification of agents
    │   └── impl           -- Implementation of agents
    ├── datastructures     -- root package for all base data structures
    │   ├── blackboard
    │   ├── context
    │   ├── impl           -- Most of the implementations are located here
    │   └── tptp           -- Internal syntax representation of TPTP formulae
    └── modules            -- All sorts of functionality modules
        ├── agent
        ├── calculus       -- Implementation of calculus rules
        ├── control        -- proof procedure's control structures
        ├── external       -- utility for remote calls and external agents
        ├── indexing
        ├── output         -- Output and logging functionality, pretty printing
        └── parsers        -- Input parsing
```
