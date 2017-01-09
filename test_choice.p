
% this is the goal

thf(fthat_type,type, (
    i: (($i > $o) > $i)
)).

% thf(ffthat,definition,(
%    i = (
%      ^ [Phi: $i > $o] : ( $ite ( ? [X: $i] : ( ( Phi @ X ) & ( ! [Y: $i] : ( ( Phi @ Y ) => ( X = Y ) ) ) ) ,
%        @+[X: $i] :  ( Phi @ X ) ,
%        s ) ) )
% )).

% this is the test for choice

thf(test_choice_type,type,(
    c: (($i > $o) > $i)
)).

thf(test_choice,definition,(
    c = (
      ^ [Phi: $i > $o] : ( @+[X: $i] : ( ( eE @ X ) & ( Phi @ X ) ) ) )
)).

% thf(test_choice,definition,(
%    c = (
%      ^ [Phi: $i > $o] : ( @+[X: $i] : ( @ ( ( eE @ X ) & ( Phi @ X ) ) ) ) )
% )).

thf(r_const,type,(
    r: ($i > $i > $o)
)).

thf(lem,conjecture,(
   ! [X: $i] : ( ( r @ X @ X ) => ( r @ X @ X ) )
)).
