# Leo-III Usage

## Basics

Leo-III is called from command line:
```Shell
> leo3
Leo III -- A Higher-Order Theorem Prover.
Christoph Benzmüller, Alexander Steen, Max Wisniewski and others.

Usage: leo3 problem [option ...]
[...]
```
The above example assumes that `leo3` can be found in your `$PATH`. This will be assumed in
the remainder of this document. If this is not the case, consider adding `leo3` to your `$PATH`
or just replace every occurence of `leo3` in the following examples with the respective quantified path.

For using Leo-III to solve a problem `/path/to/someproblem.p`, just give the problem file as the first argument:
```Shell
> ./leo3 /path/to/someproblem.p
% SZS status Theorem for /path/to/someproblem.p : 3651 ms resp. 1253 ms w/o parsing
```

> :information_source: __Note:__  *As an alternative to the above example, Leo-III can also read problems from stdin. For this to work, please provide a dash '-' as problem name argument. Example:*
> ```Shell
> > cat myproblem.p | ./leo3 -
> ```

The line starting with `% SZS status XXX` indicates the result of Leo-III's reasoning process according
to the [SZS status ontology](http://www.cs.miami.edu/~tptp/cgi-bin/SeeTPTP?Category=Documents&File=SZSOntology) [Sut08].
Prominent result values for `XXX` are:

| Result value | Meaning |
| ------------- | ------------- |
| `Theorem` | The conjecture contained in the problem was successfully proven. See below on how to generate a proof certificate |
| `Timeout` | Leo-III could not establish any result value within the given time restrictions (Increasing the time limit might help for finding a proof) |
| `GaveUp` | Leo-III could not establish any result value (Increasing the time limit will not help) |
| `InputError` | The given problem file could not be found or it was not readable (I/O error) |
| `SyntaxError` | The problem file contains syntactical errors and hence cannot be assessed |
| `TypeError` | The problem file contains typing errors and hence cannot be assessed |


### Important parameters

Parameters are passed *after* the problem file path without an equality sign "=", if used with parameter value: `./leo3 problem -param1 -param2 10`

The most important parameters are

| Parameter flag  | Effect |
| ------------- | ------------- |
| -p  | Output a refutation proof, if one was found <br><br> Default: false (not set) |
| -t `s`  | Set the timeout to `s` seconds, meaning that Leo-III will try to proof the problem and return `SZS_Timeout` if not successful after approximately `s`  seconds <br><br> Default value: 60<br>Valid values: non-negative numbers|
| --atp `system` | Use `system` for external cooperation. See further below. <br><br> Default: none set <br> Valid values: See "External cooperation" below.|
| --primsubst `level` | Use the "itensity" `level` for instantiating flexible heads according to the primitive substitution rule <br><br> Default: 1<br>Valid values: 1-6 |
| --unifiers `n` | During unification, use at most `n` distinct unifiers<br> <br> Default: 1<br> Valud values: non-negative numbers |
| --unidepth `n` | During unification, use `n` as maximal unification search depth <br><br>Default: 8<br>Valid values: Non-negative numbers |

There are many more parameters, we will add them here some time in the future.

### Proving TPTP problems

A standard library for reasoning problems is the TPTP library (http://tptp.org/). Some problems of this library use `include` statements. In order to resolve them correctly, you might need to set the `TPTP` environment variable correctly, e.g.
```
export TPTP=/path/to/TPTP
```
If this environment variable is set, Leo-III will automatically resolve TPTP axioms.

### Example

Let's solve the TPTP problem `SET014^4.p` (see [here](http://www.cs.miami.edu/~tptp/cgi-bin/SeeTPTP?Category=Problems&Domain=SET&File=SET014^4.p)) with a timeout of 60 seconds and proof output:

```
> ./leo3 path/to/SET014^4.p -t 60 -p
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

### Enabling external cooperation

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

**Note**: Since Leo-III 1.6.7 the modal logic format (in general: non-classical logic) changed and was aligned
to the proposed non-classical TPTP standard (http://tptp.org/NonClassicalLogic/).
The embedding tool included in Leo-III now supports more logics next to modal logics, see
[github.com/leoprover/logic-embedding](https://github.com/leoprover/logic-embedding) for details.

### Modal Problem Syntax

Leo-III supports TPTP THF and TFF problem syntax augmented with non-classical logic syntax as sketched in the corresponding
[TPTP proposal](http://tptp.org/NonClassicalLogic/).
Problems containing `[.]` (modal logic box) or `<.>` (modal logic diamond) are interpreted as modal logic problems.
The long forms `{$box}` and `{$dia}` may also be used instead of `[.]` and `<.>`, respectively.
Multi-modal logics are also supported (see, e.g.,  `demo/modal/ex5_multimodal_wisemen.p`).

An appropriate modal logic semantics is specified using the proposed logic specification syntax.
An input problem may contain a `logic` statement, for example
```
thf(modal_s5_standard,logic,(
    $modal ==
        [ $constants == $rigid,
          $consequence == $global,
          $modalities == $modal_system_S5 ] )).
... (global and local assumptions, conjecture) ...
```

### Modal semantics parameters

| Parameter         | Description                                                                                                                                                                                                                                                                                                                                                     |
|-------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `$quantification` | Selects whether quantification semantics is varying domains, constant domains, cumulative domains or decreasing domains.<br><br>Accepted values: `$varying`, `$constant`, `$cumulative`, `$decreasing`                                                                                                                                                          |
| `$constants`      | Selects whether constant and functions symbols are interpreted as rigidor flexible.<br><br> Accepted values: `$rigid`, `$flexible` *(Leo-III currently only supports rigid constants)*                                                                                                                                                                          |
| `$modalities`     | Selects the properties for the modal operators.<br><br> Accepted values, for each modality: `$modal_system_X` where `X` ∈ {`K`, `KB`, `K4`, `K5`, `K45`, `KB5`, `D`, `DB`, `D4`, `D5`, `D45`, `T`, `B`, `S4`, `S5`, `S5U`} <br>_or a list of axiom schemes_<br> [`$modal axiom X1` , ..., `$modal axiom Xn` ] `Xi` ∈ {`K`, `T`, `B`, `D`, `4`, `5`, `CD`, `C4`} |


### Modal semantics parameters

Formula roles are used to indicate whether assumptions are global (i.e., assumed to be valid in every world)
or local (i.e., assumed true in the current world).

  - Formulas with role `hypothesis` are local assumptions
  - Formulas with role `axiom` are global assumptions
  - A conjecture is always assumed to be local (default modal logic consequence relation).
  - These default role interpretations may be overriden by the subroles `local` and `global`, e.g., ...
    - A formula with role `axiom-local` is a local assumption 
    - A formula with role `hypothesis-global` is a global assumption
    - A conjecture with role `conjecture-global` asks whether the formula is true in every world
    

### Examples
#### Example 1
The following simple problem can easily solved by Leo-III:

```
thf(simple_b, logic, ( $modal == [
    $constants == $rigid ,
    $quantification == $constant ,
    $modalities == $modal_system_S5 ] ) ).


thf(axiom_B, conjecture, ( ![A:$o]: ( A => ( [.] @ ( <.> @ A ) ) ) )).
```

The output generated by Leo-III is:

```
% [INFO] 	 Input problem is non-classical. Running HOL transformation from semantics specification contained in the problem file ...
[...]
% SZS status Theorem for modal_problem.p : 1073 ms resp. 674 ms w/o parsing
```

#### Example 2
A multi-modal logic example:

```
tff(multimodal,logic,(
    $modal ==
      [ $constants == $rigid,
        $quantification == $cumulative,
        $modalities == $modal_system_S5 ] )).
            
tff(1, axiom, [#a](p & q)).
tff(2, hypothesis, <#b>(q)).
tff(c, conjecture, <#b>(p & q)).
```

## Non-classical reasoning in general

Since version 1.6.7 Leo-III also supports reasoning in further non-classical logics.
The input syntax uses the same TPTP extensions as for modal logic; the connectives and 
logic specification parameters vary from logic to logic. The supported logics are documented
at https://github.com/leoprover/logic-embedding
