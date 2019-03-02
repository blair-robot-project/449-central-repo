#Simple helper methods
rad2deg <- function(rad) {return((rad * 180) / (pi))}
deg2rad <- function(deg) {return((deg * pi) / (180))}
distance <- function(x1, y1, x2, y2){return(sqrt((x1-x2)^2 + (y1-y2)^2))}
leftKVFwd <- 1.433226
leftKVRev <- 1.408763
leftKA <- 0.165688
leftInterceptFwd <- 0.960891
leftInterceptRev <- 1.120689
rightKVFwd <- 1.460269
rightKVRev <- 1.421145
rightKA <- 0.224505
rightInterceptFwd <- 0.959980
rightInterceptRev <- 1.120998


plotProfile <- function(profileName, leftInverted = FALSE, rightInverted=FALSE, wheelbaseDiameter, centerToBack, startY = 0, startPos = c(-1,-1,-1,-1,-1,-1), usePosition = TRUE){
  if (leftInverted & rightInverted){
    left <- read.csv(paste("../../449-central-repo/robot2019Left",profileName,"Profile.csv",sep=""), header=FALSE)
    right <- read.csv(paste("../../449-central-repo/robot2019Right",profileName,"Profile.csv",sep=""), header=FALSE)
  } else {
    left <- read.csv(paste("../../449-central-repo/robot2019Left",profileName,"Profile.csv",sep=""), header=FALSE)
    right <- read.csv(paste("../../449-central-repo/robot2019Right",profileName,"Profile.csv",sep=""), header=FALSE)
  }
  startingCenter <- c(startY, centerToBack)
  left$V1[1] <- 0
  left$V2[1] <- 0
  left$V4[1] <- left$V4[2]
  right$V1[1] <- 0
  right$V2[1] <- 0
  right$V4[1] <- right$V4[2]
  #Position,Velocity,Accel,Delta t, Elapsed time
  left$V5 <- (0:(length(left$V1)-1))*left$V4[1]
  right$V5 <- (0:(length(right$V1)-1))*right$V4[1]
  #Time, Left X, Left Y, Right X, Right Y, Angle
  out <- array(dim=c(length(left$V1),6))
  if(identical(startPos, c(-1,-1,-1,-1,-1,-1))){
    out[1,]<-c(0, startingCenter[2], (startingCenter[1]+wheelbaseDiameter/2.), startingCenter[2], (startingCenter[1]-wheelbaseDiameter/2.), 0)
  } else {
    out[1,]<-startPos
  }
  
  for(i in 2:length(left$V5)){
    #Get the angle the robot is facing.
    perpendicular <- out[i-1,6]
    
    #Add the change in time
    out[i,1] <- out[i-1,1]+left$V4[i]
    
    #Figure out linear change for each side using position or velocity
    if (usePosition){
      deltaLeft <- left$V1[i] - left$V1[i-1]
      deltaRight <- right$V1[i] - right$V1[i-1]
    } else {
      deltaLeft <- left$V2[i]*left$V4[i]
      deltaRight <- right$V2[i]*left$V4[i]
    }
    
    # Invert the change if nessecary
    if (leftInverted){
      # print(paste("L:",left$V2[i]*leftKVRev+left$V3[i]*leftKA+leftInterceptRev))
      if (left$V2[i]*leftKVRev+left$V3[i]*leftKA+leftInterceptRev > 12){
        print("Over 12v!")
      }
      deltaLeft <- -deltaLeft
    } else {
      # print(paste("L:",left$V2[i]*leftKVFwd+left$V3[i]*leftKA+leftInterceptFwd))
      if (left$V2[i]*leftKVFwd+left$V3[i]*leftKA+leftInterceptFwd > 12){
        print("Over 12v!")
      }
    }
    
    if (rightInverted){
      # print(paste("R:",right$V2[i]*rightKVRev+right$V3[i]*rightKA+rightInterceptRev))
      if (right$V2[i]*rightKVRev+right$V3[i]*rightKA+rightInterceptRev > 12){
        print("Over 12v!")
      }
      deltaRight <- -deltaRight
    } else {
      # print(paste("R:",right$V2[i]*rightKVFwd+right$V3[i]*rightKA+rightInterceptFwd))
      if (right$V2[i]*rightKVFwd+right$V3[i]*rightKA+rightInterceptFwd > 12){
        print("Over 12v!")
      }
    }
    
    diffTerm <- deltaRight - deltaLeft
    #So in this next part, we figure out the turning center of the robot
    #and the angle it turns around that center. Note that the turning center is
    #often outside of the robot.
    
    #Calculate how much we turn first, because if theta = 0, turning center is infinitely far away and can't be calcualted.
    theta <- diffTerm/wheelbaseDiameter
    
    out[i,6] <- out[i-1,6]+theta
    
    # If theta is 0, we're going straight and need to treat it as a special case.
    if (identical(theta, 0)){
      out[i, 2] <- out[i-1,2]+deltaLeft*cos(perpendicular)
      out[i, 3] <- out[i-1,3]+deltaLeft*sin(perpendicular)
      out[i, 4] <- out[i-1,4]+deltaRight*cos(perpendicular)
      out[i, 5] <- out[i-1,5]+deltaRight*sin(perpendicular)
    } else {
      
      #We do this with sectors, so this is the radius of the turning circle for the
      #left and right sides. They just differ by the diameter of the wheelbase.
      rightR <- (wheelbaseDiameter/2) * (deltaRight + deltaLeft) / diffTerm + wheelbaseDiameter/2
      leftR <- rightR - wheelbaseDiameter
      
      #This is the angle for the vector pointing towards the new position of each
      #wheel.
      #To understand why this formula is correct, overlay isoclese triangles on the sectors
      vectorTheta <- (out[i-1,6]+out[i,6])/2
      
      #The is the length of the vector pointing towards the new position of each
      #wheel divided by the radius of the turning circle.
      vectorDistanceWithoutR <- 2*sin(theta/2)
      
      out[i, 2] <- out[i-1,2]+vectorDistanceWithoutR*leftR*cos(vectorTheta)
      out[i, 3] <- out[i-1,3]+vectorDistanceWithoutR*leftR*sin(vectorTheta)
      out[i, 4] <- out[i-1,4]+vectorDistanceWithoutR*rightR*cos(vectorTheta)
      out[i, 5] <- out[i-1,5]+vectorDistanceWithoutR*rightR*sin(vectorTheta)
    }
  }
  return(out)
}

