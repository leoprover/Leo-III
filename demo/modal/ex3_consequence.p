%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Example 3: Consequence
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%--- logic specification
thf(spec, logic, ($modal := [
   $constants := $rigid,
   $quantification := $constant,
   $consequence := $local,
   $modalities := $modal_system_K ] ) ).

%--- a is some formula
thf(a_type, type, a:$o).
%--- a is valid
thf(ax1, axiom, a).

%--- Does □a hold?
thf(c, conjecture, ($box @ a)).
