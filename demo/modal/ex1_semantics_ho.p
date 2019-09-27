%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Example 1b: Basic modal reasoning
%
% (corollary from Beckers postulate)
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%--- logic specification
thf(spec, logic, ( $modal := [
   $constants := $rigid,
   $quantification := $constant,
   $consequence := $global,
   $modalities := $modal_system_S5  ] )).

%--- Does there ∃g s.t. ◇□p(f(x)) → □p(g(x)) holds?
thf(1,conjecture,(
    ! [P: ( $i > $o ),F: ( $i > $i ),X: $i] :
    ? [G: ( $i > $i )] :
      ( ( $dia @ ( $box @ ( P @ ( F @ X ) ) ) )
     => ( $box @ ( P @ ( G @ X ) ) ) ) )).
