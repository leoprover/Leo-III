
% this is the goal

thf(fthat_type,type, (
    i: (($i > $o) > $i)
)).

% thf(ffthat,definition,(
%    i = (
%      ^ [Phi: $i > $o] : ( $ite ( ? [X: $i] : ( ( Phi @ X ) & ( ! [Y: $i] : ( ( Phi @ Y ) => ( X = Y ) ) ) ) ,
%        @+ ( Phi @ X ) ,
%        s ) ) )
% )).

% this is the test for if-then-else

thf(11, axiom, $let(f(X, Y) := X = Y, $ite(![X, Y] : f(X, Y), $true, $false))).
