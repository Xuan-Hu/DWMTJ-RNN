# -*- coding: utf-8 -*-
import sys
import math
import random
import time
from subprocess import Popen, PIPE

nLow=int(sys.argv[1])
nHigh=int(sys.argv[2])
maxPatterns = 1
numTest=1
maxThreads=1
timeWait=0.5

Rp=500
Rap=-1

Rd=2000
lSyn=-5
lAxon=0.2
k=265933.0
dwLength=float('2e-07')

dwErrorPercentAllowed = 0.25
currentThreshold=0.0000008
proportionOfPwr=1.008

counter=0
for n in range(nLow,nHigh+1):
	maxCount=2**n
	#runs a network of n neurons for each n in range
	pwr=((math.sqrt(Rp*(16*Rd+9*Rp+8*n*n*Rd+4*n*n*Rp-24*n*Rd-12*n*Rp))-3*Rp+2*n*Rp)/(2*Rp))
	Rap = Rp * proportionOfPwr*pwr
	Rap = Rp * 4
	weight=(lAxon/k)*(((Rap+Rp)/(2.0*(n-1)))+Rd)
	weight=2*(lAxon/k)*(((1.99*Rap+0.01*Rp)/(2.0*(n-1)))+Rd)
	weight=0.1

	dc=(-lSyn/k)*((n-1)*((Rap+Rp)/2.0)+Rd)
	
	for numPatterns in range(maxPatterns, maxPatterns+1):
		passCount=0
		failCount=0
		for c in range(0,numTest/maxThreads):
			patterns=[[] * numPatterns for _ in range(maxThreads)]
			j=[0]*maxThreads
			poList = []	
			for t in range(0,maxThreads):
				patterns[t]+=[3]
				for q in range(0,numPatterns):
					#patterns[t]+=[random.randrange(0,maxCount)]
					print(1)
					#asdfasdjflasdkf
					#if q == 0:
					#	patterns[t][0]=3
					#lkafjlkdakfljsjlkfds
				#represents the trained value of our network
				networkValues = [[0] * n for _ in range(numPatterns)]
				
				for p in range(0,numPatterns):
					pCop = patterns[t][p]
					index=n-1
					while index>=0:
						networkValues[p][index]=pCop%2
						index-=1
						pCop/=2
				
				for p in range(0,numPatterns):
					for pp in range(0,n):
						print (networkValues[p][pp]),
					print ""	

				weights = [[0]*n for _ in range(n)]
				for a in range(0,n):
					for b in range(0,n):
						if a != b:
							diff=0.0
							for p in range(0,numPatterns):
								diff += (1.0 if networkValues[p][a]==networkValues[p][b] else -1.0)
							weights[a][b] = weight * (diff/numPatterns)
						print(weights[a][b]),
					print ""
				
				j[t] = random.randrange(0,maxCount)
				#jkhasdfaklsfdjlkfd
				j[t]=15
				#aslkdjflkjsadfljk
				testNetworkValues=[0]*n
				jCop=j[t]
				index=n-1
				while jCop>0:
					testNetworkValues[index]=jCop%2
					index=index-1
					jCop/=2
				com = []+["python"]+["testOne.py"]+[str(n)]
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
				poList += [po]
				time.sleep(timeWait);
				#print(counter)			
			
			for t in range(0, maxThreads):
				output=poList[t].communicate()[0]
				splitOutput=output.splitlines()
				# outputVal represents the output of the network as a integer read of the bincmary numbers
				
				outputVal=0
				flippedOutputVal=0
				strOut=""
				for a in range(0,n):
					pos=float(splitOutput[a].split()[1])
					if pos<=dwLength*dwErrorPercentAllowed:
						strOut=strOut+"0"
						flippedOutputVal=flippedOutputVal+(2**(n-a-1))
					elif pos>=dwLength*(1.0-dwErrorPercentAllowed):
						strOut=strOut+"1"
						outputVal=outputVal+(2**(n-a-1))
					else:
						strOut=strOut+"X"
				passed=False
				for pattern in patterns[t]:
					if pattern==outputVal or pattern==flippedOutputVal:
						passed=True
				print "TRAIN: "+str(patterns[t]) +"TEST: "+format(j[t], '0'+str(n)+'b') + "->" + strOut + (" Pass" if passed else " Fail") 
				if passed:
					passCount+=1
				else:
					failCount+=1
		
		print str(n)+" Neurons "+ str(numPatterns) +" Patterns " + str(passCount/(passCount+failCount*1.0))