#Simple helper conversion methods
rad2deg <- function(rad) {(rad * 180) / (pi)}
deg2rad <- function(deg) {(deg * pi) / (180)}

logLogSlope <- function(effectiveWheelbase, angularVel, n){
  library("zoo")
  avg <- weighted.mean(x=effectiveWheelbase, w=angularVel, na.rm=TRUE)
  error <- effectiveWheelbase-avg
  error <- error^2
  avgError <- sqrt(rollmean(error, n, fill = NA))
  logError <- log(avgError[(1+floor((n-1)/2)):(length(avgError)-floor(n/2))])
  logVel <- log(angularVel[(1+floor((n-1)/2)):(length(angularVel)-floor(n/2))])
  plot(x = logVel, y= logError)
  model <- lm(logError ~ logVel)
  abline(model, col="Green")
  return(model)
}

drawRobot <- function(robotFile, x, y, theta){
  robotCenter <- c(x,y)
  robot <- read.csv(robotFile)
  rotMatrix <- matrix(c(cos(theta), -sin(theta), sin(theta), cos(theta)), nrow=2, ncol=2, byrow=TRUE)
  
  point1s <- rotMatrix %*% matrix(c(robot$x1, robot$y1), nrow = 2, ncol = length(robot$x1), byrow = TRUE) 
  point1s <- point1s + c(robotCenter[1], robotCenter[2])
  
  point2s <- rotMatrix %*% matrix(c(robot$x2, robot$y2), nrow = 2, ncol = length(robot$x1), byrow = TRUE) 
  point2s <- point2s + c(robotCenter[1], robotCenter[2])
  
  #Interleave the point1s and point2s so lines() draws them correctly.
  xs <- c(rbind(point1s[1,], point2s[1,]))
  ys <- c(rbind(point1s[2,], point2s[2,]))
  
  lines(x=xs, y=ys, col="Blue")
}

plotField <- function(filename, xOffset=0, yOffset=0){
  field <- read.csv(filename)
  #Strings are read as factors by default, so we need to do this to make it read them as strings
  field$col <- as.character(field$col)
  for (i in 1:length(field$x1)){
    lines(c(field$x1[i]+xOffset, field$x2[i]+xOffset), c(field$y1[i]+yOffset, field$y2[i]+yOffset), col=field$col[i])
  }
}

#Find the distance between the end position of two things
dist <- function(x1, y1, x2, y2){
  return(sqrt((x1[length(x1)] - x2[length(x2)])^2+(y1[length(y1)] - y2[length(y2)])^2))
}

#Calculate the effective wheelbase for a given delta left, right, and angle
calcWheelbase <- function(deltaLeft, deltaRight, deltaAngle){
  return((deltaRight-deltaLeft)/deltaAngle);
}

#Smooths a value while taking its derivative with respect to time.
smoothDerivative <- function(value, timeMillis, n){
  smoothed <- (value[(n+1):length(value)] - value[1:(length(value)-n)])/((timeMillis[(n+1):length(timeMillis)] - timeMillis[1:(length(timeMillis)-n)])/1000);
  return(c(rep(0, ceiling(n/2)), smoothed, rep(0, floor(n/2))));
}

#Plot the effective wheelbase against a bunch of different things
plotWheelVsVel <- function(leftPos, rightPos, rawAngleDegrees, timeMillis, angularVelThreshRad, smoothingConst){
  #Convert because degrees suuuuck
  rawAngle <- deg2rad(rawAngleDegrees)
  
  #Smooth values and get velocities
  angular <- smoothDerivative(rawAngle, timeMillis, smoothingConst)
  left <- smoothDerivative(leftPos, timeMillis, smoothingConst)
  right <- smoothDerivative(rightPos, timeMillis, smoothingConst)
  
  #find effective wheelbase
  wheelbase <- calcWheelbase(left, right, angular)
  
  #Filter out low angular vel points
  combined <- cbind(angular, wheelbase, (left+right)/2)
  combinedAngular <- subset(combined, combined[,1] > angularVelThreshRad)
  
  #Find the mean wheelbase, weighted by angular vel because higher angular vel decreases variance
  avgWheelbase = weighted.mean(x=combinedAngular[,2], w=combinedAngular[,1], na.rm=TRUE)
  
  #plot wheelbase vs angular vel
  plot(x=combinedAngular[,1], y=combinedAngular[,2], xlab="Angular Velocity (rad/sec)", ylab="Effective wheelbase diameter (feet)", main="Effective Wheelbase Diameter vs. Angular Velocity")
  abline(a=avgWheelbase, b=0, col='green')
  
  #plot wheelbase vs linear vel
  plot(x=combinedAngular[,3], y=combinedAngular[,2], xlab="Linear Velocity (feet/sec)", ylab="Effective wheelbase diameter (feet)", main="Effective Wheelbase Diameter vs. Linear Velocity")
  abline(a=avgWheelbase, b=0, col='green')
  
  #Plot wheelbase vs turn radius
  plot(x=combinedAngular[,3]/combinedAngular[,1], y=combinedAngular[,2], xlab="Turn Radius (feet)", ylab="Effective wheelbase diameter (feet)", main="Effective Wheelbase Diameter vs. Turn Radius")
  abline(a=avgWheelbase, b=0, col='green')
  
  return(avgWheelbase)
}

