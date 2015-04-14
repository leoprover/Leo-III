#!/usr/bin/python

import sys
import string
import numpy

def main(argv):  
  if len(argv) <= 1:
    print "Error: No input files:"
    print "Usage: " + argv[0] + " <file1> [<file2> [<file3> [...]]] \n"
    return
    
  m = {} ## map for results
  ## Open first file
  fp = open(argv[1])
  initMap(m,fp,len(argv)-1)
  fp.close()
  
  ## Open all remaining files
  for f in argv[2:]:
    fp = open(f)
    fillMap(m,fp)
    fp.close()
      
  printEval(m, len(argv)-1)
  return 0
    
    
    
def initMap(m, fp, filecount):
  while True:
    line = fp.readline()
    if not line: break
    (k,v) = parseline(line)
    if filecount == 1:
      m[k] = v
    else:
      m[k] = [v]
  return


def fillMap(m,fp):
  while True:
    line = fp.readline()
    if not line: break
    (k,v) = parseline(line)
    if k not in m:
      m[k] = [v]
    else:
      m[k].append(v)
  return

def parseline(line):
  key,value = line.split("\t")
  key = key.strip()
  value = int(value.strip())
  return (key,value)
  
  
def printEval(m,filecount):
  if filecount > 1:
    print "Key\t\tMin\tMax\tSum\tMean\tMedian\tStd"
    print
    mins = []
    maxs = []
    sums = []
    means = []
    medians = []
    
    warning = False
    for k,vs in m.iteritems():
      if (len(vs) != filecount): warning = True
      mins.append(min(vs))
      maxs.append(max(vs))
      sums.append(sum(vs))
      means.append(numpy.mean(vs))
      medians.append(numpy.median(vs))
      print k + "\t\t" + str(min(vs)) + "\t" + str(max(vs)) + "\t" + str(sum(vs)) + "\t" + str(numpy.mean(vs)) + "\t" + str(numpy.median(vs)) + "\t" +  str(numpy.std(vs))      
      
    print
    print "\t\tMin\tMax\tSum\tMean*\tMedian*"
    print "Global\t\t"+ str(min(mins)) + "\t"+ str(max(maxs)) + "\t" + str(sum(sums)) + "\t" + str(numpy.around(numpy.mean(means),2)) + "\t" + str(numpy.around(numpy.median(medians),2))
    print
    print "(*: Mean of means resp. median of medians)"
    if (warning):
      print
      print "WARNING: Some data keys were missing in some input files."
  else:
    print "Min\tMax\tSum\tMean\tMedian\tStd"
    vs = m.values()
    print str(min(vs)) + "\t" + str(max(vs)) + "\t" + str(sum(vs)) + "\t" + str(numpy.mean(vs)) + "\t" + str(numpy.median(vs)) + "\t" +  str(numpy.std(vs)) + "\n"      
    ## insgesamt zusammenfassung
    
    

    
main(sys.argv)
