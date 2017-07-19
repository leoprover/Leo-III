Leo-III 1.1
========
*An automated theorem prover for classical higher-order logic (with choice)*

Leo-III is an automated theorem prover for (polymorphic) higher-order logic which supports all common TPTP dialects, including THF, TFF and FOF as well as their rank-1 polymorphic derivatives  [BP13,KRS16]. 
It is based on a paramodulation calculus with ordering constraints and, in tradition of its predecessor LEO-II, heavily relies on cooperation with external (mostly first-order) theorem provers for increased performance. Nevertheless, Leo-III can also be used as a stand-alone prover without employing any external cooperation.

Leo-III is developed at Freie Universität Berlin as part of the German National Research Foundation (DFG) project BE 2501/11-1. The main contributors are (sorted alphabetically): Christoph Benzmüller, Tomer Libal, Alexander Steen and Max Wisniewski. For a full list of contributors to the project and used and third-party libraries, please refer to the `AUTHORS` file in the source distribution.

## Requirements

Leo-III requires the Java 1.8 Runtime (JRE) for execution. Leo-III works on any common OS (including Windows*, Mac OS, linux derivatives).

*) *External cooperation so-far only works on Linux and Mac systems. If you use Windows, you might want to try running Leo-III using Cygwin or similar.*

## Installation


#### Using pre-built binaries (Linux)

A current release of Leo-III 1.1 can be downloaded from GitHub:

> https://github.com/cbenzmueller/Leo-III/releases/download/v1.1/Leo.III-assembly-1.1.jar

Note that this binary was built on a Debian-based system and might not work for any Linux derivatives.

#### Requirements for building from source

The following requirements (dependencies) are not managed by the SBT build tool and hence need to be present at the system:

 - Java JDK 1.8 
 - gcc (any reasonably current version)
 - [SBT](http://www.scala-sbt.org/) (Scala Build Tool) >= 0.13.6

#### Building the project from source

Leo-III uses [SBT](http://www.scala-sbt.org/) for building the Scala sources. SBT will download an appropriate version of Scala (and further dependencies) automatically. The actual build process in wrapped inside a `Makefile`. 

1) Download the source distribution and unpack the archive
    ```Shell
    > wget https://github.com/cbenzmueller/Leo-III/archive/v1.1.tar.gz
    > tar -xzf Leo-III-1.1.tar.gz
    ```
2) Step into the newly created directory and run `make`
   ```
   > cd Leo-III-1.1/
   > make
   ```
   The building process might take some time, depending on your computer.
3) If no error occurred, you should find a `bin` directory at top-level:
   ```
   > cd bin/
   > ls
   leo leo3.jar
   ```
   where `leo3.jar` is the executable jar of Leo-III. The `leo3` file is just a bash
   script short-cut calling `java -jar leo3.jar`. Note that `leo3` assumes that the      jar files resides in the same directory as the script itself.
   
   Another possibility is to move the jar file somewhere (say `path/to/leo3.jar`)      and to define an alias
   ```alias leo3='java -jar /path/to/leo3.jar'```.


#### Using nix