#A pose estimation algorithm that assumes the left and right sides have equal scrub, in opposite directions
equalScrubPoseEstimation <- function(leftPos, rightPos, rawAngleDegrees, timeMillis, angularVelThreshRad, realWheelbase = -1){
  #Convert because degrees suuuuck
  rawAngle <- deg2rad(rawAngleDegrees)
  
  #Set up output array
  out <- array(dim=c(length(timeMillis),8))
  colnames(out)<-c("X","Y","leftX","leftY","rightX","rightY","time","wheelbase")
  if(realWheelbase == -1){
    out[1,] <- c(0,0,NA,NA,NA,NA,timeMillis[1],NA)
  } else {
    out[1,] <- c(0,0, realWheelbase/2*cos(rawAngle[1]+pi/2), realWheelbase/2*sin(rawAngle[1]+pi/2), realWheelbase/2*cos(rawAngle[1]-pi/2), realWheelbase/2*sin(rawAngle[1]-pi/2), timeMillis[1],realWheelbase)
  }
  
  #Loop through each logged tic, calculating pose iteratively
  for(i in 2:length(timeMillis)){
    #Find change in time, in seconds
    deltaTime <- (timeMillis[i] - out[i-1,7])/1000
    
    #Directly find change in position and angle
    deltaLeft <- leftPos[i]-leftPos[i-1]
    deltaRight <- rightPos[i]-rightPos[i-1]
    deltaTheta <- rawAngle[i]-rawAngle[i-1]
    
    #Get the effective wheelbase for this iteration
    wheelbase <- calcWheelbase(deltaLeft, deltaRight, deltaTheta)
    
    #Average
    avgMoved <- (deltaLeft+deltaRight)/2
    
    #The angle of the movement vector
    angle <- rawAngle[i-1]+(deltaTheta/2)

    if (deltaTheta == 0){
      x <- out[i-1,1]+avgMoved*cos(angle)
      y <- out[i-1,2]+avgMoved*sin(angle)
      #If we're driving straight, we know the magnitude is just the average of the two sides
      if(realWheelbase == -1){
        out[i,] <- c(out[i-1,1]+avgMoved*cos(angle),out[i-1,2]+avgMoved*sin(angle), NA, NA, NA, NA, timeMillis[i],NA) 
      } else {
        out[i,] <- c(x, y, x+realWheelbase/2*cos(rawAngle[i]+pi/2), y+realWheelbase/2*sin(rawAngle[i]+pi/2), x+realWheelbase/2*cos(rawAngle[i]-pi/2), y+realWheelbase/2*sin(rawAngle[i]-pi/2), timeMillis[i],realWheelbase)
      }
    } else {
      #Magnitude of movement vector is 2*r*sin(deltaTheta/2), but r is just avg/deltaTheta
      mag <- 2*(avgMoved/deltaTheta)*sin(deltaTheta/2)
      
      #Vector decomposition
      x <- out[i-1,1]+mag*cos(angle)
      y <- out[i-1,2]+mag*sin(angle)
      
      if(realWheelbase == -1){
        #Only log left and right wheel positions if the angular vel is above threshhold
        if (deltaTheta/deltaTime < angularVelThreshRad){
          out[i,] <- c(x, y, NA,NA,NA,NA, timeMillis[i],NA)
        } else {
          out[i,] <- c(x, y, x+wheelbase/2*cos(rawAngle[i]+pi/2), y+wheelbase/2*sin(rawAngle[i]+pi/2), x+wheelbase/2*cos(rawAngle[i]-pi/2), y+wheelbase/2*sin(rawAngle[i]-pi/2), timeMillis[i],wheelbase)
        }
      } else {
        out[i,] <- c(x, y, x+realWheelbase/2*cos(rawAngle[i]+pi/2), y+realWheelbase/2*sin(rawAngle[i]+pi/2), x+realWheelbase/2*cos(rawAngle[i]-pi/2), y+realWheelbase/2*sin(rawAngle[i]-pi/2), timeMillis[i],realWheelbase)
      }
    }
  }
  
  #Plot results, with fake wheelbase, only showing points within 1 actual wheelbase of the path
  plot(out[,1], out[,2], t="l", xlim = c(min(out[,1], na.rm = TRUE)-3,max(out[,1],na.rm = TRUE)+3), ylim=c(min(out[,2], na.rm = TRUE)-3,max(out[,2], na.rm=TRUE)+3), xlab="X position (Feet)", ylab="Y position (Feet)", main="Equal Scrub Pose Estimation Algorithm", asp=1)
  lines(out[,3], out[,4], col="Green")
  lines(out[,5], out[,6], col="Red")
  
  return(out)
}

