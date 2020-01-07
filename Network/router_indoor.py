#!/usr/bin/env python3
# -*- coding: utf-8 -*-

###################################################
# Network Automation Template Configurations
# Author: Pierre-Louis Binard
###################################################

import template as template
from jinja2 import Environment, FileSystemLoader

# Use the current directory
file_loader = FileSystemLoader('.')

# Inputs uses with template j2
hostname = input("Enter the hostname: ")
#port = input("Enter the connection port: ")
nb_ro = input("Nombre d'interface: ")

# List of interfaces
list_int = []

# Ask to the user the number of interfaces
for i in range(int(nb_ro)) :
    interfaces = {}
    print("Configuration de l'interface n°", i)
    interface = input("Number of interface: ")
    ip = input("Enter the @IP: ")
    subnet = input("Enter the subnet: ")
    interfaces["interface"] = interface
    interfaces["ip"] = ip
    interfaces["subnet"] = subnet
    list_int.append(interfaces)

print("Fin de configuration des interfaces")
print("-----------------------------------\n")

# OSPF Configuration
num_opsf = input("Enter the number of OSPF process: ")
router_id = input("Enter the router-id: ")
nb_ospf = input("Enter the number of OSPF route: ")

# List with network, wilcard mask and area
list_ospf = []

for i in range(int(nb_ospf)):
    ospf = {}
    network_ospf = input("Enter the network for OSPF: ")
    wilcard_mask = input("Enter the wilcard mask: ")
    area = input("Enter the area: ")
    ospf["network"] = network_ospf
    ospf["wilcard"] = wilcard_mask
    ospf["area"] = area
    list_ospf.append(ospf)

print("Fin de configuration de l'OSPF ", num_opsf)
print("-----------------------------------\n")

# BGP configuration
local_asn = input("Enter the local asn: ")
nb_bgp = int(input("Enter the number of BGP neighbor: "))

# Create loopback for BGP if exists
loopback = ""

# List with remote asn and neigbhor
list_bgp = []

for i in range(nb_bgp):
    bgp = {}
    bgp_neighbor = input("Enter the BGP neighbor: ")
    remote_asn = input("Enter the remote asn: ")
    sp_bgp_temp = bgp_neighbor.split(".")
    sp_bgp = [sp_bgp_temp[0], sp_bgp_temp[1]]
    if sp_bgp[0] == "192" and sp_bgp[1] == "168":
        bgp_neighbor_loop = bgp_neighbor
        # Afin d'eviter de demander à l'utilisateur la loopback deja demander precedemment on la récupere
        for data in list_int:
            if "lo" in data['interface'] or "loopback" in data['interface'] :
                loopback = data["interface"]
    if "lo" in loopback:
        test_loop = True
    else:
        test_loop = False
    bgp["bgp_neighbor"] = bgp_neighbor
    bgp["remote_asn"] = remote_asn
    bgp["test_loop"] = test_loop
    list_bgp.append(bgp)
# If loopback exists set True and the template add 2 commands into the configuration

print( "End of configuration" )
print( "**************************\n" )

"""
-------------------------------------END OF INPUT-------------------------------------
"""
# Load the environment
env = Environment(loader=file_loader)
template = env.get_template('test_tpl.j2')

output = template.render(hostname = hostname,
                         interfaces=list_int,
                         num_opsf =num_opsf,
                         router_id = router_id,
                         opsfs=list_ospf,
                         local_asn = local_asn,
                         bgps=list_bgp,
                         loopback=loopback)

print(output)

# Save the final configuraiton to a file
with open("config.cfg", "w") as f:
    f.write(output)