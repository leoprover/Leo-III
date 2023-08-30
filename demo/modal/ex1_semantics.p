%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Example 1: Basic modal reasoning
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%--- logic specification
thf(spec, logic, ( $modal == [
   $designation == $rigid,
   $domains == $constant,
   $modalities == $modal_system_S5 ] )).

%--- does ϕ → □◇ϕ hold?
thf(mysterious, conjecture, ![A:$o]: (A => [.]<.> A) ).
