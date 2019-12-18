#!/usr/bin/env python3

from jinja2 import Template
from netmiko import ConnectHandler



hostname = input("Enter the hostname: ")
port = input("Enter the port de connection: ")

# Host to connect to the device
device = {

             "port": "{{port}}",
            }



template = Template("Le port est {{ port }} ")
router = template.render(port=port)

# Use Netmiko to connect to the device and send the configuration
#with ConnectHandler (**device)



print(router)