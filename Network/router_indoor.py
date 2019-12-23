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
nb_ro = input("Nombre d'interface: ")

list_int = []

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
"""
loopback = "0"

print("Fin de configuration des interfaces")
print("-----------------------------------\n")
num_opsf = input("Enter the number of OSPF process: ")
router_id = input("Enter the router-id: ")
nb_ospf = input("Enter the number of OSPF route: ")

list_ospf = {}

for i in range(int(nb_ospf)) :
    network_ospf = input("Enter the network for OSPF:")
    wilcard_mask = input("Enter the wilcard mask : ")
    area = input("Enter the area:")
    list_ospf["network"] = network_ospf
    list_ospf["wilcard"] = wilcard_mask
    list_ospf["area"] = area

print("Fin de configuration de l'OSPF", num_opsf)
print("-----------------------------------\n")

# BGP configuration
local_asn = input("Enter the local asn: ")
nb_bgp = int(input("Enter the number of BGP neighbor: "))

list_bgp = {}

for i in range(nb_bgp):
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
                print(loopback)
#for data in list_int:
    #print(data['interface'])
"""
router = {
    "address": "127.0.0.1",
    "port": "{{port}}"
}

# Load the environment
env = Environment(loader=file_loader)
template = env.get_template('bgp_template.j2')
"""
if "lo" in loopback :
    test_loop = True
else :
    test_loop = False
"""
#Print the list bgp_template.j2
print( "Fin" )
print( "**************************\n" )

output = template.render(hostname=hostname, int = list_int)
#, num_opsf = num_opsf, router_id = router_id, local_asn = local_asn, loopback = loopback, test_loop = test_loop)
print(output)


