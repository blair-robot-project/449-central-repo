#Smooths a value while taking its derivative with respect to time.
smoothDerivative <- function(value, timeMillis, n){
  smoothed <- (value[(n+1):length(value)] - value[1:(length(value)-n)])/((timeMillis[(n+1):length(timeMillis)] - timeMillis[1:(length(timeMillis)-n)])/1000);
  return(c(rep(0, ceiling(n/2)), smoothed, rep(0, floor(n/2))));
}

characterizeDriveRamp <- function(velFiles, smoothing = 3){
  combinedLeftVoltage = c()
  combinedRightVoltage = c()
  combinedLeftVel = c()
  combinedRightVel = c()
  combinedLeftAccel = c()
  combinedRightAccel = c()
  for (velFile in velFiles){
    vel <- read.csv(velFile)
    goodVel <- subset(vel, abs(left.velocity) > 0.1 & abs(left.voltage) > 0.1 & abs(right.velocity) > 0.1 & right.voltage!=0)
    goodVel <- goodVel[1:(length(goodVel$time) - 1), ]
    goodVel$left_accel <- smoothDerivative(goodVel$left.velocity, goodVel$time, smoothing)
    goodVel$right_accel <- smoothDerivative(goodVel$right.velocity, goodVel$time, smoothing)
    goodVel <- subset(goodVel, left_accel != 0 & right_accel != 0)
    plot(goodVel$left.voltage, goodVel$left.velocity)
    plot(goodVel$time, goodVel$left_accel)
    combinedLeftVoltage <- c(combinedLeftVoltage, goodVel$left.voltage)
    combinedRightVoltage <- c(combinedRightVoltage, goodVel$right.voltage)
    combinedLeftVel <- c(combinedLeftVel, goodVel$left.velocity)
    combinedRightVel <- c(combinedRightVel, goodVel$right.velocity)
    combinedLeftAccel <- c(combinedLeftAccel, goodVel$left_accel)
    combinedRightAccel <- c(combinedRightAccel, goodVel$right_accel)
  }
  leftModel <- lm(combinedLeftVoltage~combinedLeftVel+combinedLeftAccel)
  rightModel <- lm(combinedRightVoltage~combinedRightVel+combinedRightAccel)
  print(summary(leftModel))
  print(summary(rightModel))
}

characterizeDrive <- function(velFile, accelFile, smoothing = 2){
  vel <- read.csv(velFile)
  accel <- read.csv(accelFile)
  goodVel <- subset(vel, abs(left.velocity) > 0.1 & abs(left.voltage) > 0.1 & abs(right.velocity) > 0.1 & right.voltage!=0)
  goodVel <- goodVel[1:(length(goodVel$time) - 1), ]
  goodVel$left_accel <- smoothDerivative(goodVel$left.velocity, goodVel$time, smoothing)
  goodVel$right_accel <- smoothDerivative(goodVel$right.velocity, goodVel$time, smoothing)
  accel$left_accel <- smoothDerivative(accel$left.velocity, accel$time, smoothing)
  accel$right_accel <- smoothDerivative(accel$right.velocity, accel$time, smoothing)
  goodAccel <- subset(accel, left.voltage != 0 & right.voltage != 0)
  goodAccel <- goodAccel[1:(length(goodAccel$time) - 2),]
  goodAccelLeft <- goodAccel[(which.max(abs(goodAccel$left_accel))+1):length(goodAccel$time),]
  goodAccelRight <- goodAccel[(which.max(abs(goodAccel$right_accel))+1):length(goodAccel$time),]
  combinedLeftVoltage <- c(goodVel$left.voltage, goodAccelLeft$left.voltage)
  combinedRightVoltage <- c(goodVel$right.voltage, goodAccelRight$right.voltage)
  combinedLeftVel <- c(goodVel$left.velocity, goodAccelLeft$left.velocity)
  combinedRightVel <- c(goodVel$right.velocity, goodAccelRight$right.velocity)
  combinedLeftAccel <- c(goodVel$left_accel, goodAccelLeft$left_accel)
  combinedRightAccel <- c(goodVel$right_accel, goodAccelRight$right_accel)
  plot(goodAccelLeft$time, goodAccelLeft$left_accel)
  plot(goodAccelRight$time, goodAccelRight$right_accel)
  plot(goodVel$time, goodVel$right.voltage)
  plot(goodVel$right.voltage, goodVel$right.velocity)
  leftModel <- lm(combinedLeftVoltage~combinedLeftVel+combinedLeftAccel)
  rightModel <- lm(combinedRightVoltage~combinedRightVel+combinedRightAccel)
  print(summary(leftModel))
  print(summary(rightModel))
}

