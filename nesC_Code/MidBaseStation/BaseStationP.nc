
#include "AM.h"
#include "RadioSense.h"
module BaseStationP @safe() {
  uses {
    interface Boot;
    interface SplitControl as RadioControl;
    interface AMSend as RadioSend[am_id_t id];
    interface Receive as RadioReceive[am_id_t id];
    interface Receive as RadioSnoop[am_id_t id];
    interface Packet as RadioPacket;
    interface AMPacket as RadioAMPacket;
    interface Timer<TMilli> as MilliTimer;
    interface Timer<TMilli> as PeriodSendTimer;

    interface Read<uint16_t> as HumiSensor;
    interface Read<uint16_t> as TempSensor;
    interface Read<uint16_t> as LightSensor;

    interface Leds;
  }
}

implementation
{
  
  message_t  radioQueueBufs[RADIO_QUEUE_LEN];
  message_t  * ONE_NOK radioQueue[RADIO_QUEUE_LEN];
  uint8_t    radioIn, radioOut;
  bool       radioBusy, radioFull;

  message_t packet;
  message_t thisSens;
  uint8_t count = 0; //number of packet in queue

  uint16_t ID = 0x0000;
  uint16_t Temp;
  uint16_t Humi;
  uint16_t Light;

  task void addMsgToQueue();
  task void radioSendTask();
  task void radioPeriodSendTask();

  void dropBlink() {
    call Leds.led2Toggle();
  }

  void failBlink() {
    call Leds.led2Toggle();
  }

  event void Boot.booted() {
    uint8_t i;

    for (i = 0; i < RADIO_QUEUE_LEN; i++)
      radioQueue[i] = &radioQueueBufs[i];
    radioIn = radioOut = 0;
    radioBusy = FALSE;
    radioFull = TRUE;

    if (call RadioControl.start() == EALREADY)
      radioFull = FALSE;
    call MilliTimer.startPeriodic(10000);
    call PeriodSendTimer.startPeriodic(20000);

  }

  event void RadioControl.startDone(error_t error) {
    if (error == SUCCESS) {
      radioFull = FALSE;
    }
  }

 
  event void RadioControl.stopDone(error_t error) {}
 
  event void PeriodSendTimer.fired(){
    if (!radioBusy)
    {
	post radioPeriodSendTask();
	radioBusy = TRUE;
    }
  
  }

  event void MilliTimer.fired() {

    if ( call TempSensor.read() == SUCCESS
		&& call LightSensor.read() == SUCCESS
			&& call HumiSensor.read() == SUCCESS)
    {
      call Leds.led1Toggle();
    }
    else 
      call Leds.led2Toggle();
  }


  event void TempSensor.readDone(error_t result, uint16_t val)
  {
    if (result == SUCCESS)
    {
      Temp = val;                  		
    }
  }

  event void LightSensor.readDone(error_t result, uint16_t val)
  {
    if (result == SUCCESS)
    {
      Light = val;                  		
    }
  }
	
  event void HumiSensor.readDone(error_t result, uint16_t val)
  {
    if (result == SUCCESS)
    {
      Humi = val; 
      post addMsgToQueue(); 
    }
  }

  message_t* ONE receive(message_t* ONE msg, void* payload, uint8_t len);
  
  event message_t *RadioSnoop.receive[am_id_t id](message_t *msg,
						    void *payload,
							    uint8_t len) {
    return receive(msg, payload, len);

  }
  
  event message_t *RadioReceive.receive[am_id_t id](message_t *msg,
						    void *payload,
						    uint8_t len) {
    return receive(msg, payload, len);
  }
  
  task void addMsgToQueue(){
    
    radio_sense_msg_t* rsm;

      rsm = (radio_sense_msg_t*)call RadioPacket.getPayload(&thisSens, sizeof(radio_sense_msg_t));
      if (rsm == NULL) {
	return;
      }
      rsm->id = ID;
      rsm->temp = Temp;
      rsm->humi = Humi;
      rsm->light = Light;
    receive(&thisSens, rsm, sizeof(radio_sense_msg_t));
  }

  message_t* receive(message_t *msg, void *payload, uint8_t len) {
    message_t *ret;

    atomic
      if (!radioFull)
	    {
        ret = radioQueue[radioIn];
        radioQueue[radioIn] = msg;
        count++;
        if (++radioIn >= RADIO_QUEUE_LEN)
          radioIn = 0;
        if (radioIn == radioOut)
          radioFull = TRUE;

        if (!radioBusy && count >= NUM_COMBINE_PACKET)
          {
            post radioSendTask();
            radioBusy = TRUE;
          }
	    }
      else
	      dropBlink();
    return ret;
  }
  
  task void radioSendTask() {
    uint8_t i;
    message_t* msg;
    radio_combine_msg_t* payload;
    radio_sense_msg_t * rsm[NUM_COMBINE_PACKET];
    atomic
      if (count < NUM_COMBINE_PACKET || (radioIn == radioOut && !radioFull))
	{
	  radioBusy = FALSE;
	  return;
	}
    
    
    payload = (radio_combine_msg_t*)call RadioPacket.getPayload(&packet, sizeof(radio_combine_msg_t));
      if (payload == NULL) {
	    return;
    }	
    
    for(i = 0; i < NUM_COMBINE_PACKET; i++){
	   uint8_t index = radioOut + i;
	   if (index >= RADIO_QUEUE_LEN) index = index - RADIO_QUEUE_LEN;
	   msg = radioQueue[index];

    //  radioQueue[index] = NULL;

	   rsm[i] = (radio_sense_msg_t*) call RadioPacket.getPayload(msg,sizeof(radio_sense_msg_t));
           payload->combine_msg[i] = *rsm[i];
	} 
     
    
    
    if (call RadioSend.send[AM_RADIO_SENSE_MSG](0x0010, &packet, sizeof(radio_combine_msg_t)) == SUCCESS)
      call Leds.led0Toggle();
    else
      {
	failBlink();
	post radioSendTask();
      }
  
  }
  task void radioPeriodSendTask(){
     uint8_t i;
    message_t* msg;
    radio_combine_msg_t* payload;
    radio_sense_msg_t * rsm[count];
    
    atomic
      if ((radioIn == radioOut && !radioFull)|| count == 0)
	{
	  radioBusy = FALSE;
	  return;
	}
      if (count >= NUM_COMBINE_PACKET){
	  post radioSendTask();
          radioBusy = FALSE;
          return;
      } 
//256
    payload = (void*)call RadioPacket.getPayload(&packet,count*sizeof(radio_sense_msg_t));
      if (payload == NULL) {
	return;
      }	
    //261
    for(i = 0; i < NUM_COMBINE_PACKET; i++){
	if(i < count) {
	   uint8_t index = radioOut + i;
	   if (index >= RADIO_QUEUE_LEN) index = index - RADIO_QUEUE_LEN;
	   msg = radioQueue[index];
	   rsm[i] = (radio_sense_msg_t*) call RadioPacket.getPayload(msg,sizeof(radio_sense_msg_t));
           payload->combine_msg[i] = *rsm[i];
	} 
	
    }
     
    if (call RadioSend.send[AM_RADIO_SENSE_MSG](0x0010, &packet, sizeof(radio_combine_msg_t)) == SUCCESS)
      call Leds.led0Toggle();
    else
      {
	failBlink();
	post radioSendTask();
      }

  }
  event void RadioSend.sendDone[am_id_t id](message_t* msg, error_t error) {
    if (error != SUCCESS)
      failBlink();
    else
	// if (&packet == msg)
	//   { 
      if (count >= NUM_COMBINE_PACKET){
		    count -= NUM_COMBINE_PACKET;
		    radioOut = (radioOut+NUM_COMBINE_PACKET) % RADIO_QUEUE_LEN;
		    if (radioFull)
		      radioFull = FALSE;
      } else {
        
          radioOut += count;
          count = 0;
      }
    // }
    post radioSendTask();
  }

  
}  


