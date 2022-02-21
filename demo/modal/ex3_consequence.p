%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Example 3: Consequence
% locality of assumptions can be overriden with subroles: 
%  - role "axiom-local" will be local, otherwise "axiom" is global,
%  - role "hypothesis-global" will be local, otherwise "hypothesis" is local,
%  - role "conjecture-global" will be global, otherwise "conjecture" is local (standard modal logic consequence),
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%--- logic specification
thf(spec, logic, ($modal == [
   $constants == $rigid,
   $quantification == $constant,
   $modalities == $modal_system_K ] ) ).

%--- a is some formula
thf(a_type, type, a:$o).
%--- a is global assumption (role "axiom")
thf(ax1, axiom, a).

%--- alternative: a is local assumption (role "hypothesis" used for that)
%thf(ax1, hypothesis, a).

%--- Does □a hold? (true in case of globally valid ax1, otherwise not)
thf(c, conjecture, ([.] @ a)).

% alternative: is □a valid in every world?
%thf(c, conjecture-global, ([.] @ a)).
