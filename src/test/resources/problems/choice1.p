
thf(a,type,a:$i).
thf(b,type,b:$i).
thf(ax,axiom,(![F:$i>$i]: ( ~((((@+[Y:$i]: (
  ((a = a) => (Y = b)) & ((a=b) => (Y=a))
)))) = b) | ~((((@+[Y:$i]: (
  ((b = a) => (Y = b)) & ((b=b) => (Y=a))
)))) = a)))).
