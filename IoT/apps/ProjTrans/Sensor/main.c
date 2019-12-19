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

#define MODULE_VERSION	0x03
#define MODULE_NAME "RF Sub1G - USB"

#define RF_868MHz  1
#define RF_915MHz  0
#if ((RF_868MHz) + (RF_915MHz) != 1)
#error Either RF_868MHz or RF_915MHz MUST be defined.
#endif

#define DEBUG 1
#define BUFF_LEN 60
#define RF_BUFF_LEN 64

#define SELECTED_FREQ  FREQ_SEL_48MHz
#define DEVICE_ADDRESS  0x82 /* Addresses 0x00 and 0xFF are broadcast */
#define GATEWAY_ADDRESS 0x81 /* Address of the associated device */

#define ENCRYPTION_KEY_1 0b00110001
#define ENCRYPTION_KEY_2 0b11000110

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
    cc1101_set_address(DEVICE_ADDRESS);
#ifdef DEBUG
	uprintf(UART0, "CC1101 RF link init done.\n\r");
#endif
}

/* Data sent on radio comes from the UART, put any data received from UART in
 * cc_tx_buff and send when either '\r' or '\n' is received.
 * This function is very simple and data received between cc_tx flag set and
 * cc_ptr rewind to 0 may be lost. */
static volatile uint32_t cc_tx = 0;
static volatile uint8_t cc_tx_buff[RF_BUFF_LEN];
static volatile uint8_t cc_ptr = 0;

/***************************************************************************** */
void handle_uart_cmd(uint8_t c)
{
	cc_tx_buff[cc_ptr++] = c;
	
	if (cc_ptr >= 8) {
		cc_tx = 1;
	}
}

void encrypt(uint8_t* tx_data, uint8_t tx_len)
{
	for (uint8_t i = 0; i < tx_len; i++) {
		tx_data[i] ^= ENCRYPTION_KEY_1;
		tx_data[i] ^= ENCRYPTION_KEY_2;
	}
}

void compute_checksum(uint8_t checksum[2], uint8_t* data, uint8_t length) {
	checksum[0] = 0;
	checksum[1] = 0;

	for (uint8_t i = 0; i < length; i++) {
		checksum[0] += data[i];
		checksum[1] += data[i] * (i + 1);
	}
}

void send_uart_to_rf(void)
{
	uint8_t cc_tx_data[RF_BUFF_LEN + 4];
	uint8_t tx_len = cc_ptr;
	int ret = 0;

	/* Create a local copy */
	memcpy((char*) &(cc_tx_data[2]), (char*) cc_tx_buff, tx_len);

	/* "Free" the rx buffer as soon as possible */
	cc_ptr = 0;

	/* Prepare buffer for sending */
	cc_tx_data[0] = tx_len + 4;
	cc_tx_data[1] = GATEWAY_ADDRESS;

	/* Compute checksum */
	uint8_t checksum[2];

	compute_checksum(checksum, cc_tx_data, tx_len + 2);

	cc_tx_data[tx_len + 2] = checksum[0];
	cc_tx_data[tx_len + 3] = checksum[1];

	#ifdef DEBUG
	for (uint8_t i = 0; i < tx_len + 4; i++) {
		uprintf(UART0, "%02X ", cc_tx_data[i]);
	}
	uprintf(UART0, "\n");
	#endif

	/* Encrypt the message */
	encrypt(&cc_tx_data[2], tx_len + 4);

	/* Send */
	if (cc1101_tx_fifo_state() != 0) {
		cc1101_flush_tx_fifo();
	}

	ret = cc1101_send_packet(cc_tx_data, (tx_len + 4));

#ifdef DEBUG
	uprintf(UART0, "Tx ret: %d\n", ret);
#endif
}


int main(void)
{
	system_init();
	uart_on(UART0, 115200, handle_uart_cmd);
	ssp_master_on(0, LPC_SSP_FRAME_SPI, 8, 4*1000*1000); /* bus_num, frame_type, data_width, rate */

	/* Radio */
	rf_config();

	uprintf(UART0, "App started\n\r");

	while (1) {
		chenillard(250);

		if (cc_tx == 1) {
			cc_tx = 0;

			send_uart_to_rf();
		}
	}
	return 0;
}
