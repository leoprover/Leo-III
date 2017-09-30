thf(aType, type, aType: $tType).

thf(a, type, a:aType).
thf(b, type, b:aType).
thf(c, type, c:aType).

thf(constraint,axiom,( ! [X:aType]: ((X = a) | (X = b) | (X = c)))).
