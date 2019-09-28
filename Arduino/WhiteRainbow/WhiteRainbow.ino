#include <Adafruit_NeoPixel.h>
#define PIN 6
Adafruit_NeoPixel strip = Adafruit_NeoPixel(150, PIN, NEO_GRB + NEO_KHZ800);

//Amount of change for each of the colors at each step
const int rgbChangeSpeed = 18;

//highest value a color would reach.
const int colorMax = 108;
int red = colorMax;
int green = 0;
int blue = 0;

//information about the white pixel bit that flows around
int currentPos = 0;
const int whiteLength = 7;
const int whiteSpeed = 2;

int tempIndex;

//last position position that the white pixel bit was at.
int lastPos = 150;
//previous color that gets overwritten by the white pixel bit.
uint32_t lastColor;

void setup() {
  // put your setup code here, to run once:
  strip.begin();
  strip.show();
}

void loop() {
  //if you've completed a cycle (ie current position is now 0, last position is 150)
  if (currentPos < lastPos || currentPos > 149) {
    //store lastColor, then find your next incremented color
    lastColor = strip.Color(red,green,blue);
    nextRGB();
  }
  lastPos = currentPos;
  tempIndex = currentPos;
  //sets the little white pixel bit to white.
  while (tempIndex != (currentPos + whiteLength) % 150) {
    strip.setPixelColor(tempIndex,100,100,100);
    tempIndex = (tempIndex + 1) % 150;
  }
  //if you're behind the white thing, get set to the new color
  for (int i = 0; i < currentPos; i++) {
    strip.setPixelColor(i,red,green,blue);
  }
  //if you're beyond the white thing, get set to the old color.
  for (int i = currentPos + whiteLength + 1; i < 150; i++) {
    strip.setPixelColor(i,lastColor);
  }
  //update strip, move white pixel bit forward by speed defined above
  strip.show();
  currentPos += whiteSpeed;
  currentPos %= 150;
  delay(18);
}

//generates the next color based on the graph: https://academe.co.uk/wp-content/uploads/2012/04/451px-HSV-RGB-comparison.svg_.png
void nextRGB() {
  if (red == colorMax && green < colorMax && blue == 0) {
    green += rgbChangeSpeed;
  } else if (green == colorMax && red > 0 && blue == 0) {
    red -= rgbChangeSpeed * 1.5;
  } else if (green == colorMax && blue < colorMax && red == 0) {
    blue += rgbChangeSpeed;
  } else if (blue == colorMax && green > 0 && red == 0) {
    green -= rgbChangeSpeed;
  } else if (blue == colorMax && red < colorMax && green == 0) {
    red += rgbChangeSpeed;
  } else if (red == colorMax && blue > 0 && green == 0) {
    blue -= rgbChangeSpeed * .5;
  }
}
