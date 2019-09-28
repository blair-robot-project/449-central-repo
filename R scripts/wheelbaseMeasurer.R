rad2deg <- function(rad) {return((rad * 180) / (pi))}
deg2rad <- function(deg) {return((deg * pi) / (180))}

#Smooths a value while taking its derivative with respect to time.
smoothDerivative <- function(value, timeMillis, n){
  smoothed <- (value[(n+1):length(value)] - value[1:(length(value)-n)])/((timeMillis[(n+1):length(timeMillis)] - timeMillis[1:(length(timeMillis)-n)])/1000);
  return(smoothed);
}

wheelbaseVsAngVel <- function(logTime, clockTime, angularDisplacementDegrees, leftPos, rightPos, n, minDegreesPerSec){
  deltaLeft <- smoothDerivative(leftPos, logTime, n)
  deltaRight <- smoothDerivative(rightPos, logTime, n)
  deltaTheta <- smoothDerivative(angularDisplacementDegrees, clockTime, n)
  dat <- data.frame(deltaLeft = deltaLeft, deltaRight = deltaRight, deltaTheta = deltaTheta)
  dat <- subset(dat, abs(deltaTheta) > minDegreesPerSec)
  dat <- subset(dat, is.finite(deltaTheta))
  dat$wheelbase <- (dat$deltaRight - dat$deltaLeft)/deg2rad(dat$deltaTheta)
  dat$absDTheta <- abs(dat$deltaTheta)
  model <- lm(dat$wheelbase ~ dat$absDTheta)
  plot(dat$absDTheta, dat$wheelbase)
  abline(model, col="green")
  print(paste("Weighted average wheelbase:",weighted.mean(dat$wheelbase, dat$absDTheta)))
  return(model)
}

oldWheelbaseVsAngVel <- function(logTime, angularVel, leftPos, rightPos, n, minDegreesPerSec){
  deltaLeft <- smoothDerivative(leftPos, logTime, n)
  deltaRight <- smoothDerivative(rightPos, logTime, n)
  deltaTheta <- angularVel[ceiling(n/2+1):(length(angularVel)-floor(n/2))]
  dat <- data.frame(deltaLeft = deltaLeft, deltaRight = deltaRight, deltaTheta = deltaTheta)
  dat <- subset(dat, abs(deltaTheta) > minDegreesPerSec)
  dat <- subset(dat, is.finite(deltaTheta))
  dat <- subset(dat, is.finite(deltaLeft))
  dat <- subset(dat, is.finite(deltaRight))
  dat$wheelbase <- (dat$deltaRight - dat$deltaLeft)/deg2rad(dat$deltaTheta)
  dat$absDTheta <- abs(dat$deltaTheta)
  model <- lm(dat$wheelbase ~ dat$absDTheta)
  plot(dat$absDTheta, dat$wheelbase)
  abline(model, col="green")
  print(paste("Weighted average wheelbase:",weighted.mean(dat$wheelbase, dat$absDTheta)))
  return(model)
}