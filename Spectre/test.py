# -*- coding: utf-8 -*-
import sys
import math
from subprocess import Popen, PIPE

nLow=int(sys.argv[1])
nHigh=int(sys.argv[2])

Rp=500
Rap=-1

Rd=2000
lSyn=-5.0
lAxon=0.1
k=4*265933.0
dwLength=float('1e-07')
dwErrorPercentAllowed = 0.05
currentThreshold=0.0000008
proportionOfPwr=1.008

counter=0
for n in range(nLow,nHigh+1):
	passCount=0
	failCount=0
	maxCount=2**n
	maxTest=2**(n/2)
	maxTest=2**n
	#runs a network of n neurons for each n in range
	pwr=((math.sqrt(Rp*(16*Rd+9*Rp+8*n*n*Rd+4*n*n*Rp-24*n*Rd-12*n*Rp))-3*Rp+2*n*Rp)/(2*Rp))
	Rap = Rp * 4
	weight=(lAxon/k)*(((Rap+Rp)/(2.0*(n-1)))+Rd)
	weight=0.1
	dc=(-lSyn/k)*((n-1)*((Rap+Rp)/2.0)+Rd)

	pattern = 5
	#pattern = 0
	#while pattern<maxTest:
	while pattern<6:
		print("Train: "+ str(pattern))
		#represents the trained value of our network
		networkValues=[0]*n # for _ in range treats by value * treats by reference
		pCop = pattern
		index=n-1
		while index>=0:
			networkValues[index]=pCop%2
			index-=1
			pCop/=2
		weights = [[0]*n for _ in range(n)]
		for a in range(0,n):
			for b in range(0,n):
				if a != b:
					diff = (1.0 if networkValues[a]==networkValues[b] else -1.0)
					weights[a][b] = weight * diff 

#		for j in range(0,maxCount):
		for j in range(2,3):
			testNetworkValues=[0]*n
			jCop=j
			index=n-1
			while jCop>0:
				testNetworkValues[index]=jCop%2
				index=index-1
				jCop=int(jCop/2)
			com = []+["python3"]+["testOne.py"]+[str(n)]
			# creates the string for each run
			for a in range (0,n):
				com+=[str(0.25)] if testNetworkValues[a]==1 else [str(-0.25)]
				for b in range(0,n):
					if a!=b:
						com+=[str(weights[a][b])]
			com+=[str(Rap)]
			com+=[str(dc)]
			com+=[str(counter)]
			counter+=1
			po=Popen(com,stdout=PIPE)
			output=po.communicate()[0]
			splitOutput=output.split()
			print('a')
			print(output)
			print('b')
			# outputVal represents the output of the network as a integer read of the binary numbers
			outputVal=0
			flippedOutputVal=0
			strOut=""
			for a in range(0,n):
				pos=float(splitOutput[a*2].split(b"\t")[1])
				if pos<=dwLength*dwErrorPercentAllowed:
					strOut=strOut+"0"
					flippedOutputVal=flippedOutputVal+(2**(n-a-1))
				elif pos>=dwLength*(1.0-dwErrorPercentAllowed):
					strOut=strOut+"1"
					outputVal=outputVal+(2**(n-a-1))
				else:
					strOut=strOut+"X"
			passed=False
			if pattern==outputVal or pattern==flippedOutputVal:
				passed=True
			print ("TEST: "+format(j, '0'+str(n)+'b') + "->" + strOut + (" Pass" if passed else " Fail"))
			if passed:
				passCount+=1
			else:
				failCount+=1
		#pattern = pattern * 2
		pattern = pattern +1
	print (str(n)+" Neurons: "+ str(passCount/(passCount+failCount*1.0)))

sum=0
f=open("engCon.txt","r");
lines = f.readlines();
length = len(lines)
for line in lines:
  sum = sum + float(line.strip())
f.close()
print(sum/length)
