%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Example 2b: Quantification semantics
%
% see also QMLTP SYM002+1
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%--- logic specification
thf(spec, logic, ($modal := [
   $constants := $rigid,
   $quantification := $cumulative,
   $consequence := $global,
   $modalities := $modal_system_K ] ) ).

%--- Specify an uninterpreted predicate symbol f
thf(f_type, type, f: ($i > $o)).

%--- Does CBF: (□∀x.f(x)) → ∀x.□f(x) hold?
thf(barcan_formula, conjecture, (
          ($box @ ( ![X: $i]: ( f @ X ) ))
            => (![X:$i]: ( $box @ (f @ X) )) ) ).
