print("\n");

@include "ackermann.nut";
@include once "generators.nut";

local n;

if(vargv.len()!=0) {
   n = vargv[0].tointeger();
  if(n < 1) n = 1;
} else {
  n = 1;
}
print("n="+n+"\n");
print("Ack(3,"+ n+ "):"+ Ack(3, n));
