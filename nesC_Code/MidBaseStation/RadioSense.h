
#ifndef RADIO_SENSE_H
#define RADIO_SENSE_H

enum {
  AM_RADIO_SENSE_MSG = 7,
  RADIO_QUEUE_LEN = 16,
  NUM_COMBINE_PACKET = 3,
};

typedef nx_struct radio_sense_msg {
  nx_uint16_t id;
  nx_uint16_t temp;
  nx_uint16_t humi;
  nx_uint16_t light;
} radio_sense_msg_t;

typedef nx_struct radio_combine_msg {
  radio_sense_msg_t combine_msg[NUM_COMBINE_PACKET];
} radio_combine_msg_t;






#endif
