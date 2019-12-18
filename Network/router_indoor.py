#!/usr/bin/env python3

###################################################
# Network Automation Template Configurations
# Author: Pierre-Louis Binard
###################################################

from jinja2 import Environment, FileSystemLoader

#Use the current directory
file_loader = FileSystemLoader('.')

#Inputs use with template j2
hostname = input("Enter the hostname: ")
port = input("Enter the connection port: ")
nb_ro = input("Nombre d'interface :")

list_int = {}

for i in range(int(nb_ro)) :
    print("Configuration de linterface n°",i)
    interface  = input("Number of interface :")
    ip = input("Enter the @IP :")
    subnet = input("Enter the subnet :")
    list_int ["interface"] = interface
    list_int ["ip"] = ip
    list_int["subnet"] = subnet


router = {
    "address": "127.0.0.1"
}

# Load the environment
#env = Environment(loader=file_loader)
#template = env.get_template('bgp_template.j2')
#output = template.render(local_asn='1111', bgp_neighbor='192.168.1.1', remote_asn='2222')
#Print the output
print(list_int)