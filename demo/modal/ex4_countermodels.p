%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Example 4: Counter-models
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%--- logic specification
thf(spec, logic, ($modal := [
   $constants := $rigid,
   $quantification := $constant,
   $consequence := $global,
   $modalities := $modal_system_K ] ) ).

%--- a is some formula
thf(a_type, type, a:$o).
%--- □a is valid
thf(ax1, axiom, $box @ a).

%--- Does a hold?
thf(c, conjecture, a).
