# -*- coding: utf-8 -*-
import sys
import math
from subprocess import Popen, PIPE
import time
import os

numNeurons=int(sys.argv[1])
Dw_Track_Resistance=2000
Rp=500
Rap=1
Rap=sys.argv[len(sys.argv)-3]
dc=sys.argv[len(sys.argv)-2]
fileCount=sys.argv[len(sys.argv)-1]

fileName=str(fileCount)+"_"+str(numNeurons)+"_Hopfield"


p= "// 3T-MTJ Hopfield Network"
p+="\nsimulator lang=spectre"
p+= "\ninclude \"/proj/cad/library/mosis/IBM_PDK/cmrf8sf/V1.8.0.4DM/Spectre/models/design.scs\""
p+= "\nahdl_include \"DW4T_Axon_linear.va\""
p+= "\nahdl_include \"switch4Schem_Veriloga.va\""
p+= "\nglobal 0"
pString="\nparameters"
for i in range(1,numNeurons+1):
  num=(i-1)*numNeurons+2
  pString+=" Val"+str(i)+"="+str(sys.argv[num])
  for j in range(1,numNeurons+1):
    if j != i:
      num=(i-1)*numNeurons+2+j
      if j > i:
        num=num-1
      pString+=" W"+str(i)+"_"+str(j)+"="+str(sys.argv[num])
p+=pString

for i in range(1,numNeurons+1):
  p+= "\n"
  p+= "\n// Neuron "+str(i)
  p+= "\nNeuron"+str(i)+" (n"+str(i)+"_Input 0 n"+str(i)+"_Up n"+str(i)+"_Down n"+str(i)+"_RMTJ n"+str(i)+"_PDW) DW4T_Axon_linear DW_V_leak=0.2 MTJ_Placement=0.5 DW_Track_Resistance="+str(Dw_Track_Resistance)+" Rp="+str(Rp)+" Rap="+str(Rap)+" Ith=8e-07 MTJ_Width=2e-08 DW_Length=1e-07 DW_Velocity=25 time_step=1e-12"
  p+= "\nV_Up_n"+str(i)+" (n"+str(i)+"_Up 0) vsource dc="+str(dc)+" type=dc"
  p+= "\nSwitch_n"+str(i)+" (SwitchingHigh_n"+str(i)+" SwitchingLow_n"+str(i)+" SwitchOut_n"+str(i)+" SwitchIn_n"+str(i)+") switch3 thresh=0 Ron=1e-06 Roff=1e+13"
  p+= "\nVSwitching_n"+str(i)+" (SwitchingHigh_n"+str(i)+" SwitchingLow_n"+str(i)+") vsource dc=-0 type=pwl period=360n rise=10p fall=10p wave=[ 0 1 "+str(2*15)+"n -1 ]"
  p+= "\nVInitial_n"+str(i)+" (SwitchIn_n"+str(i)+" 0) vsource dc=Val"+str(i)+" type=dc"
  p+= "\nR_n"+str(i)+" (SwitchOut_n"+str(i)+" n"+str(i)+"_Input) resistor r=1"
  for j in range(1,numNeurons+1):
    if j != i:
      p+= "\nn"+str(i)+"Out_"+str(j)+" (n"+str(i)+"_Down 0 n"+str(i)+"Out_"+str(j)+"_Up n"+str(j)+"_Input n"+str(i)+"Out_"+str(j)+"_Down_RMTJ n"+str(i)+"Out_"+str(j)+"_Down_PDW) DW4T_Axon_linear DW_V_leak=-5 MTJ_Placement=0.5 DW_Track_Resistance="+str(Dw_Track_Resistance)+" Rp="+str(Rp)+" Rap="+str(Rap)+" Ith=8e-07 MTJ_Width=2e-08 DW_Length=1e-07 DW_Velocity=25 time_step=1e-12"
      p+= "\nV_Up_n"+str(i)+"Out_"+str(j)+" (n"+str(i)+"Out_"+str(j)+"_Up 0) vsource dc=W"+str(i)+"_"+str(j)+" type=dc"
p+= "\n"
p+= "\ntran_"+str(numNeurons)+"_Neurons tran stop="+str(200+(0*i))+"n maxstep=1p step=1p writefinal="+fileName+"Out.txt\n"
f=open(fileName+'.scs', "w")
f.write(p)
f.close()
po= Popen(["spectre",fileName+".scs"],stdout=PIPE)
po.wait()
po= Popen(["grep","-E","n[0-9]{1,}_PDW",fileName+"Out.txt"],stdout=PIPE)
po.wait()
print (po.communicate()[0].strip())
po= Popen(["rm","-rf",fileName+".raw"],stdout=PIPE)
po.wait()

sum=0
f=open("a.sp","r");
lines = f.readlines();
length = len(lines)
for line in lines:
  sum = sum + float(line.strip())
f.close()
po= Popen(["rm","-f","a.sp"],stdout=PIPE)
po.wait()

f=open("engCon.txt","a+")
f.write(str(sum/(length/(numNeurons+(numNeurons*(numNeurons-1))))))
f.write('\n');
f.close()

po= Popen(["rm","-rf",fileName+".ahdlSimDB"],stdout=PIPE)
po.wait()
po= Popen(["rm","-f",fileName+".scs"],stdout=PIPE)
po.wait()
#po= Popen(["rm","-f",fileName+"Out.txt"],stdout=PIPE)
#po.wait()