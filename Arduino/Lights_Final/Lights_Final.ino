/* Team 449 2017 LED Strip Code
 * Written by Rafi Pedersen
 * 
 * Operates under a 4-State premise:
 * State 1: Neither gear nor peg
 * State 2: Gear only
 * State 3: Peg Only (pretty much useless)
 * State 4: Peg and Gear
 * 
 * Each state has two colors, which alternately expand from
 * the middle of the strip.
 * 
 * Detects the peg using six analog IR sensors
 * Detects the gear using two digital IR sensors, one for gear
 * in and one for gear out.
 * 
 * Uses a 150 pixel Adafruit Neopixel strip.
 */

#include <Wire.h>
#include <Adafruit_NeoPixel.h>

#define PIN 6
Adafruit_NeoPixel strip = Adafruit_NeoPixel(150, PIN, NEO_GRB + NEO_KHZ800);

//define a bunch of colors
uint32_t red = strip.Color(60,0,0);
uint32_t green = strip.Color(0,100,15);
uint32_t oyellow = strip.Color(140,40,0);
uint32_t blue = strip.Color(0,0,100);
uint32_t orange = strip.Color(140,10,0);
uint32_t yellow = strip.Color(90,20,0);
uint32_t purple = strip.Color(55,0,75);
uint32_t pink = strip.Color(80,0,65);
uint32_t lgreen = strip.Color(10,80,10);
uint32_t lblue = strip.Color(15,35,135);
uint32_t cyan = strip.Color(0,80,60);
uint32_t white = strip.Color(40,40,40);

//define 2 colors for each of the four states.
uint32_t neutral1 = red;
uint32_t neutral2 = orange;
uint32_t gear1 = yellow;
uint32_t gear2 = oyellow;
uint32_t peg1 = purple;
uint32_t peg2 = pink;
uint32_t gearPeg1 = blue;
uint32_t gearPeg2 = lblue;


int threshold = 320; //threshold for the analog peg-detecting sensors
int gearSensorBuffer = 15; //buffer for the gear sensors
                           //higher values make less flicker, more delay
int gearBufferState1 = 0; //initialize both sensors' buffers
int gearBufferState2 = 0;
//buffers for the gear sensors add or subtract based on their reading
//then state is determined by positive/negative of buffers

//used to make transition animation between two colors
int barrier = 0;
int ripplePos = 0;
boolean rippleInvert = false;

//set up analog sensors
const int sensorCount = 6; //number of sensors
const int analogInPins[] = {A0, A1, A2, A3, A4, A5};
uint32_t sensorSums[sensorCount];

const int sensorReadingCount = 20;
uint32_t pegSensorReadings[sensorCount][sensorReadingCount];
int sensorIndex = 0;

boolean pegIn = false;
boolean gearIn = false;

int loopCounter = 0;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  strip.begin();
  strip.show();
}

void loop() {
  // put your main code here, to run repeatedly:
  readSensors();

  if (loopCounter % 2 == 0) {
    ripplePos++;
    loopCounter = 1;
  } else {
    loopCounter++;
  }

  if (ripplePos > strip.numPixels() / 2) {
    ripplePos = ripplePos % (strip.numPixels()/2);
    rippleInvert = !rippleInvert;
  }
  
  if (gearIn) {
    if (pegIn) {
      for (int i = 0; i <= strip.numPixels()/2; i++) {
        setColor(strip.numPixels()/2+i, gearPeg1, gearPeg2);
        setColor(strip.numPixels()/2 - i, gearPeg1, gearPeg2);
      }
    } else {
      for (int i = 0; i <= strip.numPixels()/2; i++) {
        setColor(strip.numPixels()/2+i, gear1, gear2);
        setColor(strip.numPixels()/2 - i, gear1, gear2);
      }
    }
  } else if (pegIn) {
    for (int i = 0; i <= strip.numPixels()/2; i++) {
      setColor(strip.numPixels()/2+i, peg1, peg2);
      setColor(strip.numPixels()/2 - i, peg1, peg2);
    }
  } else {
    for (int i = 0; i <= strip.numPixels()/2; i++) {
      setColor(strip.numPixels()/2+i, neutral1, neutral2);
      setColor(strip.numPixels()/2-i, neutral1, neutral2);
    }
  }
  strip.show();
  delay(1);
}

void setColor(int i, uint32_t c1, uint32_t c2) {
  if (!rippleInvert) {
    if (i < strip.numPixels()/2 - ripplePos || i > strip.numPixels()/2+ripplePos) {
      strip.setPixelColor(i,c2);
    } else if (i == strip.numPixels()/2 - ripplePos || i == strip.numPixels()/2+ripplePos){
      strip.setPixelColor(i,90,90,90);
    } else {
      strip.setPixelColor(i,c1);
    }
  } else {
    if (i < strip.numPixels()/2 - ripplePos || i > strip.numPixels()/2+ripplePos) {
      strip.setPixelColor(i,c1);
    } else if (i == strip.numPixels()/2 - ripplePos || i == strip.numPixels()/2+ripplePos) {
      strip.setPixelColor(i,90,90,90);
    }
    else {
      strip.setPixelColor(i,c2);
    }
  }
}

void readSensors() {
  if (digitalRead(2) == 0) {
    if (gearBufferState1 < gearSensorBuffer) {
      gearBufferState1++;
    }
  } else {
    if (gearBufferState1 > gearSensorBuffer * -1) {
      gearBufferState1--;
    }
  }
  if (digitalRead(3) == 0) {
    if (gearBufferState2 < gearSensorBuffer) {
      gearBufferState2++;
    }
  } else {
    if (gearBufferState2 > gearSensorBuffer * -1) {
      gearBufferState2--;
    }
  }
  if (gearBufferState2 >= 0) {
    gearIn = true;
  } else if (gearBufferState1 < 0 && gearBufferState2 < 0) {
    gearIn = false;
  }
  
  for (int i = 0; i < sensorCount; i++) {
    pegSensorReadings[i][sensorIndex] = analogRead(analogInPins[i]);
    //Serial.print(pegSensorReadings[i][sensorIndex]); Serial.print('\t');
    sensorSums[i] = findAverage(pegSensorReadings[i]);
  }
  sensorIndex++;
  if (sensorIndex == sensorReadingCount) {
      sensorIndex = 0;
    }
  //sensorSums[1] *= 2.5;
  for (int i = 0; i < sensorCount; i++) {
    Serial.print(sensorSums[i]); Serial.print('\t');
  }
  Serial.println();
  if (maxValue(sensorSums, sensorCount) > threshold) {
    /*if(redColor < 100){
      redColor+=15;
    }
    if (blueColor > 0) {
      blueColor -= 15;
    }*/
    pegIn = true;
  } else {
    pegIn = false;
  }
}

uint32_t maxValue( uint32_t myArray[], int size) {
    int i; uint32_t maxValue;
    maxValue=myArray[0];

    //find the largest no
    for (i=0;i < size; i++) {
        if (myArray[i]>maxValue)
        maxValue=myArray[i];
    }   
    return maxValue;
}

uint32_t findAverage(uint32_t input[]) {
  uint32_t sum = 0;
  for (int i = 0; i < sensorReadingCount; i++) {
    sum += input[i];
  }
  return (sum / sensorReadingCount);
}
