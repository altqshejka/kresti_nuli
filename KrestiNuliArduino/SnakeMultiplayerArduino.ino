#define axis_X_1 0  //X-координаты 1 джойстика
#define axis_Y_1 1  //Y-координаты 2 джойстика
#define axis_X_2 2  //X-координаты 1 джойстика
#define axis_Y_2 3  //Y-координаты 2 Джойстика
#define axis_Z_1 6  //Z-координаты 1 Джойстика
#define axis_Z_2 5  //Z-координаты 2 Джойстика

int X1;
int Y1;
int X2;
int Y2;
int Z1;
int Z2;
String output;

void setup() {
  Serial.begin(9600);  
  pinMode(axis_Z_1, INPUT);
  pinMode(axis_Z_2, INPUT);
}

void loop() {
  
  delay(10);

  output = "";
  
  X1 = analogRead(axis_X_1);
  Y1 = analogRead(axis_Y_1);
  X2 = analogRead(axis_X_2);
  Y2 = analogRead(axis_Y_2);
  Z1= digitalRead(axis_Z_1);
  Z2= digitalRead(axis_Z_2);
  /*
   * 0 - нет
   * 1 - верх
   * 2 - низ
   * 3 - право
   * 4 - лево
   */

  if (Y1>600 && X1<600 && X1>200) output += 1;
  else if (Y1<200 && X1<600 && X1>200) output += 2;
  else if (X1>600 && Y1<600 && Y1>200) output += 3;
  else if (X1<200 && Y1<600 && Y1>200) output += 4;
  if (Z1==0) output+=5;
  else output += 0;

  if (Y2>600 && X2<600 && X2>200) output += 1;
  else if (Y2<200 && X2<600 && X2>200) output += 2;
  else if (X2>600 && Y2<600 && Y2>200) output += 3;
  else if (X2<200 && Y2<600 && Y2>200) output += 4;
  else if (Z2==0) output+=5;
  else output += 0;

  Serial.println(output);
  delay(100);
}