#A pose estimation algorithm that only uses the encoders
encoderOnlyPoseEstimation <- function(leftPos, rightPos, startingAngleDegrees, timeMillis, fakeWheelbase){
  #Convert because degrees suuuuck
  startingAngle <- deg2rad(startingAngleDegrees)
  
  wheelRadius <- fakeWheelbase/2
  
  #Set up output array
  out <- array(dim=c(length(timeMillis), 8))
  colnames(out) <- c("X","Y","leftX","leftY","rightX","rightY","angle","time")
  out[1,] <- c(0,0,wheelRadius*cos(startingAngle+pi/2), wheelRadius*sin(startingAngle+pi/2), wheelRadius*cos(startingAngle-pi/2), wheelRadius*sin(startingAngle-pi/2),startingAngle,timeMillis[1])
  
  #Loop through each logged tic, calculating pose iteratively
  for(i in 2:length(timeMillis)){

    #Directly find change in position and angle
    deltaLeft <- leftPos[i]-leftPos[i-1]
    deltaRight <- rightPos[i]-rightPos[i-1]
    
    #Average
    avgMoved <- (deltaRight+deltaLeft)/2
    
    #Points in the direction the robot is facing at the start of tic
    perpendicular <- out[i-1,7]
    
    #The angle of the sector the path is tracing
    theta <- (deltaRight - deltaLeft)/fakeWheelbase
    
    #If not turning, magnitude is just the average moved
    if(theta == 0){
      mag <- avgMoved
    } else {
      #If turning, calculate the radius and use that to get the magnitude of the turn
      mag <- 2*(avgMoved/theta)*sin(theta/2)
    }
    
    angle <- perpendicular + (theta/2)
    x <- out[i-1,1]+mag*cos(angle)
    y <- out[i-1,2]+mag*sin(angle)
    newHeading <- perpendicular + theta
    out[i,] <- c(x,y,x+wheelRadius*cos(newHeading+pi/2), y+wheelRadius*sin(newHeading+pi/2), x+wheelRadius*cos(newHeading-pi/2), y+wheelRadius*sin(newHeading-pi/2), newHeading, timeMillis[i])
  }
  
  #Plot results, with fake wheelbase
  plot(out[,1], out[,2], t="l", xlim = c(min(out[,1], out[,3], out[,5]),max(out[,1], out[,3], out[,5])), ylim=c(min(out[,2], out[,4], out[,6]),max(out[,2], out[,4], out[,6])), xlab="X position (Feet)", ylab="Y position (Feet)", main="Encoder-only Pose Estimation Algorithm",asp=1)
  lines(out[,3], out[,4], col="Green")
  lines(out[,5], out[,6], col="Red")
  
  return(out)
}

