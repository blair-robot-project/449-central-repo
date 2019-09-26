# Title     : findVelVsVoltage
# Objective : Find the line or curve describing how
# Created by: Noah Gleason
# Created on: 9/30/17

trials = 1
voltages = seq(1.5,5,by=0.5)
#voltages = c(0.6, 2, 2.5)
raw <- read.csv("eventLog-2017.10.01.13.58.02.csv")
raw$class <- as.character(raw$class)
maxes <- raw[grep("class org.usfirst.frc.team449.robot.drive.unidirectional.commands.DetermineVelVsVoltage", raw$class), 3]
maxes <- as.numeric(as.vector(maxes))

model <- lm(maxes ~ voltages)
coeffs <- c(coefficients(model))
View(coeffs)
xInt <- -coeffs[1]/coeffs[2]

res <- resid(model)

plot(x=voltages, y=maxes, main = "Max Velocity vs. Voltage", xlab="Voltage (volts)", ylab="Max speed (feet/sec)", xlim=c(0,12), ylim=c(0,12))
abline(model)
plot(x=voltages, y=res, main="Residuals", xlab="Voltage (volts)", ylab="Residuals (feet/sec)", xlim=c(0,12))
abline(0,0)