characterizeElevator <- function(velFile, accelFile, smoothing = 2){
  vel <- read.csv(velFile)
  accelDat <- read.csv(accelFile)
  goodVel <- subset(vel, abs(elevatorTalon.velocity) > 0.1 & elevatorTalon.voltage!=0)
  goodVel$accel <- smoothDerivative(goodVel$elevatorTalon.velocity, goodVel$time, smoothing)
  accelDat$accel <- smoothDerivative(accelDat$elevatorTalon.velocity, accelDat$time, smoothing)
  goodAccel <- subset(accelDat, elevatorTalon.voltage != 0)
  goodAccel <- goodAccel[(which.max(abs(goodAccel$accel))+1):length(goodAccel$time),]
  combinedVoltage <- c(goodVel$elevatorTalon.voltage, goodAccel$elevatorTalon.voltage)
  combinedVel <- c(goodVel$elevatorTalon.velocity, goodAccel$elevatorTalon.velocity)
  combinedAccel <- c(goodVel$accel, goodAccel$accel)
  plot(goodAccel$time, goodAccel$accel)
  plot(goodVel$time, goodVel$elevatorTalon.voltage)
  plot(goodVel$elevatorTalon.voltage, goodVel$elevatorTalon.velocity)
  model <- lm(combinedVoltage~combinedVel+combinedAccel)
  print(summary(model))
}

characterizeElevatorTrimmed <- function(vel, accelDat, smoothing = 2){
  goodVel <- subset(vel, abs(elevatorTalon.velocity) > 0.1 & elevatorTalon.voltage!=0)
  goodVel$accel <- smoothDerivative(goodVel$elevatorTalon.velocity, goodVel$time, smoothing)
  accelDat$accel <- smoothDerivative(accelDat$elevatorTalon.velocity, accelDat$time, smoothing)
  goodAccel <- subset(accelDat, elevatorTalon.voltage != 0)
  goodAccel <- subset(goodAccel, accel != 0)
  goodVel <- subset(goodVel, accel != 0)
  #goodAccel <- goodAccel[(which.max(abs(goodAccel$accel))+1):length(goodAccel$time),]
  combinedVoltage <- c(goodVel$elevatorTalon.voltage, goodAccel$elevatorTalon.voltage)
  combinedVel <- c(goodVel$elevatorTalon.velocity, goodAccel$elevatorTalon.velocity)
  combinedAccel <- c(goodVel$accel, goodAccel$accel)
  plot(goodAccel$time, goodAccel$accel)
  plot(goodVel$time, goodVel$elevatorTalon.voltage)
  plot(goodVel$elevatorTalon.voltage, goodVel$elevatorTalon.velocity)
  model <- lm(combinedVoltage~combinedVel+combinedAccel)
  print(summary(model))
}

characterizeArm <- function(velFile, accelFile, smoothing = 2, velOffset = 0, accelOffset = 0){
  vel <- read.csv(velFile)
  accel <- read.csv(accelFile)
  goodVel <- subset(vel, abs(arm.velocity) > 0.03 & abs(arm.voltage) > 0.1)
  goodVel <- goodVel[1:(length(goodVel$time) - 1), ]
  goodVel$left_accel <- smoothDerivative(goodVel$arm.velocity, goodVel$time, smoothing)
  accel$left_accel <- smoothDerivative(accel$arm.velocity, accel$time, smoothing)
  goodAccel <- subset(accel, arm.voltage != 0)
  goodAccel <- goodAccel[1:(length(goodAccel$time) - 2),]
  goodAccelLeft <- goodAccel[(which.max(abs(goodAccel$left_accel))+1):length(goodAccel$time),]
  combinedLeftVoltage <- c(goodVel$arm.voltage, goodAccelLeft$arm.voltage)
  combinedLeftVel <- c(goodVel$arm.velocity, goodAccelLeft$arm.velocity)
  combinedLeftAccel <- c(goodVel$left_accel, goodAccelLeft$left_accel)
  plot(goodAccelLeft$time, goodAccelLeft$left_accel)
  plot(goodVel$time, goodVel$arm.voltage)
  plot(goodVel$arm.voltage, goodVel$arm.velocity)
  angle <- c(goodVel$arm.position + velOffset, goodAccelLeft$arm.position + accelOffset)
  angleFactor <- cos(angle*2*pi)
  combinedLeftAccel <- combinedLeftAccel*angleFactor
  leftModel <- lm(combinedLeftVoltage~combinedLeftVel+angleFactor)
  print(summary(leftModel))
}