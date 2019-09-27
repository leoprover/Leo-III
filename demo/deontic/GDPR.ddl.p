%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Example: GDPR in Dyadic deontic logic (Carmo/Jones)
%
% $O(p) corresponds to OB (obligatory) p
% $O(p/q) corresponds to 'it ought to be p given q'
% ...
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%--- It is obligatory to process data lawfully
ddl(a1, axiom, $O(processDataLawfully)).

%--- If data is not processed lawfully it has to be erased
ddl(a2, axiom, (~processDataLawfully) => $O(eraseData)).

%--- Data is actually not processed lawfully
ddl(situationalAx, localAxiom, ~processDataLawfully). 

%--- Is it obligatory to delete the data?
ddl(c1, conjecture, $O(eraseData)).

%%% Commented out
%--- Consistency check: Is it obligatory to kill the boss?
%ddl(c3, conjecture, $O(kill)).
