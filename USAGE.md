## Usage
#### Basics

Leo-III is invoked via command-line (*assuming the leo3 executable is in your path, your current directory or you defined a leo3 alias as described above*):
```Shell
> ./leo3
Leo III -- A Higher-Order Theorem Prover.
Christoph Benzmüller, Alexander Steen, Max Wisniewski and others.

Usage: ... PROBLEM_FILE [OPTIONS]
Options:
[...]
```

A call to a problem `someproblem.p`, assumed to be located at the current directory, is invoked as follows:

```Shell
> ./leo someproblem.p
% SZS status Theorem for someproblem.p : 3651 ms resp. 1253 ms w/o parsing
```
Of course, depending on the problem, Leo-III could also output `Timeout`, `Unknown`, `SyntaxError` and others, according to the [SZS status ontology](http://www.cs.miami.edu/~tptp/cgi-bin/SeeTPTP?Category=Documents&File=SZSOntology) [Sut08].

##### Important parameters

Parameters are passed *after* the problem file path without an equality sign "=", if used with parameter value: `./leo3 problem -param1 -param2 10`

The most important parameters are

| Parameter flag  | Effect |
| ------------- | ------------- |
| -p  | Output a refutation proof, if one was found <br><br> Default: false (not set) |
| -t `s`  | Set the timeout to `s` seconds, meaning that Leo-III will try to proof the problem and return `SZS_Timeout` if not successful after approximately `s`  seconds <br><br> Default value: 60<br>Valid values: non-negative numbers|
| --atp `system` | Use `system` for external cooperation. See further below. <br><br> Default: none set <br> Valid values: See "External cooperation" below.|
| --primsubst `level` | Use the "itensity" `level` for instantiating flexible heads according to the primitive substitution rule <br><br> Default: 1<br>Valid values: 1-6 |
| --unifiers `n` | During unification, use at most `n` distinct unifiers<br> <br> Default: 1<br> Valud values: non-negative numbers |
| --unidepth `n` | During unification, use `n` as maximal unification search depth <br><br>Default: 8<br>Valid values: Non-negative numbers

There are many more parameters, we will add them here some time in the future.

#### Proving TPTP problems

A popular and de-facto standard library for proof problems is the TPTP library. Some problems of this library use `include` statements. In order to resolve them correctly, you might need to set the `TPTP` environment variable correctly, e.g.
```
export TPTP=/path/to/TPTP
```
If this environment variable is set, Leo-III will automatically resolve TPTP axioms.

#### Example

Let's solve the TPTP problem `SET014^4.p` (see [here](http://www.cs.miami.edu/~tptp/cgi-bin/SeeTPTP?Category=Problems&Domain=SET&File=SET014^4.p)) with a timeout of 60 seconds and proof output:

```
> ./leo3 SET014^4.p -t 60 -p
```
The output looks like this:
```
% SZS status Theorem for SET014^4.p : 3651 ms resp. 1253 ms w/o parsing
% SZS output start CNFRefutation for SET014^4.p
thf(union_type, type, union: (($i > $o) > (($i > $o) > ($i > $o)))).
thf(union_def, definition, (union = (^ [A:($i > $o),B:($i > $o),C:$i]: ((A @ C) | (B @ C))))).
thf(subset_type, type, subset: (($i > $o) > (($i > $o) > $o))).
thf(subset_def, definition, (subset = (^ [A:($i > $o),B:($i > $o)]: ! [C:$i]: ((A @ C) => (B @ C))))).
thf(sk1_type, type, sk1: ($i > $o)).
thf(sk2_type, type, sk2: ($i > $o)).
thf(sk3_type, type, sk3: ($i > $o)).
thf(sk4_type, type, sk4: $i).
thf(1,conjecture,((! [A:($i > $o),B:($i > $o),C:($i > $o)]: (((subset @ A @ C) & (subset @ B @ C)) => (subset @ (union @ A @ B) @ C)))),file('SET014^4.p',thm)).
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
% SZS output end CNFRefutation for SET014^4.p
```
The first line (`% SZS status Theorem ...`) indicates that Leo-III was able to solve the problem.
The lines between `% SZS output begin CNFRefutation` and `% SZS output end CNFRefutation` is the generated proof.

You can always try to verify the proof using [IDV](http://www.cs.miami.edu/~tptp/Seminars/IDV/Summary.html) which should succeed at least for simple problems.

#### Enabling external cooperation

Leo-III heavily relies on cooperation with (first-order) provers. Currently, Leo-III can cooperate with TPTP-compatible provers that support either THF or TFF syntax. At the moment, we implemented cooperation with LEO-II, Nitpick, CVC4, iProver (>= 2.6), E (>= 2.0), Alt-Ergo and Vampire.

To enable the cooperation (here CVC4), simply add e.g.
```
./leo3 PROBLEM --atp cvc4=/path/to/cvc4
```
to the call. Similar for the remaining provers. If the executables of the respective provers can be found in the `$PATH`, one may omit the path specification:
```
./leo3 PROBLEM --atp cvc4
```

## Modal logic reasoning

As of version 1.2, Leo-III supports reasoning in higher-order quantified (multi) modal logic.
Leo-III makes use of a semantical embedding approach [GSB17] and automatically embeds modal input problems
into corresponding HOL problems. No further tool or pre-processor is required.

### Modal Problem Syntax

Leo-III supports TPTP THF problem syntax augmented with modalities as sketched in the corresponding
[TPTP proposal](http://www.cs.miami.edu/~tptp/TPTP/Proposals/LogicSpecification.html). Hence,
problems containing `$box` or `$dia` are interpreted as modal logic problems.

An appropriate modal logic semantics is specified using the proposed logic specification syntax.
An input problem may contain a `logic` statement, for example
```
thf(modal_s5_standard,logic,(
    $modal :=
        [ $constants := $rigid,
          $quantification := $constant,
          $consequence := $global,
          $modalities := $modal_system_S5 ] )).
... (axioms or conjecture) ...
```

### Modal semantics parameters
If a problem uses modal logic connectives (i.e. `$box` or `$dia`) but does not specify
the modal logic using the above statement, you can pass modal logic semantic settings to Leo-III
directly using the following command-line-parameters:

| Parameter flag  | Effect |
| ------------- | ------------- |
| --assume-modal-system `system` | Use the modal logic `system` <br><br> Default: S5<br>Valid values: K, T, D, S4, S5 |
| --assume-modal-domains `domains` | Use the `domains` quantification semantics <br><br> Default value: constant<br>Valid values: constant, cumulative, decreasing, varying|
| --assume-modal-rigidity `rigidity` | Use `rigidity` constant symbols. <br><br> Default: rigid <br> Valid values: rigid, flexible|
| --assume-modal-consequence `consequence` | Use `consequence` as underlying consequence relation. <br><br> Default: global<br>Valid values: global, local |

Note that these parameters cannot (currently) be used to override the logic specification contained
in the problem (if existent).

### Example
The following simple problem can easily solved by Leo-III:

```
thf(simple_b, logic, ( $modal := [
    $constants := $rigid ,
    $quantification := $constant ,
    $consequence := $global ,
    $modalities := $modal_system_S5 ] ) ).


thf(axiom_B, conjecture, ( ![A:$o]: ( A => ( $box @ ( $dia @ A ) ) ) )).
```

The output generated by Leo-III is:

```
% [INFO] Input problem is modal. Running modal-to-HOL transformation from semantics specification contained in the problem file ... 
[...]
% SZS status Theorem for modal_problem.p : 2109 ms resp. 674 ms w/o parsing
```
