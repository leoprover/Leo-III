%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Example 2: Quantification semantics (using long-form connectives)
%
% see also QMLTP SYM001+1
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%--- logic specification
thf(spec, logic, ($modal == [
   $constants == $rigid,
   $quantification == $decreasing,
   $modalities == $modal_system_K ] ) ).

%--- Specify an uninterpreted predicate symbol f
thf(f_type, type, f: ($i > $o)).

%--- Barcan formula (∀x.□f(x)) → □∀x.f(x) holds for decreasing domains
thf(barcan_formula, conjecture, (
          (![X:$i]: ( {$box} @ (f @ X) ))
            => ({$box} @ ( ![X: $i]: ( f @ X ) )) ) ).