We support using [Nix](https://nixos.org) for creating a reliable and reproducible execution environment for Leo-III. The source distribution contains a `.nix` file that can be used to run Leo-III within a `nix` shell.

## Usage

#### Basics

Leo-III is invoked via command-line (*assuming the leo3 bash script is in your current directory or you defined a leo3 alias as described above*):
```
> ./leo3
Leo III -- A Higher-Order Theorem Prover.
Christoph Benzmüller, Alexander Steen, Max Wisniewski and others.

Usage: ... PROBLEM_FILE [OPTIONS]
Options:
[...]
```

A call to a problem `someproblem.p`, assumed to be located at the current directory, is invoked as follows:

```
> ./leo someproblem.p
% SZS status Theorem for someproblem.p : 3651 ms resp. 1253 ms w/o parsing
```
Of course, depending on the problem, Leo-III could also output `Timeout`, `Unknown`, `SyntaxError` and others, according to the SZS status ontology [...]

##### Important parameters

Parameters are passed *after* the problem file and without an equality sign "=", if used with parameter value: `./leo3 problem -param1 -param2 10`

The most important parameters are

| Parameter flag  | Effect |
| ------------- | ------------- |
| -p  | Output a refutation proof, if one was found  |
| -t `s`  | Set the timeout to `s` seconds, meaning that Leo-III will try to proof the problem and return `SZS_Timeout` if not successful after approximately `s`  seconds|
| --atp `system` | Use `system` for external cooperation. See further below. |

There are many more parameters, we will add them here some time in the future.

#### Proving TPTP problems

A popular and de-facto standard library for proof problems is the TPTP library. Some problems of this library use `include` statements. In order to resolve them correctly, you might need to set the `TPTP` environment variable correctly, e.g.
```
export TPTP=/path/to/TPTP
```
If this environment variable is set, Leo-III will automatically resolve TPTP axioms.

#### Examples 1

Let's solve the TPTP problem `SET014^4.p` (see [here](http://www.cs.miami.edu/~tptp/cgi-bin/SeeTPTP?Category=Problems&Domain=SET&File=SET014^4.p)) with a timeout of 60 seconds and proof output:

```
> ./leo3 SET014^4.p -t 60 -p
```
The output looks like this:
```
% SZS status Theorem for /opt/TPTP/Problems/SET/SET014^4.p : 3651 ms resp. 1253 ms w/o parsing
% SZS output start CNFRefutation for /opt/TPTP/Problems/SET/SET014^4.p
thf(union_type, type, union: (($i > $o) > (($i > $o) > ($i > $o)))).
thf(union_def, definition, (union = (^ [A:($i > $o),B:($i > $o),C:$i]: ((A @ C) | (B @ C))))).
thf(subset_type, type, subset: (($i > $o) > (($i > $o) > $o))).
thf(subset_def, definition, (subset = (^ [A:($i > $o),B:($i > $o)]: ! [C:$i]: ((A @ C) => (B @ C))))).
thf(sk1_type, type, sk1: ($i > $o)).
thf(sk2_type, type, sk2: ($i > $o)).
thf(sk3_type, type, sk3: ($i > $o)).
thf(sk4_type, type, sk4: $i).
thf(1,conjecture,((! [A:($i > $o),B:($i > $o),C:($i > $o)]: (((subset @ A @ C) & (subset @ B @ C)) => (subset @ (union @ A @ B) @ C)))),file('/opt/TPTP/Problems/SET/SET014^4.p',thm)).
thf(2,negated_conjecture,((~ (! [A:($i > $o),B:($i > $o),C:($i > $o)]: (((subset @ A @ C) & (subset @ B @ C)) => (subset @ (union @ A @ B) @ C))))),inference(neg_conjecture,[status(cth)],[1])).
thf(3,plain,((~ (! [A:($i > $o),B:($i > $o),C:($i > $o)]: ((! [D:$i]: ((A @ D) => (C @ D)) & ! [D:$i]: ((B @ D) => (C @ D))) => (! [D:$i]: (((A @ D) | (B @ D)) => (C @ D))))))),inference(defexp_and_simp_and_etaexpand,[status(thm)],[2])).
thf(5,plain,((sk1 @ sk4) | (sk2 @ sk4)),inference(cnf,[status(esa)],[3])).
thf(6,plain,(! [A:$i] : ((~ (sk2 @ A)) | (sk3 @ A))),inference(cnf,[status(esa)],[3])).
thf(8,plain,(! [A:$i] : ((~ (sk2 @ A)) | (sk3 @ A))),inference(simp,[status(thm)],[6])).
thf(4,plain,((~ (sk3 @ sk4))),inference(cnf,[status(esa)],[3])).
thf(11,plain,(! [A:$i] : ((~ (sk2 @ A)) | ((sk3 @ sk4) != (sk3 @ A)))),inference(paramod_ordered,[status(thm)],[8,4])).
thf(12,plain,((~ (sk2 @ sk4))),inference(pattern_uni,[status(thm)],[11:[bind(A, $thf(sk4))]])).
thf(13,plain,((sk1 @ sk4) | ((sk2 @ sk4) != (sk2 @ sk4))),inference(paramod_ordered,[status(thm)],[5,12])).
thf(14,plain,((sk1 @ sk4)),inference(pattern_uni,[status(thm)],[13:[]])).
thf(7,plain,(! [A:$i] : ((~ (sk1 @ A)) | (sk3 @ A))),inference(cnf,[status(esa)],[3])).
thf(9,plain,(! [A:$i] : ((~ (sk1 @ A)) | ((sk3 @ sk4) != (sk3 @ A)))),inference(paramod_ordered,[status(thm)],[7,4])).
thf(10,plain,((~ (sk1 @ sk4))),inference(pattern_uni,[status(thm)],[9:[bind(A, $thf(sk4))]])).
thf(15,plain,(((sk1 @ sk4) != (sk1 @ sk4))),inference(paramod_ordered,[status(thm)],[14,10])).
thf(16,plain,($false),inference(pattern_uni,[status(thm)],[15:[]])).
% SZS output end CNFRefutation for /opt/TPTP/Problems/SET/SET014^4.p
```
The first line (`% SZS status Theorem ...`) indicates that Leo-III was able to solve the problem.
The lines between `% SZS output begin CNFRefutation` and `% SZS output end CNFRefutation` is the generated proof.

You can always try to verify the proof using [IDV](http://www.cs.miami.edu/~tptp/Seminars/IDV/Summary.html) which should succeed at least for simple problems.

#### Enabling external cooperation
asd

#### Examples 2

asd

## Further information
Leo-III is licenced under the BSD 3-clause "New" or "Revised" License, see `LICENCE` in the source distribution.

Further information including related projects, current publications etc, can be found on the [Leo-III web site](www.inf.fu-berlin.de/~lex/leo3).

## Contributing to the project

We are always greateful to hear feedback from our users:

- If you are using Leo-III for any project yourself, we would be happy to hear about it! 
- If you encounter problems using Leo-III, feel tree to open a bug report (or simply a question) on the GitHub page.
- If you are interested to contribute to the project, simply fork the GitHub repository and open pull requests!

## References
tba
