/****************************************************************************
 *   apps/rf_sub1G/simple/main.c
 *
 * sub1G_module support code - USB version
 *
 * Copyright 2013-2014 Nathael Pajani <nathael.pajani@ed3l.fr>
 *
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *************************************************************************** */

#include "core/system.h"
#include "core/systick.h"
#include "core/pio.h"
#include "lib/stdio.h"
#include "drivers/serial.h"
#include "drivers/gpio.h"
#include "drivers/ssp.h"
#include "extdrv/cc1101.h"
#include "extdrv/status_led.h"
#include "drivers/i2c.h"

#define MODULE_VERSION	0x03
#define MODULE_NAME "RF Sub1G - USB"

#define RF_868MHz  1
#define RF_915MHz  0
#if ((RF_868MHz) + (RF_915MHz) != 1)
#error Either RF_868MHz or RF_915MHz MUST be defined.
#endif

#define DEBUG 1
#define BUFF_LEN 60
#define RF_BUFF_LEN  64

#define SELECTED_FREQ  FREQ_SEL_48MHz
#define DEVICE_ADDRESS  0x81 /* Addresses 0x00 and 0xFF are broadcast */
#define NEIGHBOR_ADDRESS 0x82 /* Address of the associated device */
#define MSG_DEMANDE_ACQ 1
#define MSG_ACQ 2
#define MSG_DEMANDE_RENVOIE 3

#define ENCRYPTION_KEY_1 0b00110001
#define ENCRYPTION_KEY_2 0b11000110

#define NB_MAX_DATA_SEND 54
#define LEN_CHECKSUM 2
#define LEN_ENTETE 8

/***************************************************************************** */
/* Pins configuration */
/* pins blocks are passed to set_pins() for pins configuration.
 * Unused pin blocks can be removed safely with the corresponding set_pins() call
 * All pins blocks may be safelly merged in a single block for single set_pins() call..
 */
const struct pio_config common_pins[] = {
	/* UART 0 */
	{ LPC_UART0_RX_PIO_0_1,  LPC_IO_DIGITAL },
	{ LPC_UART0_TX_PIO_0_2,  LPC_IO_DIGITAL },
	/* SPI */
	{ LPC_SSP0_SCLK_PIO_0_14, LPC_IO_DIGITAL },
	{ LPC_SSP0_MOSI_PIO_0_17, LPC_IO_DIGITAL },
	{ LPC_SSP0_MISO_PIO_0_16, LPC_IO_DIGITAL },
	ARRAY_LAST_PIO,
};

const struct pio cc1101_cs_pin = LPC_GPIO_0_15;
const struct pio cc1101_miso_pin = LPC_SSP0_MISO_PIO_0_16;
const struct pio cc1101_gdo0 = LPC_GPIO_0_6;
const struct pio cc1101_gdo2 = LPC_GPIO_0_7;

const struct pio button = LPC_GPIO_0_12; /* ISP button */

/***************************************************************************** */
/***************************************************************************** */

static volatile int check_rx = 0;
void rf_rx_calback(uint32_t gpio)
{
	check_rx = 1;
}

/***************************************************************************** */
/***************************************************************************** */

void system_init()
{
	/* Stop the watchdog */
	startup_watchdog_disable(); /* Do it right now, before it gets a chance to break in */
	system_set_default_power_state();
	clock_config(SELECTED_FREQ);
	set_pins(common_pins);
	gpio_on();
	/* System tick timer MUST be configured and running in order to use the sleeping
	 * functions */
	systick_timer_on(1); /* 1ms */
	systick_start();
}

/* Define our fault handler. This one is not mandatory, the dummy fault handler
 * will be used when it's not overridden here.
 * Note : The default one does a simple infinite loop. If the watchdog is deactivated
 * the system will hang.
 */
void fault_info(const char* name, uint32_t len)
{
	uprintf(UART0, name);
	while (1);
}

static uint8_t rf_specific_settings[] = {
	CC1101_REGS(gdo_config[2]), 0x07, /* GDO_0 - Assert on CRC OK | Disable temp sensor */
	CC1101_REGS(gdo_config[0]), 0x2E, /* GDO_2 - FIXME : do something usefull with it for tests */
	CC1101_REGS(pkt_ctrl[0]), 0x0F, /* Accept all sync, CRC err auto flush, Append, Addr check and Bcast */
#if (RF_915MHz == 1)
	/* FIXME : Add here a define protected list of settings for 915MHz configuration */
#endif
};

/* RF config */
void rf_config(void)
{
	config_gpio(&cc1101_gdo0, LPC_IO_MODE_PULL_UP, GPIO_DIR_IN, 0);
	cc1101_init(0, &cc1101_cs_pin, &cc1101_miso_pin); /* ssp_num, cs_pin, miso_pin */
	/* Set default config */
	cc1101_config();
	/* And change application specific settings */
	cc1101_update_config(rf_specific_settings, sizeof(rf_specific_settings));
	set_gpio_callback(rf_rx_calback, &cc1101_gdo0, EDGE_RISING);
    cc1101_set_address(DEVICE_ADDRESS);
#ifdef DEBUG
	//uprintf(UART0, "CC1101 RF link init done.\n\r");
#endif
}

/***************************************************************************** */
/***************************************************************************** */
/***************************************************************************** */