drawProfile <- function (coords, centerToBack, wheelbaseDiameter, clear=TRUE, linePlot = TRUE){
  if (clear){
    if (linePlot){
      plot(coords[,2],coords[,3], type="l", col="Green", ylim=c(-16, 16),xlim = c(0,54), xlab = "X Position (feet)", ylab="Y Position (feet)", asp=1)
    } else {
      plot(coords[,2],coords[,3], col="Green", ylim=c(-16, 16), xlim = c(0,54), xlab = "X Position (feet)", ylab="Y Position (feet)", asp=1)
    }
    plotField("deepSpaceField.csv")
  } else {
    if (linePlot){
      lines(coords[,2],coords[,3],col="Green")
    } else {
      points(coords[,2],coords[,3],col="Green")
    }
  }
  if (linePlot){
    lines(coords[,4],coords[,5],col="Red")
  } else {
    points(coords[,4],coords[,5],col="Red")
  }
}

drawRobot <- function(robotFile, x, y, theta, robotCircleFile=NA){
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
  
  if(!is.na(robotCircleFile)){
    library("plotrix")
    circles <- read.csv(robotCircleFile)
    centers <- rotMatrix %*% matrix(c(circles$x, circles$y), nrow = 2, ncol = length(circles$x), byrow = TRUE)
    centers <- centers + c(robotCenter[1], robotCenter[2])
    for (i in 1:length(circles$x)) {
      draw.circle(x=centers[1,i], y=centers[2, i], radius = circles$radius[i], col="Green")
    }
  }
}

plotField <- function(filename, xOffset=0, yOffset=0){
  field <- read.csv(filename)
  #Strings are read as factors by default, so we need to do this to make it read them as strings
  field$col <- as.character(field$col)
  for (i in 1:length(field$x1)){
    lines(c(field$x1[i]+xOffset, field$x2[i]+xOffset), c(field$y1[i]+yOffset, field$y2[i]+yOffset), col=field$col[i])
  }
}

tracedAnimation <- function(x, y, leftX, leftY, rightX, rightY, headingRadians, deltaTime, fieldFile, robotFile, robotRadius, frameSize=-1, filename="animation.mp4", robotCircleFile=NA){
  library("animation")
  theta <- headingRadians
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
      drawRobot(robotFile, x[i],y[i],theta[i],robotCircleFile = robotCircleFile)
    }
  }, interval = deltaTime, ani.width = 1920, ani.height = 1080, video.name=filename)
}

executeProfileSequence <- function(names, leftInverted, rightInverted, wheelbaseDiameter, centerToBack, startY, robotFile, intakeFile = NA){
  out <- plotProfile(names[1], leftInverted = leftInverted[1], rightInverted = rightInverted[1], wheelbaseDiameter=wheelbaseDiameter, centerToBack=centerToBack, startY = startY,  usePosition = TRUE)
  totalOut <- out
  drawProfile(out, centerToBack = centerToBack, wheelbaseDiameter = wheelbaseDiameter, clear = TRUE, linePlot = TRUE)
  tmp <- out[length(out[,1]),]
  drawRobot(robotFile, x=(tmp[2]+tmp[4])/2, y= (tmp[3]+tmp[5])/2, theta = tmp[6], intakeFile)
  if(length(names) > 1){
    for(i in 2:length(names)){
      print(paste("Starting profile",i))
      out <- plotProfile(names[i], leftInverted = leftInverted[i], rightInverted = rightInverted[i], wheelbaseDiameter=wheelbaseDiameter, centerToBack=centerToBack, startPos = tmp,  usePosition = TRUE)
      totalOut <- rbind(totalOut, out)
      drawProfile(out, centerToBack = centerToBack, wheelbaseDiameter = wheelbaseDiameter, clear = FALSE, linePlot = TRUE)
      tmp <- out[length(out[,1]),]
      drawRobot(robotFile, x=(tmp[2]+tmp[4])/2, y= (tmp[3]+tmp[5])/2, theta = tmp[6], intakeFile)
    }
  }
  #Time, Left X, Left Y, Right X, Right Y, Angle
  print(paste("X:",(tmp[2]+tmp[4])/2,"Y:",(tmp[3]+tmp[5])/2))
  print(rad2deg(tmp[6]))
  return(totalOut)
}

wheelbaseDiameter <- 2.14
centerToBack <- (39.5/2.)/12.
centerToSide <- (24.5)/12.
#Time, Left X, Left Y, Right X, Right Y, Angle
drawProfile(plotProfile("LoadToFR", leftInverted = FALSE, rightInverted = FALSE, startPos = c(0, 4 + centerToBack, -0+wheelbaseDiameter/2.,  4 + centerToBack, -0-wheelbaseDiameter/2., 0), wheelbaseDiameter=wheelbaseDiameter, centerToBack=centerToBack), wheelbaseDiameter=wheelbaseDiameter, centerToBack=centerToBack)