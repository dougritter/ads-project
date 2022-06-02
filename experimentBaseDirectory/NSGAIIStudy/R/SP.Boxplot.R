postscript("SP.Boxplot.eps", horizontal=FALSE, onefile=FALSE, height=8, width=12, pointsize=10)
resultDirectory<-"../data"
qIndicator <- function(indicator, problem)
{
fileNSGAIIa<-paste(resultDirectory, "NSGAIIa", sep="/")
fileNSGAIIa<-paste(fileNSGAIIa, problem, sep="/")
fileNSGAIIa<-paste(fileNSGAIIa, indicator, sep="/")
NSGAIIa<-scan(fileNSGAIIa)

algs<-c("NSGAIIa")
boxplot(NSGAIIa,names=algs, notch = FALSE)
titulo <-paste(indicator, problem, sep=":")
title(main=titulo)
}
par(mfrow=c(3,3))
indicator<-"SP"
qIndicator(indicator, "scheduleProblem")
qIndicator(indicator, "roomAllocationProblem")