uint8_t calculation_first_byte_checksum (uint8_t* data) {
	uint8_t firstBytesChecksum = 0;
	uint8_t len_without_check_sum = data[0]-1;

	for(int i=0;i<len_without_check_sum;i++) {
		firstBytesChecksum = (firstBytesChecksum + data[i]) % 256;
	}

	return firstBytesChecksum;
}

uint8_t calculation_second_byte_checksum (uint8_t* data) {
	uint8_t secondBytesChecksum = 0;
	uint8_t len_without_check_sum = data[0]-1;	

	for(int i=0;i<len_without_check_sum;i++) {
		secondBytesChecksum = (secondBytesChecksum + (data[i] * i)) % 256;
	}

	return secondBytesChecksum;
}

/***************************************************************************** */
/***************************************************************************** */
/***************************************************************************** */

void encrypt(uint8_t* tx_data, uint8_t tx_len)
{
	for (uint8_t i = 0; i < tx_len; i++) {
		tx_data[i] ^= ENCRYPTION_KEY_1;
		tx_data[i] ^= ENCRYPTION_KEY_2;
	}
}

void decrypt(volatile uint8_t* tx_data, uint8_t tx_len)
{
	for (uint8_t i = 0; i < tx_len; i++) {
		tx_data[i] ^= ENCRYPTION_KEY_2;
		tx_data[i] ^= ENCRYPTION_KEY_1;
	}
}

/***************************************************************************** */
/***************************************************************************** */
/***************************************************************************** */

void send_acq_on_rf(uint8_t idPaquet, uint8_t idSousPaquet){
	uint8_t data[LEN_ENTETE];
	
	data[0]=LEN_ENTETE - 1;
	data[1]=NEIGHBOR_ADDRESS;
	data[2]=DEVICE_ADDRESS;
	data[3]=MSG_ACQ;//type message 
	data[4]=idPaquet;
	data[5]=idSousPaquet;

	uint8_t checksum[LEN_CHECKSUM];
	checksum[0] = calculation_first_byte_checksum(data);
	checksum[1] = calculation_second_byte_checksum(data);

	data[6]=checksum[0];
	data[7]=checksum[1];

	// Encrypt the message 
	encrypt(&data[2], data[0]-1);

	// Send 
	if (cc1101_tx_fifo_state() != 0) {
		cc1101_flush_tx_fifo();
	}
	cc1101_send_packet(data, LEN_ENTETE);
}

void handle_rf_rx_data(void)
{
	uint8_t data[RF_BUFF_LEN];
	uint8_t status = 0;

	/* Check for received packet (and get it if any) */
	int ret = cc1101_receive_packet(data, RF_BUFF_LEN, &status);

	/* Go back to RX mode */
	cc1101_enter_rx_mode();

	if(ret == 0)
	{
		rf_config();
		check_rx = 0;
		return;
	}

	/* Check that is message is addressed to us */
	if (data[1] != DEVICE_ADDRESS) {
    	cc1101_set_address(DEVICE_ADDRESS);
		return;
	}

	/* Decrypt the message */
	decrypt(&data[2], data[0] - 1);	

	uint8_t typeMsg = data[3];
	uint8_t idPaquet = data[4];
	uint8_t idSousPaquet = data[6];

	uint8_t len_msg = data[0];
	uint8_t checksum[LEN_CHECKSUM];
	checksum[0] = calculation_first_byte_checksum(data);
	checksum[1] = calculation_second_byte_checksum(data);

	if (checksum[0] == data[len_msg-1] && checksum[1] == data[len_msg]) { // si des données ont été envoyées
		if (typeMsg == MSG_DEMANDE_ACQ){
			send_acq_on_rf(idPaquet,idSousPaquet);
		}

		uint8_t len_data = data[0] - LEN_ENTETE + 1 - 2;
		uint8_t msg_data[len_data-2];
		memcpy(msg_data,&data[LEN_ENTETE],len_data);

		//TODO ne pas ecrire le message si meme id message d avant si le controlleur à renvoyer avant l acquittement 

		for(int i=0;i<len_data;i+=2){
			uprintf(UART0, "{\"device_address\":%d,\"intensity\":%d}\n",msg_data[i],msg_data[i+1]);
		}	
	}
}

/***************************************************************************** */
/***************************************************************************** */
/***************************************************************************** */

int main(void)
{
	system_init();
	uart_on(UART0, 115200, NULL);
	//i2c_on(I2C0, I2C_CLK_100KHz, I2C_MASTER);
	ssp_master_on(0, LPC_SSP_FRAME_SPI, 8, 4*1000*1000); /* bus_num, frame_type, data_width, rate */
	
	/* Radio */
	rf_config();

	#ifdef DEBUG
	uprintf(UART0, "App started\n\r");
	#endif

	while (1) {
		uint8_t status = 0;

		msleep(250);

		/* Do not leave radio in an unknown or unwated state */
		do {
			status = (cc1101_read_status() & CC1101_STATE_MASK);
		} while (status == CC1101_STATE_TX);

		
		if (status != CC1101_STATE_RX) {
			static uint8_t loop = 0;
			loop++;
			if (loop > 10) {
				if (cc1101_rx_fifo_state() != 0) {
					cc1101_flush_rx_fifo();
				}
				cc1101_enter_rx_mode();
				loop = 0;
			}
		}
		if (check_rx == 1) {
			check_rx = 0;
			handle_rf_rx_data();
		}
	}
	return 0;
}