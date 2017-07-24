with import <nixpkgs> {}; 

stdenv.mkDerivation rec {
  name = "Leo-III";
  buildInputs = [stdenv openjdk cmake gcc sbt];
  JAVA_HOME = "${openjdk}/lib/openjdk/";
  shellHook = ''
      export _JAVA_OPTIONS=-Duser.home=$HOME
  '';
}
