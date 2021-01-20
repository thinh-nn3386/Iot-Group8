configuration BaseStationC {
}
implementation {
  components MainC, BaseStationP, LedsC;
  components ActiveMessageC as Radio;

  components new SensirionSht11C() as TempSensor;
  components new SensirionSht11C() as HumiSensor;
  components new HamamatsuS1087ParC() as LightSensor;

  components new TimerMilliC() as Timer1;
  components new TimerMilliC() as Timer2;

  MainC.Boot <- BaseStationP;

  BaseStationP.RadioControl -> Radio;
 
  BaseStationP.RadioSend -> Radio;
  BaseStationP.RadioReceive -> Radio.Receive;
  BaseStationP.RadioSnoop -> Radio.Snoop;
  BaseStationP.RadioPacket -> Radio;
  BaseStationP.RadioAMPacket -> Radio;
  
  BaseStationP.Leds -> LedsC;
  BaseStationP.MilliTimer -> Timer1;
  BaseStationP.PeriodSendTimer -> Timer2;
  
  BaseStationP.TempSensor -> TempSensor.Temperature;
  BaseStationP.HumiSensor -> HumiSensor.Humidity;
  BaseStationP.LightSensor -> LightSensor;
}
