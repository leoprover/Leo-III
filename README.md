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
Scala 2.11.6 is required to build and run the project.
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
`./src/native/picosat-965`.

To build Leo-III run:

    > sbt compile
    > sbt nativeCompile

Leo-III can then be started by executing

    > sbt run

use quotation marks to pass arguments to LEO-III

    > sbt "run test.thf --seq"

To run the unit tests call

    > sbt test

It is possible to generate a standalone `.jar` file which
contains all dependencies required by Leo-III.

    > sbt assembly
    > java -jar target/scala-2.11/Leo\ III-assembly-0.1.jar

Occasionally it might happen that multiple versions of Java are
installed. The command line argument `-java-home` can be used
to select a specific one. For example:

    > sbt -java-home /usr/lib/jvm/java-8-openjdk-amd64/ compile

Many Linux distributions offer a native method to select the
Java version. This often works better then selecting the Java
home via the command line switch. On Ubuntu this is called
`update-alternatives`.

### The PicoSAT bindings

To test the bindings run:

    > sbt "testOnly leo.modules.sat_solver.PicoSATTestSuite"


Building the project (Maven)
----------------

:boom: Building with Maven is currently broken. :boom:

Alternatively [Maven](http://maven.apache.org/) can be
used to build Leo-III. This option is now deprecated and will be
removed in the future. Information about downloading and
installing Maven can be found at [the download section of
the maven website](http://maven.apache.org/download.cgi).

The project is compiled and built into an executable `.jar` file using

    > mvn compile assembly::single

Or, alternatively, the makefile can be used. Invoking

    > make

will result in the same `.jar`

All test suits are ran by

    > mvn test

The compiled test class files will be placed at `./target/test-classes/`.

The sole compilation process can be started by typing

    > mvn compile

The compiled files (class files) will be placed at `./target/classes/`.


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
    │   ├── term
    │   └── tptp           -- Internal syntax representation of TPTP
    └── modules            -- All sorts of functionality modules
        ├── churchNumerals -- old package, most likely to be removed soon
        ├── normalization
        ├── output         -- Output and logging functionality
        ├── parsers        -- Input parsing
        ├── proofCalculi
        └── visualization
```