accelerometerOnlyPoseEstimation <- function(xAccel, yAccel, rawAngleDegrees, timeMillis, realWheelbase){
  #Convert because degrees suuuuck
  rawAngle <- deg2rad(rawAngleDegrees)
  
  #Set up output array
  out <- array(dim=c(length(timeMillis),9))
  colnames(out)<-c("X","Y","leftX","leftY","rightX","rightY","xVel","yVel","time")
  out[1,] <- c(0,0, realWheelbase/2*cos(rawAngle[1]+pi/2), realWheelbase/2*sin(rawAngle[1]+pi/2), realWheelbase/2*cos(rawAngle[1]-pi/2), realWheelbase/2*sin(rawAngle[1]-pi/2),0,0, timeMillis[1])
  
  #Loop through each logged tic, calculating pose iteratively
  for(i in 2:length(timeMillis)){
    #Find change in time, in seconds
    deltaTime <- (timeMillis[i] - out[i-1,9])/1000
    xVel <- out[i-1,7]+xAccel[i]*deltaTime
    yVel <- out[i-1,8]+yAccel[i]*deltaTime
    x <- out[i-1,1]+xVel*deltaTime
    y <- out[i-1,2]+yVel*deltaTime
    angle <- rawAngle[i]
    out[i,] <- c(x,y,x+realWheelbase/2*cos(angle+pi/2),y+realWheelbase/2*sin(angle+pi/2),x+realWheelbase/2*cos(angle-pi/2),y+realWheelbase/2*sin(angle-pi/2),xVel,yVel, timeMillis[i])
  }
  
  #Plot results
  plot(out[,1], out[,2], t="l", xlim = c(min(out[,1], out[,3], out[,5]),max(out[,1], out[,3], out[,5])), ylim=c(min(out[,2], out[,4], out[,6]),max(out[,2], out[,4], out[,6])), xlab="X position (Feet)", ylab="Y position (Feet)", main="Accelerometer-only Pose Estimation Algorithm",asp=1)
  lines(out[,3], out[,4], col="Green")
  lines(out[,5], out[,6], col="Red")
  return(out)
}

#Make all the graphs, for wheelbase and both algorithms, using the average effective wheelbase for encoder-only
doEverything <- function(leftPos, rightPos, rawAngleDegrees, timeMillis, angularVelThreshRad, smoothingConst){
  avg <- plotWheelVsVel(leftPos, rightPos, rawAngleDegrees, timeMillis, angularVelThreshRad, smoothingConst)
  equalScrubPoseEstimation(leftPos, rightPos, rawAngleDegrees, timeMillis, angularVelThreshRad)
  encoderOnlyPoseEstimation(leftPos=leftPos, rightPos=rightPos, startingAngleDegrees=rawAngleDegrees[1], timeMillis=timeMillis, fakeWheelbase=avg)
  return(avg)
}

animateRobot <- function(x, y, headingRadians, deltaTime, fieldFile, robotFile, robotRadius, frameSize=-1, filename="animation.mp4"){
  library("animation")
  theta <- deg2rad(headingRadians)
  saveVideo({
    for(i in 1:length(x)){
      #Set up frame
      if(frameSize == -1){
        plot(x=c(),y=c(),xlim=c(min(x)-robotRadius, max(x)+robotRadius),ylim=c(min(y)-robotRadius, max(y)+robotRadius), asp=1, xlab="X position (feet)", ylab="Y position (feet)")
      } else {
        plot(x=c(),y=c(),xlim=c(x[i]-frameSize/2, x[i]+frameSize/2),ylim=c(y[i]-frameSize/2, y[i]+frameSize/2),asp=1, xlab="X position (feet)", ylab="Y position (feet)")
      }
      plotField(fieldFile, 0, 0)
      drawRobot(robotFile, x[i],y[i],theta[i])
    }
  }, interval = deltaTime, ani.width = 600, ani.height = 600, video.name=filename)
}

tracedAnimation <- function(x, y, leftX, leftY, rightX, rightY, headingRadians, deltaTime, fieldFile, robotFile, robotRadius, frameSize=-1, filename="animation.mp4"){
  library("animation")
  theta <- deg2rad(headingRadians)
  saveVideo({
    for(i in 1:length(x)){
      #Set up frame
      if(frameSize == -1){
        plot(x=c(),y=c(),xlim=c(min(x)-robotRadius, max(x)+robotRadius),ylim=c(min(y)-robotRadius, max(y)+robotRadius), asp=1, xlab="X position (feet)", ylab="Y position (feet)")
      } else {
        plot(x=c(),y=c(),xlim=c(x[i]-frameSize/2, x[i]+frameSize/2),ylim=c(y[i]-frameSize/2, y[i]+frameSize/2),asp=1, xlab="X position (feet)", ylab="Y position (feet)")
      }
      lines(x=leftX[1:i], y=leftY[1:i], col="Green")
      lines(x=rightX[1:i], y=rightY[1:i], col="Red")
      plotField(fieldFile, 0, 0)
      drawRobot(robotFile, x[i],y[i],theta[i])
    }
  }, interval = deltaTime, ani.width = 1920, ani.height = 1080, video.name=filename)
}

#tracedAnimation(equalScrub[,1],equalScrub[,2], equalScrub[,3],equalScrub[,4],equalScrub[,5],equalScrub[,6],rel$Drive.raw_angle,0.05,"field.csv","robot.csv",27.138/12, -1, "traceAnimation.mp4")